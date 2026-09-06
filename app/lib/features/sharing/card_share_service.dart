import 'dart:typed_data';

class CardSharePayload {
  const CardSharePayload({required this.smsText, this.pngBytes});
  final String smsText;
  final Uint8List? pngBytes;
}

class CardShareService {
  const CardShareService();
  String smsText({
    required String bank,
    required String holder,
    required String cardNumber,
    required String account,
    required String iban,
  }) => '$bank\n$holder\nکارت: $cardNumber\nحساب: $account\nشبا: $iban';
  CardSharePayload prepare({
    required String bank,
    required String holder,
    required String cardNumber,
    required String account,
    required String iban,
    Uint8List? pngBytes,
  }) => CardSharePayload(
    smsText: smsText(
      bank: bank,
      holder: holder,
      cardNumber: cardNumber,
      account: account,
      iban: iban,
    ),
    pngBytes: pngBytes,
  );
}
