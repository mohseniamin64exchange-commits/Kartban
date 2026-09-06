import 'dart:async';

import 'package:flutter/widgets.dart';

import 'app_lock.dart';

final class SecureLifecycleObserver with WidgetsBindingObserver {
  SecureLifecycleObserver({required AppLockController appLock})
    : _appLock = appLock;

  final AppLockController _appLock;

  void attach() => WidgetsBinding.instance.addObserver(this);

  void detach() => WidgetsBinding.instance.removeObserver(this);

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.hidden:
      case AppLifecycleState.inactive:
      case AppLifecycleState.paused:
      case AppLifecycleState.detached:
        unawaited(_appLock.concealForBackground(DateTime.now()));
      case AppLifecycleState.resumed:
        unawaited(_appLock.resumeFromBackground(DateTime.now()));
    }
  }
}
