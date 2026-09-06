final class SecurityException implements Exception {
  const SecurityException(this.code, this.message);

  final String code;
  final String message;

  @override
  String toString() => 'SecurityException($code)';
}
