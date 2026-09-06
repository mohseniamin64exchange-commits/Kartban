import 'package:flutter/foundation.dart';

enum BankBrand { mellat, melli, saderat, tejarat }

enum CardOwnership { personal, customer }

@immutable
class CardViewData {
  const CardViewData({
    required this.id,
    required this.bank,
    required this.holderName,
    required this.cardNumber,
    required this.accountNumber,
    required this.iban,
    required this.ownership,
    this.hasEncryptedSensitiveData = false,
  });
  final String id;
  final BankBrand bank;
  final String holderName;
  final String cardNumber;
  final String accountNumber;
  final String iban;
  final CardOwnership ownership;
  final bool hasEncryptedSensitiveData;

  String get bankName => switch (bank) {
    BankBrand.mellat => 'بانک ملت',
    BankBrand.melli => 'بانک ملی ایران',
    BankBrand.saderat => 'بانک صادرات ایران',
    BankBrand.tejarat => 'بانک تجارت',
  };
  String get bankLatinName => switch (bank) {
    BankBrand.mellat => 'BANK MELLAT',
    BankBrand.melli => 'BANK MELLI IRAN',
    BankBrand.saderat => 'BANK SADERAT IRAN',
    BankBrand.tejarat => 'BANK TEJARAT',
  };
}

@immutable
class CardEditDraft {
  const CardEditDraft({
    required this.bank,
    required this.cardNumber,
    required this.accountNumber,
    required this.iban,
    required this.ownership,
    this.cvv2,
    this.expiry,
  });
  final BankBrand bank;
  final String cardNumber;
  final String accountNumber;
  final String iban;
  final CardOwnership ownership;
  final String? cvv2;
  final String? expiry;

  @override
  String toString() =>
      'CardEditDraft(bank: $bank, ownership: $ownership, sensitive: [REDACTED])';
}
