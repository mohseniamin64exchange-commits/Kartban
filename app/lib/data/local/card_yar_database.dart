import 'package:sqflite/sqflite.dart';

import 'database_schema.dart';

typedef DatabaseOpener =
    Future<Database> Function(
      String path, {
      required int version,
      required Future<void> Function(Database, int) onCreate,
      required Future<void> Function(Database) onConfigure,
    });

final class CardYarDatabase {
  CardYarDatabase({DatabaseOpener? opener})
    : _opener = opener ?? _defaultOpenDatabase;

  final DatabaseOpener _opener;
  Database? _database;

  Future<Database> open() async {
    final current = _database;
    if (current != null && current.isOpen) return current;

    final basePath = await getDatabasesPath();
    final separator = basePath.endsWith('/') || basePath.endsWith(r'\')
        ? ''
        : '/';
    final database = await _opener(
      '$basePath$separator${DatabaseSchema.fileName}',
      version: DatabaseSchema.version,
      onConfigure: (db) => db.execute('PRAGMA foreign_keys = ON'),
      onCreate: _create,
    );
    _database = database;
    return database;
  }

  Future<void> close() async {
    final current = _database;
    _database = null;
    if (current != null && current.isOpen) await current.close();
  }

  static Future<Database> _defaultOpenDatabase(
    String path, {
    required int version,
    required Future<void> Function(Database, int) onCreate,
    required Future<void> Function(Database) onConfigure,
  }) => openDatabase(
    path,
    version: version,
    onCreate: onCreate,
    onConfigure: onConfigure,
  );

  static Future<void> _create(Database db, int version) async {
    await db.transaction((transaction) async {
      await transaction.execute(DatabaseSchema.createPeople);
      await transaction.execute(DatabaseSchema.createCards);
      await transaction.execute(DatabaseSchema.createSettings);
      await transaction.execute(DatabaseSchema.createCardNumberIndex);
      await transaction.execute(DatabaseSchema.createIbanIndex);
      await transaction.execute(DatabaseSchema.createAccountIndex);
      await transaction.execute(DatabaseSchema.insertDefaultSettings);
    });
  }
}
