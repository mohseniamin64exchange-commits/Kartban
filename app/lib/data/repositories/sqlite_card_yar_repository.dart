import 'package:sqflite/sqflite.dart';

import '../../domain/domain.dart';
import '../local/card_yar_database.dart';
import '../local/database_schema.dart';
import '../mappers/sqlite_mappers.dart';

final class SqliteCardYarRepository implements CardYarRepository {
  const SqliteCardYarRepository(this._database);

  final CardYarDatabase _database;

  @override
  Future<List<Person>> getPeople() async {
    final db = await _database.open();
    final rows = await db.query(
      'people',
      orderBy: 'last_name COLLATE NOCASE, first_name COLLATE NOCASE',
    );
    return List.unmodifiable(rows.map(SqliteMappers.personFromMap));
  }

  @override
  Future<Person?> getPerson(String id) async {
    final db = await _database.open();
    final rows = await db.query(
      'people',
      where: 'id = ?',
      whereArgs: [id],
      limit: 1,
    );
    return rows.isEmpty ? null : SqliteMappers.personFromMap(rows.single);
  }

  @override
  Future<void> savePerson(Person person) async {
    final db = await _database.open();
    await db.insert(
      'people',
      SqliteMappers.personToMap(person),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  @override
  Future<void> deletePerson(String id) async {
    final db = await _database.open();
    await db.delete('people', where: 'id = ?', whereArgs: [id]);
  }

  @override
  Future<List<BankCard>> getCards({String? personId}) async {
    final db = await _database.open();
    final rows = await db.query(
      'cards',
      where: personId == null ? null : 'person_id = ?',
      whereArgs: personId == null ? null : [personId],
      orderBy: 'updated_at DESC',
    );
    return List.unmodifiable(rows.map(SqliteMappers.cardFromMap));
  }

  @override
  Future<BankCard?> getCard(String id) async {
    final db = await _database.open();
    final rows = await db.query(
      'cards',
      where: 'id = ?',
      whereArgs: [id],
      limit: 1,
    );
    return rows.isEmpty ? null : SqliteMappers.cardFromMap(rows.single);
  }

  @override
  Future<void> saveCard(BankCard card) async {
    if (!IranianBankValidators.isValidCardNumber(card.cardNumber)) {
      throw const FormatException('Invalid Iranian card number.');
    }
    if (card.iban != null &&
        card.iban!.trim().isNotEmpty &&
        !IranianBankValidators.isValidIban(card.iban!)) {
      throw const FormatException('Invalid Iranian IBAN.');
    }

    final db = await _database.open();
    try {
      final changed = await db.update(
        'cards',
        SqliteMappers.cardToMap(card),
        where: 'id = ?',
        whereArgs: [card.id],
        conflictAlgorithm: ConflictAlgorithm.abort,
      );
      if (changed == 0) {
        await db.insert(
          'cards',
          SqliteMappers.cardToMap(card),
          conflictAlgorithm: ConflictAlgorithm.abort,
        );
      }
    } on DatabaseException catch (error) {
      if (!error.isUniqueConstraintError()) rethrow;
      throw DuplicateIdentifierException(_duplicateField(error.toString()));
    }
  }

  @override
  Future<void> deleteCard(String id) async {
    final db = await _database.open();
    await db.delete('cards', where: 'id = ?', whereArgs: [id]);
  }

  @override
  Future<AppSettings> getSettings() async {
    final db = await _database.open();
    final rows = await db.query(
      'settings',
      where: 'singleton_id = 1',
      limit: 1,
    );
    return rows.isEmpty
        ? const AppSettings()
        : SqliteMappers.settingsFromMap(rows.single);
  }

  @override
  Future<void> saveSettings(AppSettings settings) async {
    final db = await _database.open();
    await db.insert(
      'settings',
      SqliteMappers.settingsToMap(settings),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  @override
  Future<RepositoryHealthReport> checkHealth() async {
    final db = await _database.open();
    final integrityRows = await db.rawQuery('PRAGMA integrity_check');
    final integrity = integrityRows.isEmpty
        ? 'unknown'
        : integrityRows.first.values.first.toString();
    final personCount =
        Sqflite.firstIntValue(
          await db.rawQuery('SELECT COUNT(*) FROM people'),
        ) ??
        0;
    final cardCount =
        Sqflite.firstIntValue(
          await db.rawQuery('SELECT COUNT(*) FROM cards'),
        ) ??
        0;

    return RepositoryHealthReport(
      isHealthy: integrity == 'ok',
      schemaVersion: DatabaseSchema.version,
      personCount: personCount,
      cardCount: cardCount,
      message: integrity == 'ok' ? null : integrity,
    );
  }

  static String _duplicateField(String message) {
    if (message.contains('iban')) return 'iban';
    if (message.contains('account_number')) return 'accountNumber';
    return 'cardNumber';
  }
}
