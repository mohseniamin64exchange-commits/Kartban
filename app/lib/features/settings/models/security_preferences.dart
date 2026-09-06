import 'package:flutter/foundation.dart';

enum AppLockMethod {
  password('رمز عبور'),
  pattern('الگو');

  const AppLockMethod(this.label);
  final String label;
}

enum AutoLockDelay {
  immediately(Duration.zero, 'فوری'),
  thirtySeconds(Duration(seconds: 30), '۳۰ ثانیه'),
  oneMinute(Duration(minutes: 1), '۱ دقیقه'),
  fiveMinutes(Duration(minutes: 5), '۵ دقیقه'),
  onAppClose(null, 'با بستن برنامه');

  const AutoLockDelay(this.duration, this.label);
  final Duration? duration;
  final String label;
}

@immutable
class SecurityPreferences {
  const SecurityPreferences({
    this.appLockEnabled = false,
    this.method = AppLockMethod.password,
    this.biometricEnabled = false,
    this.autoLockDelay = AutoLockDelay.oneMinute,
    this.biometricAvailable = false,
  });

  final bool appLockEnabled;
  final AppLockMethod method;
  final bool biometricEnabled;
  final AutoLockDelay autoLockDelay;
  final bool biometricAvailable;

  SecurityPreferences copyWith({
    bool? appLockEnabled,
    AppLockMethod? method,
    bool? biometricEnabled,
    AutoLockDelay? autoLockDelay,
    bool? biometricAvailable,
  }) => SecurityPreferences(
    appLockEnabled: appLockEnabled ?? this.appLockEnabled,
    method: method ?? this.method,
    biometricEnabled: biometricEnabled ?? this.biometricEnabled,
    autoLockDelay: autoLockDelay ?? this.autoLockDelay,
    biometricAvailable: biometricAvailable ?? this.biometricAvailable,
  );
}
