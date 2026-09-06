import 'enums.dart';

final class AppSettings {
  const AppSettings({
    this.theme = AppThemePreference.system,
    this.textScale = AppTextScale.medium,
    this.autoLockTimeout = AutoLockTimeout.oneMinute,
    this.appLockEnabled = false,
    this.patternEnabled = false,
    this.biometricEnabled = false,
  });

  final AppThemePreference theme;
  final AppTextScale textScale;
  final AutoLockTimeout autoLockTimeout;
  final bool appLockEnabled;
  final bool patternEnabled;
  final bool biometricEnabled;

  bool get hasLocalUnlockMethod =>
      appLockEnabled || patternEnabled || biometricEnabled;

  AppSettings copyWith({
    AppThemePreference? theme,
    AppTextScale? textScale,
    AutoLockTimeout? autoLockTimeout,
    bool? appLockEnabled,
    bool? patternEnabled,
    bool? biometricEnabled,
  }) => AppSettings(
    theme: theme ?? this.theme,
    textScale: textScale ?? this.textScale,
    autoLockTimeout: autoLockTimeout ?? this.autoLockTimeout,
    appLockEnabled: appLockEnabled ?? this.appLockEnabled,
    patternEnabled: patternEnabled ?? this.patternEnabled,
    biometricEnabled: biometricEnabled ?? this.biometricEnabled,
  );

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is AppSettings &&
          theme == other.theme &&
          textScale == other.textScale &&
          autoLockTimeout == other.autoLockTimeout &&
          appLockEnabled == other.appLockEnabled &&
          patternEnabled == other.patternEnabled &&
          biometricEnabled == other.biometricEnabled;

  @override
  int get hashCode => Object.hash(
    theme,
    textScale,
    autoLockTimeout,
    appLockEnabled,
    patternEnabled,
    biometricEnabled,
  );
}
