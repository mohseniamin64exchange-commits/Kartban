import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:local_auth/local_auth.dart';

import 'core/design/design.dart';
import 'data/local/card_yar_database.dart';
import 'features/backup/application/local_backup_workflow.dart';
import 'features/backup/presentation/backup_restore_screen.dart';
import 'features/cards/models/card_view_data.dart';
import 'features/cards/person_cards_page.dart';
import 'features/home/home_models.dart';
import 'features/home/home_screen.dart';
import 'features/settings/models/display_preferences.dart';
import 'features/settings/models/security_preferences.dart' as settings;
import 'features/settings/presentation/about_screen.dart';
import 'features/settings/presentation/display_settings_screen.dart';
import 'features/settings/presentation/privacy_center_screen.dart';
import 'features/settings/presentation/security_settings_screen.dart';
import 'security/app_lock.dart' as security;
import 'security/secure_lifecycle.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const CardYarBootstrap());
}

class CardYarBootstrap extends StatefulWidget {
  const CardYarBootstrap({super.key});
  @override
  State<CardYarBootstrap> createState() => _CardYarBootstrapState();
}

class _CardYarBootstrapState extends State<CardYarBootstrap> {
  final _navigatorKey = GlobalKey<NavigatorState>();
  final _database = CardYarDatabase();
  final _appLock = security.AppLockController();
  late final SecureLifecycleObserver _lifecycle = SecureLifecycleObserver(
    appLock: _appLock,
  );
  late final LocalBackupWorkflow _backupWorkflow = LocalBackupWorkflow(
    _database,
  );

  DisplayPreferences _display = const DisplayPreferences();
  settings.SecurityPreferences _security = const settings.SecurityPreferences();
  bool _initialized = false;

  @override
  void initState() {
    super.initState();
    _appLock.addListener(_refresh);
    _lifecycle.attach();
    _initialize();
  }

  Future<void> _initialize() async {
    await _database.open();
    await _appLock.initialize(hasSensitiveCards: false);
    final localAuth = LocalAuthentication();
    final biometric =
        await localAuth.isDeviceSupported() &&
        (await localAuth.getAvailableBiometrics()).isNotEmpty;
    if (!mounted) return;
    setState(() {
      _security = _security.copyWith(biometricAvailable: biometric);
      _initialized = true;
    });
  }

  void _refresh() {
    if (mounted) setState(() {});
  }

  @override
  void dispose() {
    _lifecycle.detach();
    _appLock.removeListener(_refresh);
    _database.close();
    super.dispose();
  }

  ThemeMode get _themeMode => switch (_display.brightness) {
    CardYarBrightness.light => ThemeMode.light,
    CardYarBrightness.dark => ThemeMode.dark,
    CardYarBrightness.system => ThemeMode.system,
  };

  KartyarTextScale get _textScale => switch (_display.textSize) {
    CardYarTextSize.small => KartyarTextScale.small,
    CardYarTextSize.medium => KartyarTextScale.medium,
    CardYarTextSize.large => KartyarTextScale.large,
  };

  @override
  Widget build(BuildContext context) => MaterialApp(
    navigatorKey: _navigatorKey,
    debugShowCheckedModeBanner: false,
    title: 'کارت‌یار',
    locale: const Locale('fa'),
    supportedLocales: const [Locale('fa')],
    localizationsDelegates: const [
      GlobalMaterialLocalizations.delegate,
      GlobalWidgetsLocalizations.delegate,
      GlobalCupertinoLocalizations.delegate,
    ],
    theme: KartyarTheme.light(textScale: _textScale),
    darkTheme: KartyarTheme.dark(textScale: _textScale),
    themeMode: _themeMode,
    builder: (context, child) => Directionality(
      textDirection: TextDirection.rtl,
      child: MediaQuery.withClampedTextScaling(
        minScaleFactor: _display.textSize.scale,
        maxScaleFactor: _display.textSize.scale,
        child: Stack(
          children: [
            if (_initialized)
              child ?? const SizedBox.shrink()
            else
              const ColoredBox(
                color: Color(0xFF041B3D),
                child: Center(
                  child: CircularProgressIndicator(color: Colors.white),
                ),
              ),
            if (_appLock.isLocked)
              Positioned.fill(child: _UnlockPage(appLock: _appLock)),
          ],
        ),
      ),
    ),
    home: Builder(builder: (context) => HomeScreen(actions: _actions(context))),
  );

