import 'package:flutter/services.dart';

class SecureScreenController {
  static const MethodChannel _channel = MethodChannel('com.kartyar/security');

  Future<void> protect() =>
      _channel.invokeMethod<void>('setSecureScreen', true);
  Future<void> release() =>
      _channel.invokeMethod<void>('setSecureScreen', false);
}
