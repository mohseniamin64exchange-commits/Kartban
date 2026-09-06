import 'dart:convert';
import 'dart:io';
import 'dart:math';
import 'dart:typed_data';
import 'package:cryptography/cryptography.dart';
import 'package:file_picker/file_picker.dart';
import 'package:path/path.dart' as p;
import 'package:sqflite/sqflite.dart';

import '../../../data/local/card_yar_database.dart';
import '../../../data/local/database_schema.dart';
import '../domain/backup_models.dart';

class LocalBackupWorkflow {
  LocalBackupWorkflow(this._database);
  final CardYarDatabase _database;
  final _cipher = AesGcm.with256bits();
  final _kdf = Pbkdf2(
    macAlgorithm: Hmac.sha256(),
    iterations: 210000,
    bits: 256,
  );
  List<int>? _pendingBytes;
  String? _pendingToken;

  Future<DatabaseHealthReport> checkHealth() async {
    final db = await _database.open();
    final result = await db.rawQuery('PRAGMA integrity_check');
    final healthy = result.isNotEmpty && result.first.values.first == 'ok';
    return DatabaseHealthReport(
      level: healthy ? HealthLevel.healthy : HealthLevel.blocked,
      checkedAt: DateTime.now(),
      safeMessages: [
        healthy
            ? 'ساختار پایگاه داده سالم است.'
            : 'پایگاه داده نیاز به بررسی دارد.',
      ],
    );
  }

  Future<BackupCreationReceipt?> createEncryptedBackup(String password) async {
    if (password.length < 6)
      throw const BackupWorkflowException(
        BackupFailureCode.writeFailed,
        'رمز پشتیبان باید حداقل ۶ نویسه باشد.',
      );
    final health = await checkHealth();
    if (!health.canCreateBackup)
      throw const BackupWorkflowException(
        BackupFailureCode.databaseUnhealthy,
        'پیش از پشتیبان‌گیری، سلامت داده تأیید نشد.',
      );
    final summary = await _summary();
    await _database.close();
    final dbPath = await _databasePath();
    final plain = await File(dbPath).readAsBytes();
    final salt = _random(16);
    final nonce = _random(12);
    final key = await _kdf.deriveKey(
      secretKey: SecretKey(utf8.encode(password)),
      nonce: salt,
    );
    final box = await _cipher.encrypt(plain, secretKey: key, nonce: nonce);
    plain.fillRange(0, plain.length, 0);
    final envelope = jsonEncode({
      'format': 'KARTYAR_BACKUP',
      'packageVersion': 1,
      'schemaVersion': DatabaseSchema.version,
      'createdAt': DateTime.now().toUtc().toIso8601String(),
      'summary': {
        'people': summary.peopleCount,
        'cards': summary.cardCount,
        'personal': summary.personalCardCount,
      },
      'salt': base64Encode(salt),
      'nonce': base64Encode(box.nonce),
      'ciphertext': base64Encode(box.cipherText),
      'mac': base64Encode(box.mac.bytes),
    });
    final uri = await FilePicker.saveFile(
      bytes: Uint8List.fromList(utf8.encode(envelope)),
      mimeType: 'application/octet-stream',
      dialogTitle: 'ذخیره پشتیبان رمزنگاری‌شده کارت‌یار',
      fileName: 'kartyar-${DateTime.now().millisecondsSinceEpoch}.kty',
      type: FileType.custom,
      allowedExtensions: const ['kty'],
    );
    if (uri == null) return null;
    await _database.open();
    return BackupCreationReceipt(
      savedPath: uri.toString(),
      createdAt: DateTime.now(),
      summary: summary,
    );
  }

