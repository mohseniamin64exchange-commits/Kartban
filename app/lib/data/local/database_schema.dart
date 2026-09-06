final class DatabaseSchema {
  const DatabaseSchema._();

  static const version = 1;
  static const fileName = 'kartyar.db';

  static const createPeople = '''
CREATE TABLE people (
  id TEXT PRIMARY KEY NOT NULL,
  first_name TEXT NOT NULL,
  last_name TEXT NOT NULL,
  kind TEXT NOT NULL,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL
)
''';

  static const createCards = '''
CREATE TABLE cards (
  id TEXT PRIMARY KEY NOT NULL,
  person_id TEXT NOT NULL,
  bank TEXT NOT NULL,
  ownership TEXT NOT NULL,
  holder_name TEXT NOT NULL,
  card_number TEXT NOT NULL,
  iban TEXT,
  account_number TEXT,
  title TEXT,
  sensitive_vault_key TEXT,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL,
  FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE CASCADE,
  CHECK (ownership != 'customer' OR sensitive_vault_key IS NULL)
)
''';

  static const createSettings = '''
CREATE TABLE settings (
  singleton_id INTEGER PRIMARY KEY CHECK (singleton_id = 1),
  theme TEXT NOT NULL,
  text_scale TEXT NOT NULL,
  auto_lock_timeout TEXT NOT NULL,
  app_lock_enabled INTEGER NOT NULL CHECK (app_lock_enabled IN (0, 1)),
  pattern_enabled INTEGER NOT NULL CHECK (pattern_enabled IN (0, 1)),
  biometric_enabled INTEGER NOT NULL CHECK (biometric_enabled IN (0, 1))
)
''';

  static const createCardNumberIndex =
      'CREATE UNIQUE INDEX cards_card_number_uq ON cards(card_number)';
  static const createIbanIndex =
      "CREATE UNIQUE INDEX cards_iban_uq ON cards(iban) "
      "WHERE iban IS NOT NULL AND iban != ''";
  static const createAccountIndex =
      "CREATE UNIQUE INDEX cards_account_uq ON cards(account_number) "
      "WHERE account_number IS NOT NULL AND account_number != ''";

  static const insertDefaultSettings = '''
INSERT INTO settings (
  singleton_id, theme, text_scale, auto_lock_timeout,
  app_lock_enabled, pattern_enabled, biometric_enabled
) VALUES (1, 'system', 'medium', 'oneMinute', 0, 0, 0)
''';
}
