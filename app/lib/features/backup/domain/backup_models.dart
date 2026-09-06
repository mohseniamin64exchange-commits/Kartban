import 'package:flutter/foundation.dart';

enum HealthLevel { healthy, warning, blocked }

@immutable
class DatabaseHealthReport {
  const DatabaseHealthReport({
    required this.level,
    required this.checkedAt,
    this.safeMessages = const [],
  });

  final HealthLevel level;
  final DateTime checkedAt;

  /// Must contain structural messages only, never record values.
  final List<String> safeMessages;

  bool get canCreateBackup => level == HealthLevel.healthy;
}

@immutable
class BackupSafeSummary {
  const BackupSafeSummary({
    required this.peopleCount,
    required this.cardCount,
    required this.personalCardCount,
  });

  final int peopleCount;
  final int cardCount;
  final int personalCardCount;
}

@immutable
class BackupPackageMetadata {
  const BackupPackageMetadata({
    required this.packageVersion,
    required this.schemaVersion,
    required this.createdAt,
    required this.summary,
    required this.isEncrypted,
  });

  final int packageVersion;
  final int schemaVersion;
  final DateTime createdAt;
  final BackupSafeSummary summary;
  final bool isEncrypted;
}

@immutable
class BackupCreationReceipt {
  const BackupCreationReceipt({
    required this.savedPath,
    required this.createdAt,
    required this.summary,
  });

  final String savedPath;
  final DateTime createdAt;
  final BackupSafeSummary summary;
}

@immutable
class RestorePreview {
  const RestorePreview({
    required this.restoreToken,
    required this.metadata,
    required this.validatedAt,
  });

  /// Opaque, short-lived token. It carries no database data.
  final String restoreToken;
  final BackupPackageMetadata metadata;
  final DateTime validatedAt;
}

enum BackupFailureCode {
  databaseUnhealthy,
  fileNotSelected,
  invalidPackage,
  integrityFailed,
  unsupportedPackageVersion,
  unsupportedSchemaVersion,
  decryptionFailed,
  restoredDatabaseUnhealthy,
  previewExpired,
  writeFailed,
}

class BackupWorkflowException implements Exception {
  const BackupWorkflowException(this.code, this.safeMessage);

  final BackupFailureCode code;

  /// User-facing and deliberately stripped of record values and secrets.
  final String safeMessage;

  @override
  String toString() => safeMessage;
}
