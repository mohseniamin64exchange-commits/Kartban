import 'dart:convert';
import 'dart:math';
import 'package:cryptography/cryptography.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:local_auth/local_auth.dart';

class AppLockService {
  AppLockService({
    LocalAuthentication? localAuthentication,
    FlutterSecureStorage? storage,
  }) : _auth = localAuthentication ?? LocalAuthentication(),
       _storage = storage ?? const FlutterSecureStorage();

  static const _hashKey = 'app_lock_hash_v1';
  static const _saltKey = 'app_lock_salt_v1';
  final LocalAuthentication _auth;
  final FlutterSecureStorage _storage;
  final _kdf = Pbkdf2(
    macAlgorithm: Hmac.sha256(),
    iterations: 120000,
    bits: 256,
  );

  Future<bool> get hasSecret async =>
      (await _storage.read(key: _hashKey)) != null;
  Future<bool> get biometricAvailable async =>
      await _auth.isDeviceSupported() &&
      (await _auth.getAvailableBiometrics()).isNotEmpty;

  Future<void> setSecret(String value) async {
    if (value.length < 4)
      throw ArgumentError('رمز یا الگو باید حداقل ۴ نویسه باشد.');
    final salt = List<int>.generate(16, (_) => Random.secure().nextInt(256));
    final key = await _kdf.deriveKey(
      secretKey: SecretKey(utf8.encode(value)),
      nonce: salt,
    );
    await _storage.write(key: _saltKey, value: base64Encode(salt));
    await _storage.write(
      key: _hashKey,
      value: base64Encode(await key.extractBytes()),
    );
  }

  Future<bool> verifySecret(String value) async {
    final saltText = await _storage.read(key: _saltKey);
    final expectedText = await _storage.read(key: _hashKey);
    if (saltText == null || expectedText == null) return false;
    final key = await _kdf.deriveKey(
      secretKey: SecretKey(utf8.encode(value)),
      nonce: base64Decode(saltText),
    );
    return _constantTimeEquals(
      await key.extractBytes(),
      base64Decode(expectedText),
    );
  }

  Future<bool> authenticateBiometric() => _auth.authenticate(
    localizedReason: 'برای بازکردن کارت‌یار هویت خود را تأیید کنید',
    biometricOnly: true,
    sensitiveTransaction: true,
    persistAcrossBackgrounding: true,
  );

  Future<void> changeSecret(String current, String replacement) async {
    if (!await verifySecret(current)) throw StateError('رمز فعلی صحیح نیست.');
    await setSecret(replacement);
  }

  bool _constantTimeEquals(List<int> left, List<int> right) {
    if (left.length != right.length) return false;
    var result = 0;
    for (var i = 0; i < left.length; i++) {
      result |= left[i] ^ right[i];
    }
    return result == 0;
  }
}
