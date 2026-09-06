import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:kartyar/domain/validation/iranian_bank_validators.dart';
import 'package:kartyar/features/cards/models/card_view_data.dart';
import 'package:kartyar/features/cards/person_cards_page.dart';
import 'package:kartyar/features/home/home_screen.dart';

void main() {
  testWidgets('صفحه اصلی فارسی و RTL نمایش داده می‌شود', (tester) async {
    await tester.binding.setSurfaceSize(const Size(393, 852));
    await tester.pumpWidget(
      MaterialApp(locale: const Locale('fa'), home: HomeScreen()),
    );
    await tester.pumpAndSettle();

    expect(find.text('کارت‌یار'), findsOneWidget);
    expect(find.text('علی رضایی'), findsOneWidget);
    expect(find.text('افزودن شخص یا کارت'), findsOneWidget);
    expect(
      Directionality.of(tester.element(find.text('علی رضایی'))),
      TextDirection.rtl,
    );

    await tester.enterText(find.byType(TextField).first, 'مریم');
    await tester.pump();
    expect(find.text('مریم احمدی'), findsOneWidget);
    expect(find.text('علی رضایی'), findsNothing);
  });

  testWidgets('صفحه شخص چهار کارت و هیچ CVV2 نشان نمی‌دهد', (tester) async {
    await tester.binding.setSurfaceSize(const Size(393, 852));
    final cards = BankBrand.values
        .map(
          (bank) => CardViewData(
            id: bank.name,
            bank: bank,
            holderName: 'علی رضایی',
            cardNumber: '6104 3379 0694 6320',
            accountNumber: '100200300400',
            iban: 'IR12 3456 7890 1234 5678 9012 34',
            ownership: CardOwnership.customer,
          ),
        )
        .toList();

    await tester.pumpWidget(
      MaterialApp(
        home: PersonCardsPage(personName: 'علی رضایی', cards: cards),
      ),
    );
    await tester.pumpAndSettle();

    expect(find.text('4 کارت بانکی'), findsOneWidget);
    expect(find.textContaining('CVV'), findsNothing);
    expect(find.text('ارسال کارت'), findsOneWidget);
  });

  test('شماره کارت معتبر و رقم فارسی درست تشخیص داده می‌شود', () {
    expect(
      IranianBankValidators.isValidCardNumber('6104-3379-0694-6320'),
      isTrue,
    );
    expect(
      IranianBankValidators.normalizeCardNumber('۶۱۰۴ ۳۳۷۹ ۰۶۹۴ ۶۳۲۰'),
      '6104337906946320',
    );
    expect(
      IranianBankValidators.isValidCardNumber('1111111111111111'),
      isFalse,
    );
  });
}
