class IranianBankingValidator {
  const IranianBankingValidator._();

  static String digits(String value) => value
      .replaceAll(RegExp(r'[^0-9۰-۹]'), '')
      .replaceAllMapped(
        RegExp(r'[۰-۹]'),
        (m) => '${'۰۱۲۳۴۵۶۷۸۹'.indexOf(m.group(0)!)}',
      );

  static bool isValidCardNumber(String input) {
    final value = digits(input);
    if (value.length != 16 || RegExp(r'^(\d)\1{15}$').hasMatch(value))
      return false;
    var sum = 0;
    for (var i = 0; i < 16; i++) {
      var digit = int.parse(value[i]) * (i.isEven ? 2 : 1);
      if (digit > 9) digit -= 9;
      sum += digit;
    }
    return sum % 10 == 0;
  }

  static bool isValidIban(String input) {
    final compact = input.replaceAll(' ', '').toUpperCase();
    final value = compact.startsWith('IR') ? compact : 'IR$compact';
    if (!RegExp(r'^IR\d{24}$').hasMatch(value)) return false;
    final rearranged = '${value.substring(4)}1827${value.substring(2, 4)}';
    var remainder = 0;
    for (final char in rearranged.split('')) {
      remainder = (remainder * 10 + int.parse(char)) % 97;
    }
    return remainder == 1;
  }

  static bool isValidAccount(String value) {
    final normalized = digits(value);
    return normalized.length >= 5 && normalized.length <= 24;
  }

  static bool isValidPersonName(String value) =>
      value.trim().replaceAll(RegExp(r'\s+'), ' ').length >= 3;
}
