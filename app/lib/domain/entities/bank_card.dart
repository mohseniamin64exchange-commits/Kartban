import 'bank.dart';

enum CardOwnership { personal, customer }

class BankCard {
  const BankCard({
    required this.id,
    required this.personId,
    required this.bank,
    required this.cardNumber,
    required this.iban,
    required this.accountNumber,
    required this.ownership,
    this.encryptedSensitivePayload,
    this.createdAt,
  });
  final int? id;
  final int personId;
  final IranianBank bank;
  final String cardNumber;
  final String iban;
  final String accountNumber;
  final CardOwnership ownership;
  final String? encryptedSensitivePayload;
  final DateTime? createdAt;

  bool get isPersonal => ownership == CardOwnership.personal;
  bool get hasSensitiveData =>
      isPersonal && (encryptedSensitivePayload?.isNotEmpty ?? false);

  BankCard sanitizedForShare() => BankCard(
    id: id,
    personId: personId,
    bank: bank,
    cardNumber: cardNumber,
    iban: iban,
    accountNumber: accountNumber,
    ownership: ownership,
    createdAt: createdAt,
  );

  Map<String, Object?> toMap() => {
    'id': id,
    'person_id': personId,
    'bank': bank.name,
    'card_number': cardNumber.replaceAll(RegExp(r'\D'), ''),
    'iban': iban.replaceAll(' ', '').toUpperCase(),
    'account_number': accountNumber.trim(),
    'ownership': ownership.name,
    'sensitive_payload': isPersonal ? encryptedSensitivePayload : null,
    'created_at': (createdAt ?? DateTime.now()).toIso8601String(),
  };

  factory BankCard.fromMap(Map<String, Object?> map) => BankCard(
    id: map['id'] as int?,
    personId: map['person_id']! as int,
    bank: IranianBank.values.byName(map['bank']! as String),
    cardNumber: map['card_number']! as String,
    iban: map['iban']! as String,
    accountNumber: map['account_number']! as String,
    ownership: CardOwnership.values.byName(map['ownership']! as String),
    encryptedSensitivePayload: map['sensitive_payload'] as String?,
    createdAt: DateTime.tryParse(map['created_at'] as String? ?? ''),
  );
}

class SensitiveCardFields {
  const SensitiveCardFields({required this.cvv2, required this.expiry});
  final String cvv2;
  final String expiry;
  Map<String, String> toJson() => {'cvv2': cvv2, 'expiry': expiry};
  factory SensitiveCardFields.fromJson(Map<String, dynamic> json) =>
      SensitiveCardFields(
        cvv2: json['cvv2'] as String,
        expiry: json['expiry'] as String,
      );
}