  HomeActions _actions(BuildContext context) => _KartyarActions(
    onOpenPerson: (person) => Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PersonCardsPage(
          personName: person.name,
          cards: _sampleCards(person.name),
          onAuthenticateSensitive: (_) async => _authenticateSensitive(context),
        ),
      ),
    ),
    add: () => _showInfo(context, 'ثبت شخص و کارت از صفحه شخص انجام می‌شود.'),
    lock: () async {
      if (!_security.appLockEnabled) {
        _showInfo(context, 'ابتدا از بخش امنیت، قفل برنامه را فعال کنید.');
      } else {
        await _appLock.lockNow();
      }
    },
    security: () => Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => SecuritySettingsScreen(
          preferences: _security,
          onAppLockChanged: (enabled) => _setLockEnabled(context, enabled),
          onMethodChanged: (method) =>
              setState(() => _security = _security.copyWith(method: method)),
          onBiometricChanged: (enabled) => setState(
            () => _security = _security.copyWith(biometricEnabled: enabled),
          ),
          onAutoLockChanged: (delay) => setState(
            () => _security = _security.copyWith(autoLockDelay: delay),
          ),
          onChangeCredential: () => _configureCredential(context),
          onLockNow: () => _appLock.lockNow(),
        ),
      ),
    ),
    backup: () => Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => BackupRestoreScreen(workflow: _backupWorkflow),
      ),
    ),
    privacy: () => Navigator.of(
      context,
    ).push(MaterialPageRoute(builder: (_) => const PrivacyCenterScreen())),
    display: () => Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => DisplaySettingsScreen(
          preferences: _display,
          onTextSizeChanged: (value) =>
              setState(() => _display = _display.copyWith(textSize: value)),
          onBrightnessChanged: (value) =>
              setState(() => _display = _display.copyWith(brightness: value)),
        ),
      ),
    ),
    about: () => Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => AboutCardYarScreen(
          version: '1.0.0',
          buildNumber: '1',
          onOpenLicenses: () => showLicensePage(
            context: context,
            applicationName: 'کارت‌یار',
            applicationVersion: '1.0.0',
          ),
        ),
      ),
    ),
  );

  List<CardViewData> _sampleCards(String holder) => [
    CardViewData(
      id: 'mellat-1',
      bank: BankBrand.mellat,
      holderName: holder,
      cardNumber: '6104 3378 0000 0018',
      accountNumber: '100001234567',
      iban: 'IR12 0170 0000 0010 0000 0000 01',
      ownership: CardOwnership.customer,
    ),
    CardViewData(
      id: 'melli-1',
      bank: BankBrand.melli,
      holderName: holder,
      cardNumber: '6037 9975 0000 0024',
      accountNumber: '100002345678',
      iban: 'IR22 0170 0000 0010 0000 0000 02',
      ownership: CardOwnership.personal,
      hasEncryptedSensitiveData: true,
    ),
    CardViewData(
      id: 'saderat-1',
      bank: BankBrand.saderat,
      holderName: holder,
      cardNumber: '6037 6975 0000 0031',
      accountNumber: '100003456789',
      iban: 'IR32 0170 0000 0010 0000 0000 03',
      ownership: CardOwnership.customer,
    ),
    CardViewData(
      id: 'tejarat-1',
      bank: BankBrand.tejarat,
      holderName: holder,
      cardNumber: '6273 5375 0000 0066',
      accountNumber: '100004567890',
      iban: 'IR42 0170 0000 0010 0000 0000 04',
      ownership: CardOwnership.customer,
    ),
  ];

  Future<void> _setLockEnabled(BuildContext context, bool enabled) async {
    if (enabled) {
      final secret = await _askSecret(context, 'ساخت رمز برنامه');
      if (secret == null || secret.length < 6) return;
      await _appLock.configureSecret(
        secret: secret,
        method: _security.method == settings.AppLockMethod.password
            ? security.AppLockMethod.password
            : security.AppLockMethod.pattern,
        biometricEnabled: _security.biometricEnabled,
        autoLockDelay:
            _security.autoLockDelay.duration ?? const Duration(days: 3650),
      );
      if (mounted)
        setState(() => _security = _security.copyWith(appLockEnabled: true));
    } else {
      await _appLock.disable(hasSensitiveCards: false);
      if (mounted)
        setState(
          () => _security = _security.copyWith(
            appLockEnabled: false,
            biometricEnabled: false,
          ),
        );
    }
  }

  Future<void> _configureCredential(BuildContext context) async {
    final secret = await _askSecret(context, 'رمز یا الگوی جدید');
    if (secret == null || secret.length < 6) return;
    await _appLock.configureSecret(
      secret: secret,
      method: _security.method == settings.AppLockMethod.password
          ? security.AppLockMethod.password
          : security.AppLockMethod.pattern,
      biometricEnabled: _security.biometricEnabled,
      autoLockDelay:
          _security.autoLockDelay.duration ?? const Duration(days: 3650),
    );
  }

  Future<bool> _authenticateSensitive(BuildContext context) async {
    if (_security.biometricEnabled) {
      final result = await _appLock.unlockWithBiometrics();
      if (result == security.AppLockResult.authenticated) return true;
    }
    if (!context.mounted) return false;
    final secret = await _askSecret(
      context,
      'احراز هویت برای نمایش اطلاعات حساس',
    );
    if (secret == null) return false;
    return await _appLock.unlockWithSecret(secret) ==
        security.AppLockResult.authenticated;
  }

  Future<String?> _askSecret(BuildContext context, String title) async {
    final controller = TextEditingController();
    final result = await showDialog<String>(
      context: context,
      builder: (context) => Directionality(
        textDirection: TextDirection.rtl,
        child: AlertDialog(
          title: Text(title),
          content: TextField(
            controller: controller,
            obscureText: true,
            autofocus: true,
            textAlign: TextAlign.right,
            decoration: const InputDecoration(labelText: 'رمز یا الگو'),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('انصراف'),
            ),
            FilledButton(
              onPressed: () => Navigator.pop(context, controller.text),
              child: const Text('تأیید'),
            ),
          ],
        ),
      ),
    );
    controller.clear();
    controller.dispose();
    return result;
  }

  void _showInfo(BuildContext context, String text) =>
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(text)));
}

