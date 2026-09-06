import '../../domain/domain.dart';

final class SqliteMappers {
  const SqliteMappers._();

  static Map<String, Object?> personToMap(Person person) => {
    'id': person.id,
    'first_name': person.firstName.trim(),
    'last_name': person.lastName.trim(),
    'kind': person.kind.name,
    'created_at': person.createdAt.toUtc().toIso8601String(),
    'updated_at': person.updatedAt.toUtc().toIso8601String(),
  };

  static Person personFromMap(Map<String, Object?> map) => Person(
    id: map['id']! as String,
    firstName: map['first_name']! as String,
    lastName: map['last_name']! as String,
    kind: PersonKind.values.byName(map['kind']! as String),
    createdAt: DateTime.parse(map['created_at']! as String).toLocal(),
    updatedAt: DateTime.parse(map['updated_at']! as String).toLocal(),
  );

  static Map<String, Object?> cardToMap(BankCard card) => {
    'id': card.id,
    'person_id': card.personId,
    'bank': card.bank.name,
    'ownership': card.ownership.name,
    'holder_name': card.holderName.trim(),
    'card_number': IranianBankValidators.normalizeCardNumber(card.cardNumber),
    'iban': card.iban == null
        ? null
        : IranianBankValidators.normalizeIban(card.iban!),
    'account_number': card.accountNumber == null
        ? null
        : IranianBankValidators.normalizeAccountNumber(card.accountNumber!),
    'title': card.title?.trim(),
    'sensitive_vault_key': card.sensitiveVaultKey,
    'created_at': card.createdAt.toUtc().toIso8601String(),
    'updated_at': card.updatedAt.toUtc().toIso8601String(),
  };

  static BankCard cardFromMap(Map<String, Object?> map) => BankCard(
    id: map['id']! as String,
    personId: map['person_id']! as String,
    bank: IranianBank.values.byName(map['bank']! as String),
    ownership: CardOwnership.values.byName(map['ownership']! as String),
    holderName: map['holder_name']! as String,
    cardNumber: map['card_number']! as String,
    iban: map['iban'] as String?,
    accountNumber: map['account_number'] as String?,
    title: map['title'] as String?,
    sensitiveVaultKey: map['sensitive_vault_key'] as String?,
    createdAt: DateTime.parse(map['created_at']! as String).toLocal(),
    updatedAt: DateTime.parse(map['updated_at']! as String).toLocal(),
  );

  static Map<String, Object?> settingsToMap(AppSettings settings) => {
    'singleton_id': 1,
    'theme': settings.theme.name,
    'text_scale': settings.textScale.name,
    'auto_lock_timeout': settings.autoLockTimeout.name,
    'app_lock_enabled': settings.appLockEnabled ? 1 : 0,
    'pattern_enabled': settings.patternEnabled ? 1 : 0,
    'biometric_enabled': settings.biometricEnabled ? 1 : 0,
  };

  static AppSettings settingsFromMap(Map<String, Object?> map) => AppSettings(
    theme: AppThemePreference.values.byName(map['theme']! as String),
    textScale: AppTextScale.values.byName(map['text_scale']! as String),
    autoLockTimeout: AutoLockTimeout.values.byName(
      map['auto_lock_timeout']! as String,
    ),
    appLockEnabled: map['app_lock_enabled'] == 1,
    patternEnabled: map['pattern_enabled'] == 1,
    biometricEnabled: map['biometric_enabled'] == 1,
  );
}
