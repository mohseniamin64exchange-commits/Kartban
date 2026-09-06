import 'dart:convert';
import 'dart:math';
import 'dart:typed_data';

import 'package:cryptography/cryptography.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:local_auth/local_auth.dart';

import 'security_channel.dart';

enum AppLockMethod { password, pattern }

enum AppLockResult { authenticated, cancelled, unavailable, failed }

final class AppLockSettings {
  const AppLockSettings({
    required this.enabled,
    required this.biometricEnabled,
    required this.lockMethod,
    required this.autoLockDelay,
  });

  const AppLockSettings.disabled()
    : enabled = false,
      biometricEnabled = false,
      lockMethod = null,
      autoLockDelay = Duration.zero;

  final bool enabled;
  final bool biometricEnabled;
  final AppLockMethod? lockMethod;
  final Duration autoLockDelay;
}

abstract interface class AppLockStore {
  Future<AppLockSettings> readSettings();

  Future<void> writeSettings(AppLockSettings settings);

  Future<void> saveVerifier({required Uint8List salt, required Uint8List hash});

  Future<({Uint8List salt, Uint8List hash})?> readVerifier();

  Future<void> clearVerifier();
}

final class SecureStorageAppLockStore implements AppLockStore {
  SecureStorageAppLockStore({FlutterSecureStorage? storage})
    : _storage = storage ?? const FlutterSecureStorage();

  static const _androidOptions = AndroidOptions(
    resetOnError: false,
    migrateOnAlgorithmChange: true,
    preferencesKeyPrefix: 'kartyar_security',
    sharedPreferencesName: 'kartyar_app_lock',
  );

  static const _enabledKey = 'security.lock.enabled.v1';
  static const _biometricKey = 'security.lock.biometric.v1';
  static const _methodKey = 'security.lock.method.v1';
  static const _delayKey = 'security.lock.delay_seconds.v1';
  static const _saltKey = 'security.lock.salt.v1';
  static const _hashKey = 'security.lock.hash.v1';

  final FlutterSecureStorage _storage;

  @override
  Future<AppLockSettings> readSettings() async {
    final values = await _storage.readAll(aOptions: _androidOptions);
    final methodName = values[_methodKey];
    return AppLockSettings(
      enabled: values[_enabledKey] == 'true',
      biometricEnabled: values[_biometricKey] == 'true',
      lockMethod: AppLockMethod.values
          .where((value) => value.name == methodName)
          .firstOrNull,
      autoLockDelay: Duration(
        seconds: int.tryParse(values[_delayKey] ?? '') ?? 0,
      ),
    );
  }

  @override
  Future<void> writeSettings(AppLockSettings settings) async {
    await Future.wait([
      _write(_enabledKey, settings.enabled.toString()),
      _write(_biometricKey, settings.biometricEnabled.toString()),
      _write(_methodKey, settings.lockMethod?.name),
      _write(_delayKey, settings.autoLockDelay.inSeconds.toString()),
    ]);
  }

  @override
  Future<void> saveVerifier({
    required Uint8List salt,
    required Uint8List hash,
  }) async {
    await Future.wait([
      _write(_saltKey, base64UrlEncode(salt)),
      _write(_hashKey, base64UrlEncode(hash)),
    ]);
  }

  @override
  Future<({Uint8List salt, Uint8List hash})?> readVerifier() async {
    final values = await Future.wait([
      _storage.read(key: _saltKey, aOptions: _androidOptions),
      _storage.read(key: _hashKey, aOptions: _androidOptions),
    ]);
    if (values[0] == null || values[1] == null) return null;
    try {
      return (
        salt: base64Url.decode(values[0]!),
        hash: base64Url.decode(values[1]!),
      );
    } on FormatException {
      return null;
    }
  }

  @override
  Future<void> clearVerifier() async {
    await Future.wait([
      _storage.delete(key: _saltKey, aOptions: _androidOptions),
      _storage.delete(key: _hashKey, aOptions: _androidOptions),
    ]);
  }

  Future<void> _write(String key, String? value) {
    if (value == null) {
      return _storage.delete(key: key, aOptions: _androidOptions);
    }
    return _storage.write(key: key, value: value, aOptions: _androidOptions);
  }
}

final class AppLockController extends ChangeNotifier {
  AppLockController({
    AppLockStore? store,
    LocalAuthentication? localAuthentication,
    SecurityChannel? securityChannel,
  }) : _store = store ?? SecureStorageAppLockStore(),
       _localAuthentication = localAuthentication ?? LocalAuthentication(),
       _securityChannel = securityChannel ?? SecurityChannel();

  static const _saltLength = 16;
  static final _pbkdf2 = Pbkdf2(
    macAlgorithm: Hmac.sha256(),
    iterations: 210000,
    bits: 256,
  );

  final AppLockStore _store;
  final LocalAuthentication _localAuthentication;
  final SecurityChannel _securityChannel;

  AppLockSettings _settings = const AppLockSettings.disabled();
  bool _isLocked = true;
  bool _sensitiveViewVisible = false;
  DateTime? _backgroundedAt;
  int _sensitiveContentRevision = 0;

  AppLockSettings get settings => _settings;
  bool get isLocked => _settings.enabled && _isLocked;
  int get sensitiveContentRevision => _sensitiveContentRevision;