class _KartyarActions implements HomeActions {
  const _KartyarActions({
    required this.onOpenPerson,
    required this.add,
    required this.lock,
    required this.security,
    required this.backup,
    required this.privacy,
    required this.display,
    required this.about,
  });
  final ValueChanged<HomePersonSummary> onOpenPerson;
  final VoidCallback add, lock, security, backup, privacy, display, about;
  @override
  void openPerson(HomePersonSummary person) => onOpenPerson(person);
  @override
  void addPersonOrCard() => add();
  @override
  void lockNow() => lock();
  @override
  void openSecurity() => security();
  @override
  void openBackup() => backup();
  @override
  void openPrivacyCenter() => privacy();
  @override
  void openDisplaySettings() => display();
  @override
  void openAbout() => about();
}

class _UnlockPage extends StatefulWidget {
  const _UnlockPage({required this.appLock});
  final security.AppLockController appLock;
  @override
  State<_UnlockPage> createState() => _UnlockPageState();
}

class _UnlockPageState extends State<_UnlockPage> {
  final _controller = TextEditingController();
  String? _error;
  bool _busy = false;
  @override
  void dispose() {
    _controller.clear();
    _controller.dispose();
    super.dispose();
  }

  Future<void> _unlockSecret() async {
    setState(() {
      _busy = true;
      _error = null;
    });
    final result = await widget.appLock.unlockWithSecret(_controller.text);
    _controller.clear();
    if (!mounted) return;
    setState(() {
      _busy = false;
      if (result != security.AppLockResult.authenticated)
        _error = 'رمز یا الگو صحیح نیست.';
    });
  }

  @override
  Widget build(BuildContext context) => Material(
    color: const Color(0xFF041B3D),
    child: SafeArea(
      child: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(28),
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 380),
            child: Column(
              children: [
                const Icon(Icons.wallet_rounded, color: Colors.white, size: 64),
                const SizedBox(height: 12),
                const Text(
                  'کارت‌یار قفل است',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 25,
                    fontWeight: FontWeight.w900,
                  ),
                ),
                const SizedBox(height: 24),
                TextField(
                  controller: _controller,
                  obscureText: true,
                  textAlign: TextAlign.right,
                  onSubmitted: (_) => _unlockSecret(),
                  decoration: InputDecoration(
                    labelText: 'رمز یا الگو',
                    errorText: _error,
                  ),
                ),
                const SizedBox(height: 12),
                SizedBox(
                  width: double.infinity,
                  child: FilledButton(
                    onPressed: _busy ? null : _unlockSecret,
                    child: _busy
                        ? const CircularProgressIndicator()
                        : const Text('باز کردن'),
                  ),
                ),
                const SizedBox(height: 8),
                TextButton.icon(
                  onPressed: () => widget.appLock.unlockWithBiometrics(),
                  icon: const Icon(Icons.fingerprint_rounded),
                  label: const Text('اثر انگشت'),
                ),
              ],
            ),
          ),
        ),
      ),
    ),
  );
}
