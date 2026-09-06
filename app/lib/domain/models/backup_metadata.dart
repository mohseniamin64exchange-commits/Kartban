import 'enums.dart';

final class BackupMetadata {
  const BackupMetadata({
    required this.formatVersion,
    required this.schemaVersion,
    required this.createdAt,
    required this.personCount,
    required this.cardCount,
    required this.encrypted,
    required this.authenticated,
    required this.health,
    this.contentDigest,
  });

  final int formatVersion;
  final int schemaVersion;
  final DateTime createdAt;
  final int personCount;
  final int cardCount;
  final bool encrypted;
  final bool authenticated;
  final BackupHealth health;
  final String? contentDigest;

  bool get isRestorable =>
      encrypted && authenticated && health == BackupHealth.healthy;
}
