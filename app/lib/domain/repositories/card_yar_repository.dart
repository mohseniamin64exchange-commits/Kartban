import '../models/app_settings.dart';
import '../models/bank_card.dart';
import '../models/person.dart';

abstract interface class CardYarRepository {
  Future<List<Person>> getPeople();
  Future<Person?> getPerson(String id);
  Future<void> savePerson(Person person);
  Future<void> deletePerson(String id);

  Future<List<BankCard>> getCards({String? personId});
  Future<BankCard?> getCard(String id);
  Future<void> saveCard(BankCard card);
  Future<void> deleteCard(String id);

  Future<AppSettings> getSettings();
  Future<void> saveSettings(AppSettings settings);
  Future<RepositoryHealthReport> checkHealth();
}

final class RepositoryHealthReport {
  const RepositoryHealthReport({
    required this.isHealthy,
    required this.schemaVersion,
    required this.personCount,
    required this.cardCount,
    this.message,
  });

  final bool isHealthy;
  final int schemaVersion;
  final int personCount;
  final int cardCount;
  final String? message;
}

final class DuplicateIdentifierException implements Exception {
  const DuplicateIdentifierException(this.field);

  final String field;

  @override
  String toString() => 'Duplicate bank identifier: $field';
}
