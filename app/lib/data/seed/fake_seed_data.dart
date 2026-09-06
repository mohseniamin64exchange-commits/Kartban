import '../../domain/domain.dart';

final class FakeSeedData {
  const FakeSeedData._();

  static final people = <Person>[
    Person(
      id: 'person-ali-rezaei',
      firstName: 'علی',
      lastName: 'رضایی',
      kind: PersonKind.male,
      createdAt: DateTime.utc(2026, 1, 1),
      updatedAt: DateTime.utc(2026, 1, 1),
    ),
    Person(
      id: 'person-maryam-ahmadi',
      firstName: 'مریم',
      lastName: 'احمدی',
      kind: PersonKind.female,
      createdAt: DateTime.utc(2026, 1, 2),
      updatedAt: DateTime.utc(2026, 1, 2),
    ),
    Person(
      id: 'company-aftab',
      firstName: 'فروشگاه آفتاب',
      lastName: '',
      kind: PersonKind.company,
      createdAt: DateTime.utc(2026, 1, 3),
      updatedAt: DateTime.utc(2026, 1, 3),
    ),
  ];

  // Fake checksum-valid identifiers. No seed references sensitive data.
  static final cards = <BankCard>[
    BankCard(
      id: 'card-demo-mellat',
      personId: 'person-ali-rezaei',
      bank: IranianBank.mellat,
      ownership: CardOwnership.customer,
      holderName: 'علی رضایی',
      cardNumber: '6104337906946320',
      accountNumber: '100200300400',
      createdAt: DateTime.utc(2026, 1, 1),
      updatedAt: DateTime.utc(2026, 1, 1),
    ),
    BankCard(
      id: 'card-demo-melli',
      personId: 'person-maryam-ahmadi',
      bank: IranianBank.melli,
      ownership: CardOwnership.customer,
      holderName: 'مریم احمدی',
      cardNumber: '6037997512345670',
      accountNumber: '900800700600',
      createdAt: DateTime.utc(2026, 1, 2),
      updatedAt: DateTime.utc(2026, 1, 2),
    ),
  ];

  static Future<void> insertIfEmpty(CardYarRepository repository) async {
    if ((await repository.getPeople()).isNotEmpty) return;
    for (final person in people) {
      await repository.savePerson(person);
    }
    for (final card in cards) {
      await repository.saveCard(card);
    }
  }
}
