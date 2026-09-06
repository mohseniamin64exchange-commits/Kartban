final class IranianBankValidators {
  const IranianBankValidators._();

  static String normalizeDigits(String input) {
    const persian = '۰۱۲۳۴۵۶۷۸۹';
    const arabic = '٠١٢٣٤٥٦٧٨٩';
    final buffer = StringBuffer();
    for (final rune in input.runes) {
      final character = String.fromCharCode(rune);
      final persianIndex = persian.indexOf(character);
      final arabicIndex = arabic.indexOf(character);
      if (persianIndex >= 0) {
        buffer.write(persianIndex);
      } else if (arabicIndex >= 0) {
        buffer.write(arabicIndex);
      } else {
        buffer.write(character);
      }
    }
    return buffer.toString();
  }

  static String normalizeCardNumber(String input) =>
      normalizeDigits(input).replaceAll(RegExp(r'[^0-9]'), '');

  static bool isValidCardNumber(String input) {
    final value = normalizeCardNumber(input);
    if (value.length != 16 || RegExp(r'^(\d)\1{15}$').hasMatch(value)) {
      return false;
    }

    var sum = 0;
    for (var index = 0; index < value.length; index++) {
      var product = int.parse(value[index]) * (index.isEven ? 2 : 1);
      if (product > 9) product -= 9;
      sum += product;
    }
    return sum % 10 == 0;
  }

  static String normalizeIban(String input) =>
      normalizeDigits(input).replaceAll(RegExp(r'[\s-]'), '').toUpperCase();

  static bool isValidIban(String input) {
    final value = normalizeIban(input);
    if (!RegExp(r'^IR\d{24}$').hasMatch(value)) return false;

    final rearranged = '${value.substring(4)}${value.substring(0, 4)}';
    final numeric = StringBuffer();
    for (final codeUnit in rearranged.codeUnits) {
      if (codeUnit >= 65 && codeUnit <= 90) {
        numeric.write(codeUnit - 55);
      } else {
        numeric.writeCharCode(codeUnit);
      }
    }

    var remainder = 0;
    for (final codeUnit in numeric.toString().codeUnits) {
      remainder = (remainder * 10 + codeUnit - 48) % 97;
    }
    return remainder == 1;
  }

  static String normalizeAccountNumber(String input) =>
      normalizeDigits(input).replaceAll(RegExp(r'[^0-9]'), '');
}
