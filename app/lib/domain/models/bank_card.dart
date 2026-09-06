import 'enums.dart';

final class BankCard {
  BankCard({
    required this.id,
    required this.personId,
    required this.bank,
    required this.ownership,
    required this.holderName,
    required this.cardNumber,
    required this.createdAt,
    required this.updatedAt,
    this.iban,
    this.accountNumber,
    this.title,
    this.sensitiveVaultKey,
  }) {
    if (ownership == CardOwnership.customer && sensitiveVaultKey != null) {
      throw ArgumentError(
        'Customer cards cannot reference sensitive information.',
      );
    }
  }

  final String id;
  final String personId;
  final IranianBank bank;
  final CardOwnership ownership;
  final String holderName;
  final String cardNumber;
  final String? iban;
  final String? accountNumber;
  final String? title;

  /// Opaque key only. The referenced payload belongs to SensitiveVault.
  final String? sensitiveVaultKey;
  final DateTime createdAt;
  final DateTime updatedAt;

  bool get hasSensitiveData => sensitiveVaultKey != null;

  BankCard copyWith({
    String? personId,
    IranianBank? bank,
    CardOwnership? ownership,
    String? holderName,
    String? cardNumber,
    String? iban,
    bool clearIban = false,
    String? accountNumber,
    bool clearAccountNumber = false,
    String? title,
    bool clearTitle = false,
    String? sensitiveVaultKey,
    bool clearSensitiveVaultKey = false,
    DateTime? updatedAt,
  }) => BankCard(
    id: id,
    personId: personId ?? this.personId,
    bank: bank ?? this.bank,
    ownership: ownership ?? this.ownership,
    holderName: holderName ?? this.holderName,
    cardNumber: cardNumber ?? this.cardNumber,
    iban: clearIban ? null : iban ?? this.iban,
    accountNumber: clearAccountNumber
        ? null
        : accountNumber ?? this.accountNumber,
    title: clearTitle ? null : title ?? this.title,
    sensitiveVaultKey: clearSensitiveVaultKey
        ? null
        : sensitiveVaultKey ?? this.sensitiveVaultKey,
    createdAt: createdAt,
    updatedAt: updatedAt ?? this.updatedAt,
  );

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is BankCard &&
          id == other.id &&
          personId == other.personId &&
          bank == other.bank &&
          ownership == other.ownership &&
          holderName == other.holderName &&
          cardNumber == other.cardNumber &&
          iban == other.iban &&
          accountNumber == other.accountNumber &&
          title == other.title &&
          sensitiveVaultKey == other.sensitiveVaultKey &&
          createdAt == other.createdAt &&
          updatedAt == other.updatedAt;

  @override
  int get hashCode => Object.hash(
    id,
    personId,
    bank,
    ownership,
    holderName,
    cardNumber,
    iban,
    accountNumber,
    title,
    sensitiveVaultKey,
    createdAt,
    updatedAt,
  );
}
