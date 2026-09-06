import '../models/bank_card.dart';
import '../models/person.dart';
import 'iranian_bank_validators.dart';

enum DuplicateSeverity { warning, error }

enum DuplicateField { personName, cardNumber, iban, accountNumber }

final class DuplicateIssue {
  const DuplicateIssue({
    required this.field,
    required this.severity,
    required this.conflictingId,
  });

  final DuplicateField field;
  final DuplicateSeverity severity;
  final String conflictingId;
}

final class DuplicateRules {
  const DuplicateRules._();

  static List<DuplicateIssue> checkPerson(
    Person candidate,
    Iterable<Person> existing,
  ) {
    final normalizedName = _normalizeName(candidate.displayName);
    return existing
        .where(
          (person) =>
              person.id != candidate.id &&
              _normalizeName(person.displayName) == normalizedName,
        )
        .map(
          (person) => DuplicateIssue(
            field: DuplicateField.personName,
            severity: DuplicateSeverity.warning,
            conflictingId: person.id,
          ),
        )
        .toList(growable: false);
  }

  static List<DuplicateIssue> checkCard(
    BankCard candidate,
    Iterable<BankCard> existing,
  ) {
    final number = IranianBankValidators.normalizeCardNumber(
      candidate.cardNumber,
    );
    final iban = candidate.iban == null
        ? null
        : IranianBankValidators.normalizeIban(candidate.iban!);
    final account = candidate.accountNumber == null
        ? null
        : IranianBankValidators.normalizeAccountNumber(
            candidate.accountNumber!,
          );
    final issues = <DuplicateIssue>[];

    for (final card in existing.where((card) => card.id != candidate.id)) {
      if (IranianBankValidators.normalizeCardNumber(card.cardNumber) ==
          number) {
        issues.add(_error(DuplicateField.cardNumber, card.id));
      }
      if (iban != null &&
          iban.isNotEmpty &&
          card.iban != null &&
          IranianBankValidators.normalizeIban(card.iban!) == iban) {
        issues.add(_error(DuplicateField.iban, card.id));
      }
      if (account != null &&
          account.isNotEmpty &&
          card.accountNumber != null &&
          IranianBankValidators.normalizeAccountNumber(card.accountNumber!) ==
              account) {
        issues.add(_error(DuplicateField.accountNumber, card.id));
      }
    }
    return List.unmodifiable(issues);
  }

  static DuplicateIssue _error(DuplicateField field, String id) =>
      DuplicateIssue(
        field: field,
        severity: DuplicateSeverity.error,
        conflictingId: id,
      );

  static String _normalizeName(String value) =>
      IranianBankValidators.normalizeDigits(value)
          .replaceAll('ي', 'ی')
          .replaceAll('ك', 'ک')
          .replaceAll(RegExp(r'\s+'), ' ')
          .trim()
          .toLowerCase();
}
