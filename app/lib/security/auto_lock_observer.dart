import 'dart:async';
import 'package:flutter/widgets.dart';

class AutoLockObserver with WidgetsBindingObserver {
  AutoLockObserver({required this.onLock, required this.delay});
  final VoidCallback onLock;
  final Duration? delay;
  Timer? _timer;

  void start() => WidgetsBinding.instance.addObserver(this);
  void dispose() {
    _timer?.cancel();
    WidgetsBinding.instance.removeObserver(this);
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      _timer?.cancel();
      return;
    }
    if (state == AppLifecycleState.inactive ||
        state == AppLifecycleState.paused ||
        state == AppLifecycleState.hidden) {
      _timer?.cancel();
      if (delay == null) return;
      if (delay == Duration.zero) {
        onLock();
      } else {
        _timer = Timer(delay!, onLock);
      }
    }
  }
}
