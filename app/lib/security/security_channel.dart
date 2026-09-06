import 'dart:typed_data';

import 'package:flutter/services.dart';

import 'security_exception.dart';

final class EncryptedPayload {
  const EncryptedPayload({
    required this.ciphertext,
    required this.iv,
    required this.version,
  });

  final Uint8List ciphertext;
  final Uint8List iv;
  final int version;
}

final class SecurityChannel {
  SecurityChannel({MethodChannel? channel})
    : _channel = channel ?? const MethodChannel(_channelName);

  static const _channelName = 'ir.kartyar/security';
  final MethodChannel _channel;

  Future<void> setSecureScreen(bool enabled) async {
    await _invoke<void>('setSecureScreen', {'enabled': enabled});
  }

  Future<bool> isDeviceSecure() async {
    return await _invoke<bool>('isDeviceSecure') ?? false;
  }

  Future<void> ensurePersonalCardKey({required bool requireAuthentication}) {
    return _invoke<void>('ensurePersonalCardKey', {
      'requireAuthentication': requireAuthentication,
    });
  }

  Future<void> deletePersonalCardKey() {
    return _invoke<void>('deletePersonalCardKey');
  }

  Future<EncryptedPayload> encryptPersonalCardSecret({
    required bool isPersonalCard,
    required Uint8List plaintext,
    Uint8List? associatedData,
  }) async {
    _requirePersonalCard(isPersonalCard);
    if (plaintext.isEmpty) {
      throw const SecurityException(
        'empty_sensitive_data',
        'اطلاعات حساس خالی است.',
      );
    }

    final result =
        await _invoke<Map<Object?, Object?>>('encryptPersonalCardSecret', {
          'isPersonalCard': true,
          'plaintext': plaintext,
          if (associatedData != null) 'associatedData': associatedData,
        });
    if (result == null) {
      throw const SecurityException(
        'invalid_native_response',
        'پاسخ رمزنگاری معتبر نیست.',
      );
    }
    return EncryptedPayload(
      ciphertext: _requiredBytes(result, 'ciphertext'),
      iv: _requiredBytes(result, 'iv'),
      version: (result['version'] as num?)?.toInt() ?? 1,
    );
  }

  Future<Uint8List> decryptPersonalCardSecret({
    required bool isPersonalCard,
    required EncryptedPayload payload,
    Uint8List? associatedData,
  }) async {
    _requirePersonalCard(isPersonalCard);
    final result = await _invoke<Uint8List>('decryptPersonalCardSecret', {
      'isPersonalCard': true,
      'ciphertext': payload.ciphertext,
      'iv': payload.iv,
      if (associatedData != null) 'associatedData': associatedData,
    });
    if (result == null) {
      throw const SecurityException(
        'invalid_native_response',
        'پاسخ رمزگشایی معتبر نیست.',
      );
    }
    return result;
  }

  Future<T?> _invoke<T>(
    String method, [
    Map<String, Object?>? arguments,
  ]) async {
    try {
      return await _channel.invokeMethod<T>(method, arguments);
    } on PlatformException catch (error) {
      throw SecurityException(
        error.code,
        error.message ?? 'عملیات امنیتی انجام نشد.',
      );
    }
  }

  static void _requirePersonalCard(bool isPersonalCard) {
    if (!isPersonalCard) {
      throw const SecurityException(
        'customer_sensitive_data_rejected',
        'CVV2 و تاریخ انقضا برای کارت مشتری ذخیره نمی‌شود.',
      );
    }
  }

  static Uint8List _requiredBytes(Map<Object?, Object?> map, String key) {
    final value = map[key];
    if (value is Uint8List) return value;
    throw SecurityException('invalid_native_response', 'فیلد $key معتبر نیست.');
  }
}