  Future<void> initialize({required bool hasSensitiveCards}) async {
    _settings = await _store.readSettings();
    if (hasSensitiveCards && !_settings.enabled) {
      _settings = AppLockSettings(
        enabled: true,
        biometricEnabled: false,
        lockMethod: _settings.lockMethod,
        autoLockDelay: Duration.zero,
      );
      await _store.writeSettings(_settings);
    }
    _isLocked = _settings.enabled;
    await _securityChannel.setSecureScreen(isLocked);
    notifyListeners();
  }

  Future<void> configureSecret({
    required String secret,
    required AppLockMethod method,
    required bool biometricEnabled,
    required Duration autoLockDelay,
  }) async {
    _validateSecret(secret, method);
    final salt = _randomBytes(_saltLength);
    final hash = await _derive(secret, salt);
    try {
      await _store.saveVerifier(salt: salt, hash: hash);
    } finally {
      hash.fillRange(0, hash.length, 0);
    }
    _settings = AppLockSettings(
      enabled: true,
      biometricEnabled: biometricEnabled,
      lockMethod: method,
      autoLockDelay: autoLockDelay,
    );
    await _store.writeSettings(_settings);
    _isLocked = false;
    await _securityChannel.setSecureScreen(false);
    notifyListeners();
  }

  Future<AppLockResult> unlockWithSecret(String secret) async {
    final verifier = await _store.readVerifier();
    if (verifier == null) return AppLockResult.unavailable;
    final candidate = await _derive(secret, verifier.salt);
    final matches = _constantTimeEquals(candidate, verifier.hash);
    candidate.fillRange(0, candidate.length, 0);
    if (!matches) return AppLockResult.failed;
    await _finishUnlock();
    return AppLockResult.authenticated;
  }

  Future<AppLockResult> unlockWithBiometrics() async {
    if (!_settings.biometricEnabled) return AppLockResult.unavailable;
    try {
      final supported = await _localAuthentication.isDeviceSupported();
      final canCheck = await _localAuthentication.canCheckBiometrics;
      if (!supported || !canCheck) return AppLockResult.unavailable;
      final authenticated = await _localAuthentication.authenticate(
        localizedReason: 'برای باز کردن امن کارتیار، هویت خود را تأیید کنید',
        biometricOnly: true,
        persistAcrossBackgrounding: true,
        sensitiveTransaction: true,
      );
      if (!authenticated) return AppLockResult.cancelled;
      await _finishUnlock();
      return AppLockResult.authenticated;
    } on LocalAuthException {
      return AppLockResult.failed;
    }
  }

  Future<void> lockNow() async {
    _isLocked = true;
    _hideSensitiveContent();
    await _securityChannel.setSecureScreen(true);
    notifyListeners();
  }

  Future<void> concealForBackground(DateTime now) async {
    _backgroundedAt ??= now;
    _hideSensitiveContent();
    if (_settings.enabled && _settings.autoLockDelay == Duration.zero) {
      _isLocked = true;
    }
    await _securityChannel.setSecureScreen(true);
    notifyListeners();
  }

  Future<void> resumeFromBackground(DateTime now) async {
    final backgroundedAt = _backgroundedAt;
    _backgroundedAt = null;
    if (_settings.enabled &&
        backgroundedAt != null &&
        now.difference(backgroundedAt) >= _settings.autoLockDelay) {
      _isLocked = true;
    }
    await _securityChannel.setSecureScreen(isLocked);
    notifyListeners();
  }

  Future<void> setSensitiveViewVisible(bool visible) async {
    if (visible && isLocked) {
      throw StateError('Authentication is required before sensitive display.');
    }
    _sensitiveViewVisible = visible;
    if (!visible) _hideSensitiveContent();
    await _securityChannel.setSecureScreen(visible || isLocked);
    notifyListeners();
  }

  Future<void> disable({required bool hasSensitiveCards}) async {
    if (hasSensitiveCards) {
      throw StateError('App lock is required while sensitive cards exist.');
    }
    await _store.clearVerifier();
    _settings = const AppLockSettings.disabled();
    _isLocked = false;
    _hideSensitiveContent();
    await _store.writeSettings(_settings);
    await _securityChannel.setSecureScreen(false);
    notifyListeners();
  }

  Future<void> _finishUnlock() async {
    _isLocked = false;
    await _securityChannel.setSecureScreen(_sensitiveViewVisible);
    notifyListeners();
  }

  void _hideSensitiveContent() {
    if (_sensitiveViewVisible) {
      _sensitiveViewVisible = false;
      _sensitiveContentRevision++;
    }
  }

  static void _validateSecret(String secret, AppLockMethod method) {
    final minimum = method == AppLockMethod.password ? 6 : 4;
    if (secret.length < minimum) {
      throw ArgumentError.value(
        secret.length,
        'secret',
        'Secret is too short.',
      );
    }
  }

  static Future<Uint8List> _derive(String secret, Uint8List salt) async {
    final key = await _pbkdf2.deriveKeyFromPassword(
      password: secret,
      nonce: salt,
    );
    return Uint8List.fromList(await key.extractBytes());
  }

  static Uint8List _randomBytes(int length) {
    final random = Random.secure();
    return Uint8List.fromList(
      List<int>.generate(length, (_) => random.nextInt(256)),
    );
  }

  static bool _constantTimeEquals(Uint8List left, Uint8List right) {
    if (left.length != right.length) return false;
    var difference = 0;
    for (var index = 0; index < left.length; index++) {
      difference |= left[index] ^ right[index];
    }
    return difference == 0;
  }
}
