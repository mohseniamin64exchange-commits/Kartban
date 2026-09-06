import 'dart:convert';
import 'dart:typed_data';

import 'security_channel.dart';
import 'security_exception.dart';

final class PersonalCardSecret {
  PersonalCardSecret._(this._bytes);

  final Uint8List _bytes;

  factory PersonalCardSecret.fromInput({
    required bool isPersonalCard,
    required String cvv2,
    required String expiry,
  }) {
    if (!isPersonalCard) {
      throw const SecurityException(
        'customer_sensitive_data_rejected',
        'CVV2 و تاریخ انقضا برای کارت مشتری ذخیره نمی‌شود.',
      );
    }
    if (!RegExp(r'^\d{3,4}$').hasMatch(cvv2)) {
      throw const SecurityException('invalid_cvv2', 'CVV2 معتبر نیست.');
    }
    if (!RegExp(r'^\d{2}/\d{2}$').hasMatch(expiry)) {
      throw const SecurityException(
        'invalid_expiry',
        'تاریخ انقضا باید با الگوی سال/ماه وارد شود.',
      );
    }
    return PersonalCardSecret._(
      Uint8List.fromList(utf8.encode('$cvv2\u001f$expiry')),
    );
  }

  Uint8List copyBytes() => Uint8List.fromList(_bytes);

  void dispose() => _bytes.fillRange(0, _bytes.length, 0);
}

final class PersonalCardVault {
  PersonalCardVault({SecurityChannel? channel})
    : _channel = channel ?? SecurityChannel();

  final SecurityChannel _channel;

  Future<void> initializeKey({required bool requireAuthentication}) {
    return _channel.ensurePersonalCardKey(
      requireAuthentication: requireAuthentication,
    );
  }

  Future<EncryptedPayload> seal({
    required bool isPersonalCard,
    required String cardId,
    required PersonalCardSecret secret,
  }) async {
    final plaintext = secret.copyBytes();
    try {
      return await _channel.encryptPersonalCardSecret(
        isPersonalCard: isPersonalCard,
        plaintext: plaintext,
        associatedData: Uint8List.fromList(utf8.encode(cardId)),
      );
    } finally {
      plaintext.fillRange(0, plaintext.length, 0);
      secret.dispose();
    }
  }

  Future<PersonalCardSecretView> open({
    required bool isPersonalCard,
    required String cardId,
    required EncryptedPayload payload,
  }) async {
    final plaintext = await _channel.decryptPersonalCardSecret(
      isPersonalCard: isPersonalCard,
      payload: payload,
      associatedData: Uint8List.fromList(utf8.encode(cardId)),
    );
    try {
      final parts = utf8
          .decode(plaintext, allowMalformed: false)
          .split('\u001f');
      if (parts.length != 2) {
        throw const SecurityException(
          'invalid_sensitive_payload',
          'ساختار داده حساس معتبر نیست.',
        );
      }
      return PersonalCardSecretView(cvv2: parts[0], expiry: parts[1]);
    } finally {
      plaintext.fillRange(0, plaintext.length, 0);
    }
  }
}

final class PersonalCardSecretView {
  const PersonalCardSecretView({required this.cvv2, required this.expiry});

  final String cvv2;
  final String expiry;
}