  Future<RestorePreview?> inspectBackup(String password) async {
    final picked = await FilePicker.pickFile(
      type: FileType.custom,
      allowedExtensions: const ['kty'],
    );
    if (picked == null) return null;
    final bytes = await picked.readAsBytes();
    if (bytes.isEmpty)
      throw const BackupWorkflowException(
        BackupFailureCode.fileNotSelected,
        'فایل قابل خواندن نیست.',
      );
    try {
      final envelope = jsonDecode(utf8.decode(bytes)) as Map<String, dynamic>;
      if (envelope['format'] != 'KARTYAR_BACKUP' ||
          envelope['packageVersion'] != 1) {
        throw const BackupWorkflowException(
          BackupFailureCode.unsupportedPackageVersion,
          'نسخه فایل پشتیبان پشتیبانی نمی‌شود.',
        );
      }
      final salt = base64Decode(envelope['salt'] as String);
      final nonce = base64Decode(envelope['nonce'] as String);
      final cipherText = base64Decode(envelope['ciphertext'] as String);
      final mac = Mac(base64Decode(envelope['mac'] as String));
      final key = await _kdf.deriveKey(
        secretKey: SecretKey(utf8.encode(password)),
        nonce: salt,
      );
      final plain = await _cipher.decrypt(
        SecretBox(cipherText, nonce: nonce, mac: mac),
        secretKey: key,
      );
      final token = base64UrlEncode(_random(18));
      _pendingBytes?.fillRange(0, _pendingBytes!.length, 0);
      _pendingBytes = plain;
      _pendingToken = token;
      final rawSummary = envelope['summary'] as Map<String, dynamic>;
      return RestorePreview(
        restoreToken: token,
        metadata: BackupPackageMetadata(
          packageVersion: 1,
          schemaVersion: envelope['schemaVersion'] as int,
          createdAt: DateTime.parse(envelope['createdAt'] as String),
          summary: BackupSafeSummary(
            peopleCount: rawSummary['people'] as int,
            cardCount: rawSummary['cards'] as int,
            personalCardCount: rawSummary['personal'] as int,
          ),
          isEncrypted: true,
        ),
        validatedAt: DateTime.now(),
      );
    } on BackupWorkflowException {
      rethrow;
    } catch (_) {
      throw const BackupWorkflowException(
        BackupFailureCode.decryptionFailed,
        'فایل، رمز یا امضای پشتیبان معتبر نیست.',
      );
    }
  }

  Future<void> confirmRestore(String token) async {
    if (token != _pendingToken || _pendingBytes == null)
      throw const BackupWorkflowException(
        BackupFailureCode.previewExpired,
        'پیش‌نمایش بازیابی منقضی شده است.',
      );
    final dbPath = await _databasePath();
    await _database.close();
    final target = File(dbPath);
    final staged = File('$dbPath.restore');
    await staged.writeAsBytes(_pendingBytes!, flush: true);
    final check = await openDatabase(staged.path, readOnly: true);
    final integrity = await check.rawQuery('PRAGMA integrity_check');
    await check.close();
    if (integrity.isEmpty || integrity.first.values.first != 'ok') {
      await staged.delete();
      throw const BackupWorkflowException(
        BackupFailureCode.restoredDatabaseUnhealthy,
        'سلامت داده بازیابی‌شده تأیید نشد.',
      );
    }
    final safety = File('$dbPath.before_restore');
    if (await target.exists()) await target.copy(safety.path);
    await staged.copy(target.path);
    await staged.delete();
    _pendingBytes!.fillRange(0, _pendingBytes!.length, 0);
    _pendingBytes = null;
    _pendingToken = null;
    await _database.open();
  }

  Future<BackupSafeSummary> _summary() async {
    final db = await _database.open();
    final people =
        Sqflite.firstIntValue(
          await db.rawQuery('SELECT COUNT(*) FROM people'),
        ) ??
        0;
    final cards =
        Sqflite.firstIntValue(
          await db.rawQuery('SELECT COUNT(*) FROM cards'),
        ) ??
        0;
    final personal =
        Sqflite.firstIntValue(
          await db.rawQuery(
            "SELECT COUNT(*) FROM cards WHERE ownership = 'personal'",
          ),
        ) ??
        0;
    return BackupSafeSummary(
      peopleCount: people,
      cardCount: cards,
      personalCardCount: personal,
    );
  }

  Future<String> _databasePath() async =>
      p.join(await getDatabasesPath(), DatabaseSchema.fileName);
  List<int> _random(int length) =>
      List<int>.generate(length, (_) => Random.secure().nextInt(256));
}
