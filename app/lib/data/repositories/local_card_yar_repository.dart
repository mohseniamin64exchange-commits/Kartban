import 'package:sqflite/sqflite.dart';
import '../../domain/entities/bank_card.dart';
import '../../domain/entities/person.dart';
import '../database/card_yar_database.dart';

class LegacyLocalCardYarRepository {
  LegacyLocalCardYarRepository(this.database);
  final CardYarDatabase database;

  Future<void> initialize() async {
    await database.instance;
  }

  Future<List<Person>> people() async {
    final db = await database.instance;
    return (await db.query(
      'people',
      orderBy: 'name COLLATE NOCASE ASC',
    )).map(Person.fromMap).toList(growable: false);
  }

  Future<List<BankCard>> cards({int? personId}) async {
    final db = await database.instance;
    final rows = await db.query(
      'cards',
      where: personId == null ? null : 'person_id = ?',
      whereArgs: personId == null ? null : [personId],
      orderBy: 'created_at ASC',
    );
    return rows.map(BankCard.fromMap).toList(growable: false);
  }

  Future<int> savePerson(Person person) async {
    final db = await database.instance;
    final map = person.toMap()..remove('id');
    if (person.id == null) return db.insert('people', map);
    await db.update('people', map, where: 'id = ?', whereArgs: [person.id]);
    return person.id!;
  }

  Future<int> saveCard(BankCard card) async {
    final db = await database.instance;
    final safe = card.isPersonal
        ? card
        : BankCard(
            id: card.id,
            personId: card.personId,
            bank: card.bank,
            cardNumber: card.cardNumber,
            iban: card.iban,
            accountNumber: card.accountNumber,
            ownership: card.ownership,
            createdAt: card.createdAt,
          );
    final map = safe.toMap()..remove('id');
    try {
      if (safe.id == null) return await db.insert('cards', map);
      await db.update('cards', map, where: 'id = ?', whereArgs: [safe.id]);
      return safe.id!;
    } on DatabaseException catch (error) {
      final message = error.toString();
      if (message.contains('card_number'))
        throw const LegacyDuplicateBankIdentifierException('شماره کارت');
      if (message.contains('iban'))
        throw const LegacyDuplicateBankIdentifierException('شماره شبا');
      if (message.contains('account_number'))
        throw const LegacyDuplicateBankIdentifierException('شماره حساب');
      rethrow;
    }
  }

  Future<void> deleteCard(int id) async {
    final db = await database.instance;
    await db.delete('cards', where: 'id = ?', whereArgs: [id]);
  }

  Future<bool> hasDuplicatePersonName(String name, {int? excludingId}) async {
    final db = await database.instance;
    final normalized = name.trim().replaceAll(RegExp(r'\s+'), ' ');
    final rows = await db.rawQuery(
      excludingId == null
          ? 'SELECT 1 FROM people WHERE lower(trim(name)) = lower(?) LIMIT 1'
          : 'SELECT 1 FROM people WHERE lower(trim(name)) = lower(?) AND id <> ? LIMIT 1',
      excludingId == null ? [normalized] : [normalized, excludingId],
    );
    return rows.isNotEmpty;
  }

  Future<void> close() => database.close();
}

class LegacyDuplicateBankIdentifierException implements Exception {
  const LegacyDuplicateBankIdentifierException(this.field);
  final String field;
}
