import 'package:flutter/services.dart';

class NativeSensitiveVault {
  static const MethodChannel _channel = MethodChannel('com.kartyar/security');

  Future<String> encrypt(String plaintext) async {
    final encrypted = await _channel.invokeMethod<String>('encrypt', plaintext);
    if (encrypted == null || encrypted.isEmpty) {
      throw StateError('رمزنگاری اطلاعات حساس انجام نشد.');
    }
    return encrypted;
  }

  Future<String> decrypt(String payload) async {
    final decrypted = await _channel.invokeMethod<String>('decrypt', payload);
    if (decrypted == null) throw StateError('رمزگشایی اطلاعات حساس انجام نشد.');
    return decrypted;
  }

  Future<void> deleteKey() => _channel.invokeMethod<void>('deleteKey');
}
