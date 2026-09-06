import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';
import 'package:sqflite/sqflite.dart';
import '../../domain/entities/bank.dart';
import '../../domain/entities/bank_card.dart';
import '../../domain/entities/person.dart';

class CardYarDatabase {
  Database? _database;
  Future<Database> get instance async {
    if (_database != null) return _database!;
    final directory = await getApplicationSupportDirectory();
    _database = await openDatabase(
      p.join(directory.path, 'kartyar.db'),
      version: 1,
      onConfigure: (db) => db.execute('PRAGMA foreign_keys = ON'),
      onCreate: (db, version) async {
        await db.execute(
          'CREATE TABLE people (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL COLLATE NOCASE, kind TEXT NOT NULL, note TEXT, created_at TEXT NOT NULL)',
        );
        await db.execute(
          "CREATE TABLE cards (id INTEGER PRIMARY KEY AUTOINCREMENT, person_id INTEGER NOT NULL REFERENCES people(id) ON DELETE CASCADE, bank TEXT NOT NULL, card_number TEXT NOT NULL UNIQUE, iban TEXT NOT NULL UNIQUE, account_number TEXT NOT NULL UNIQUE, ownership TEXT NOT NULL, sensitive_payload TEXT, created_at TEXT NOT NULL, CHECK (ownership = 'personal' OR sensitive_payload IS NULL))",
        );
        await db.execute('CREATE INDEX people_name_idx ON people(name)');
        await db.execute('CREATE INDEX cards_person_idx ON cards(person_id)');
        await _seed(db);
      },
    );
    return _database!;
  }

  Future<void> _seed(Database db) async {
    final people = <Person>[
      const Person(id: null, name: 'علی رضایی', kind: PersonKind.male),
      const Person(id: null, name: 'مریم احمدی', kind: PersonKind.female),
      const Person(id: null, name: 'فروشگاه آفتاب', kind: PersonKind.company),
      const Person(id: null, name: 'شرکت پارس‌گستر', kind: PersonKind.company),
      const Person(id: null, name: 'سمیرا نوری', kind: PersonKind.female),
    ];
    final ids = <int>[];
    for (final person in people) {
      ids.add(await db.insert('people', person.toMap()));
    }
    final cards = <BankCard>[
      BankCard(
        id: null,
        personId: ids[0],
        bank: IranianBank.mellat,
        cardNumber: '6104337800000018',
        iban: 'IR120170000000100000000001',
        accountNumber: '100001',
        ownership: CardOwnership.customer,
      ),
      BankCard(
        id: null,
        personId: ids[0],
        bank: IranianBank.melli,
        cardNumber: '6037997500000024',
        iban: 'IR220170000000100000000002',
        accountNumber: '100002',
        ownership: CardOwnership.customer,
      ),
      BankCard(
        id: null,
        personId: ids[0],
        bank: IranianBank.saderat,
        cardNumber: '6037697500000031',
        iban: 'IR320170000000100000000003',
        accountNumber: '100003',
        ownership: CardOwnership.customer,
      ),
      BankCard(
        id: null,
        personId: ids[1],
        bank: IranianBank.melli,
        cardNumber: '6037997500000040',
        iban: 'IR420170000000100000000004',
        accountNumber: '100004',
        ownership: CardOwnership.customer,
      ),
      BankCard(
        id: null,
        personId: ids[1],
        bank: IranianBank.mellat,
        cardNumber: '6104337800000059',
        iban: 'IR520170000000100000000005',
        accountNumber: '100005',
        ownership: CardOwnership.customer,
      ),
      BankCard(
        id: null,
        personId: ids[2],
        bank: IranianBank.tejarat,
        cardNumber: '6273537500000066',
        iban: 'IR620170000000100000000006',
        accountNumber: '100006',
        ownership: CardOwnership.customer,
      ),
      BankCard(
        id: null,
        personId: ids[3],
        bank: IranianBank.pasargad,
        cardNumber: '5022297500000073',
        iban: 'IR720170000000100000000007',
        accountNumber: '100007',
        ownership: CardOwnership.customer,
      ),
      BankCard(
        id: null,
        personId: ids[4],
        bank: IranianBank.iranZamin,
        cardNumber: '5057857500000081',
        iban: 'IR820170000000100000000008',
        accountNumber: '100008',
        ownership: CardOwnership.customer,
      ),
    ];
    for (final card in cards) {
      await db.insert('cards', card.toMap());
    }
  }

  Future<bool> integrityCheck() async {
    final db = await instance;
    final result = await db.rawQuery('PRAGMA integrity_check');
    return result.isNotEmpty && result.first.values.first == 'ok';
  }

  Future<void> close() async {
    final db = _database;
    _database = null;
    await db?.close();
  }
}
