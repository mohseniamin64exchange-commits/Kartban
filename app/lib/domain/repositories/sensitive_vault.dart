import '../models/sensitive_card_secret.dart';

/// Contract for an authenticated, encrypted vault backed by platform security.
/// Implementations must never log or persist SensitiveCardSecret as plaintext.
abstract interface class SensitiveVault {
  Future<String> store(SensitiveCardSecret secret);
  Future<SensitiveCardSecret?> reveal(String key);
  Future<void> delete(String key);
  Future<bool> contains(String key);
}
