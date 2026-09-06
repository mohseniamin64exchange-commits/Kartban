enum AppThemeMode { system, light, dark }

enum AppTextSize { small, medium, large }

enum LockMethod { password, pattern, biometric }

enum AutoLockDelay { immediate, thirtySeconds, oneMinute, fiveMinutes, onClose }

class AppPreferences {
  const AppPreferences({
    this.themeMode = AppThemeMode.system,
    this.textSize = AppTextSize.medium,
    this.lockEnabled = false,
    this.biometricEnabled = false,
    this.autoLockDelay = AutoLockDelay.immediate,
    this.clipboardClearSeconds = 30,
  });
  final AppThemeMode themeMode;
  final AppTextSize textSize;
  final bool lockEnabled;
  final bool biometricEnabled;
  final AutoLockDelay autoLockDelay;
  final int clipboardClearSeconds;
}
