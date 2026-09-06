import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:kartyar/core/design/kartyar_theme.dart';
import 'package:kartyar/features/cards/models/card_view_data.dart';
import 'package:kartyar/features/cards/person_cards_page.dart';
import 'package:kartyar/features/home/home_screen.dart';

void main() {
  Future<void> pumpAt(
    WidgetTester tester, {
    required Size size,
    required Widget child,
  }) async {
    await tester.binding.setSurfaceSize(size);
    addTearDown(() => tester.binding.setSurfaceSize(null));
    await tester.pumpWidget(
      MaterialApp(
        debugShowCheckedModeBanner: false,
        locale: const Locale('fa'),
        theme: KartyarTheme.light(),
        home: child,
      ),
    );
    await tester.pumpAndSettle();
  }

  testWidgets('home 393x852 visual', (tester) async {
    await pumpAt(tester, size: const Size(393, 852), child: HomeScreen());
    await expectLater(
      find.byType(MaterialApp),
      matchesGoldenFile('goldens/home-393x852.png'),
    );
  });

  testWidgets('home large text 360x800 has no overflow', (tester) async {
    await tester.binding.setSurfaceSize(const Size(360, 800));
    addTearDown(() => tester.binding.setSurfaceSize(null));
    await tester.pumpWidget(
      MaterialApp(
        debugShowCheckedModeBanner: false,
        locale: const Locale('fa'),
        theme: KartyarTheme.light(),
        builder: (context, child) => MediaQuery(
          data: MediaQuery.of(
            context,
          ).copyWith(textScaler: const TextScaler.linear(1.22)),
          child: child!,
        ),
        home: HomeScreen(),
      ),
    );
    await tester.pumpAndSettle();
    expect(tester.takeException(), isNull);
  });

  testWidgets('person cards 393x852 visual', (tester) async {
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
    await pumpAt(
      tester,
      size: const Size(393, 852),
      child: PersonCardsPage(personName: 'علی رضایی', cards: cards),
    );
    await expectLater(
      find.byType(MaterialApp),
      matchesGoldenFile('goldens/person-cards-393x852.png'),
    );
  });
}
