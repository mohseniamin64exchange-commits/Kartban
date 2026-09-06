/// A short-lived in-memory value. It intentionally has no JSON/map conversion.
final class SensitiveCardSecret {
  const SensitiveCardSecret({required this.cvv2, required this.expiry});

  final String cvv2;
  final String expiry;
}
