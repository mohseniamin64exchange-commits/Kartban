import 'package:flutter/foundation.dart';

enum HomePersonKind { man, woman, store, company }

enum HomeCardKind { all, personal, customer }

enum HomeSortOrder { nameAscending, nameDescending }

@immutable
class HomeBankSummary {
  const HomeBankSummary({
    required this.id,
    required this.name,
    required this.shortName,
  });
  final String id;
  final String name;
  final String shortName;
}

@immutable
class HomePersonSummary {
  const HomePersonSummary({
    required this.id,
    required this.name,
    required this.kind,
    required this.cardCount,
    required this.banks,
    this.cardKind = HomeCardKind.customer,
    this.searchTokens = const <String>[],
  });
  final String id;
  final String name;
  final HomePersonKind kind;
  final int cardCount;
  final List<HomeBankSummary> banks;
  final HomeCardKind cardKind;
  final List<String> searchTokens;
}

abstract interface class HomeActions {
  void openPerson(HomePersonSummary person);
  void addPersonOrCard();
  void lockNow();
  void openSecurity();
  void openBackup();
  void openPrivacyCenter();
  void openDisplaySettings();
  void openAbout();
}

class NoopHomeActions implements HomeActions {
  const NoopHomeActions();
  @override
  void addPersonOrCard() {}
  @override
  void lockNow() {}
  @override
  void openAbout() {}
  @override
  void openBackup() {}
  @override
  void openDisplaySettings() {}
  @override
  void openPerson(HomePersonSummary person) {}
  @override
  void openPrivacyCenter() {}
  @override
  void openSecurity() {}
}

const homeBanks = <String, HomeBankSummary>{
  'mellat': HomeBankSummary(id: 'mellat', name: 'بانک ملت', shortName: 'ملت'),
  'melli': HomeBankSummary(
    id: 'melli',
    name: 'بانک ملی ایران',
    shortName: 'ملی',
  ),
  'saderat': HomeBankSummary(
    id: 'saderat',
    name: 'بانک صادرات',
    shortName: 'صادرات',
  ),
  'sepah': HomeBankSummary(id: 'sepah', name: 'بانک سپه', shortName: 'سپه'),
};

final fakeHomePeople = <HomePersonSummary>[
  HomePersonSummary(
    id: 'person-1',
    name: 'علی رضایی',
    kind: HomePersonKind.man,
    cardCount: 3,
    banks: [homeBanks['mellat']!, homeBanks['melli']!, homeBanks['saderat']!],
    cardKind: HomeCardKind.personal,
    searchTokens: const ['۶۱۰۴', 'IR', 'ملت', 'ملی', 'صادرات'],
  ),
  HomePersonSummary(
    id: 'person-2',
    name: 'مریم احمدی',
    kind: HomePersonKind.woman,
    cardCount: 2,
    banks: [homeBanks['mellat']!, homeBanks['melli']!],
    searchTokens: const ['ملت', 'ملی'],
  ),
  HomePersonSummary(
    id: 'person-3',
    name: 'حسین محمدی',
    kind: HomePersonKind.man,
    cardCount: 1,
    banks: [homeBanks['melli']!],
    searchTokens: const ['ملی'],
  ),
  HomePersonSummary(
    id: 'person-4',
    name: 'فروشگاه آفتاب',
    kind: HomePersonKind.store,
    cardCount: 3,
    banks: [homeBanks['mellat']!, homeBanks['melli']!, homeBanks['saderat']!],
    searchTokens: const ['فروشگاه', 'ملت', 'ملی', 'صادرات'],
  ),
  HomePersonSummary(
    id: 'person-5',
    name: 'شرکت پارس‌گستر',
    kind: HomePersonKind.company,
    cardCount: 5,
    banks: [
      homeBanks['mellat']!,
      homeBanks['melli']!,
      homeBanks['saderat']!,
      homeBanks['sepah']!,
    ],
    cardKind: HomeCardKind.personal,
    searchTokens: const ['شرکت', 'ملت', 'ملی', 'صادرات', 'سپه'],
  ),
  HomePersonSummary(
    id: 'person-6',
    name: 'سمیرا نوری',
    kind: HomePersonKind.woman,
    cardCount: 1,
    banks: [homeBanks['saderat']!],
    searchTokens: const ['صادرات'],
  ),
  HomePersonSummary(
    id: 'person-7',
    name: 'امین کریمی',
    kind: HomePersonKind.man,
    cardCount: 2,
    banks: [homeBanks['mellat']!, homeBanks['saderat']!],
    searchTokens: const ['ملت', 'صادرات'],
  ),
];
