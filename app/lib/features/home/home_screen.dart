import 'package:flutter/material.dart';

import 'home_drawer.dart';
import 'home_models.dart';
import 'widgets/home_person_row.dart';

class HomeScreen extends StatefulWidget {
  HomeScreen({
    super.key,
    List<HomePersonSummary>? people,
    this.actions = const NoopHomeActions(),
    this.isLoading = false,
  }) : people = people ?? fakeHomePeople;

  final List<HomePersonSummary> people;
  final HomeActions actions;
  final bool isLoading;

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final _searchController = TextEditingController();
  String? _bankId;
  HomeCardKind _cardKind = HomeCardKind.all;
  HomeSortOrder _sortOrder = HomeSortOrder.nameAscending;

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  String _normalize(String value) => value
      .trim()
      .toLowerCase()
      .replaceAll('ي', 'ی')
      .replaceAll('ك', 'ک')
      .replaceAll(RegExp(r'\s+'), ' ');

  List<HomePersonSummary> get _visiblePeople {
    final query = _normalize(_searchController.text);
    final result = widget.people.where((person) {
      final bankMatch =
          _bankId == null || person.banks.any((bank) => bank.id == _bankId);
      final kindMatch =
          _cardKind == HomeCardKind.all || person.cardKind == _cardKind;
      final haystack = _normalize(
        [
          person.name,
          ...person.searchTokens,
          ...person.banks.map((bank) => bank.name),
        ].join(' '),
      );
      return bankMatch &&
          kindMatch &&
          (query.isEmpty || haystack.contains(query));
    }).toList();
    result.sort(
      (a, b) => _sortOrder == HomeSortOrder.nameAscending
          ? a.name.compareTo(b.name)
          : b.name.compareTo(a.name),
    );
    return result;
  }

  int get _cardCount =>
      widget.people.fold(0, (sum, person) => sum + person.cardCount);
  int get _activeFilterCount =>
      (_bankId == null ? 0 : 1) + (_cardKind == HomeCardKind.all ? 0 : 1);

  @override
  Widget build(BuildContext context) {
    final people = _visiblePeople;
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        backgroundColor: const Color(0xFFF4F7FB),
        drawer: CardYarDrawer(actions: widget.actions),
        body: SafeArea(
          bottom: false,
          child: CustomScrollView(
            keyboardDismissBehavior: ScrollViewKeyboardDismissBehavior.onDrag,
            slivers: [
              SliverToBoxAdapter(child: _buildHeader(context)),
              if (widget.isLoading)
                const SliverFillRemaining(
                  hasScrollBody: false,
                  child: Center(child: CircularProgressIndicator()),
                )
              else if (people.isEmpty)
                SliverFillRemaining(
                  hasScrollBody: false,
                  child: _EmptyState(onClear: _clearFilters),
                )
              else
                SliverPadding(
                  padding: const EdgeInsets.fromLTRB(14, 14, 14, 112),
                  sliver: SliverList.separated(
                    itemCount: people.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 10),
                    itemBuilder: (context, index) => HomePersonRow(
                      person: people[index],
                      onTap: () => widget.actions.openPerson(people[index]),
                    ),
                  ),
                ),
            ],
          ),
        ),
        bottomNavigationBar: SafeArea(
          top: false,
          child: Container(
            color: const Color(0xFFF4F7FB),
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 12),
            child: SizedBox(
              height: 58,
              child: FilledButton.icon(
                onPressed: widget.actions.addPersonOrCard,
                icon: const Icon(Icons.add_rounded, size: 30),
                label: const Text(
                  'افزودن شخص یا کارت',
                  style: TextStyle(fontSize: 17, fontWeight: FontWeight.w700),
                ),
                style: FilledButton.styleFrom(
                  backgroundColor: const Color(0xFF06275A),
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(18),
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildHeader(BuildContext context) => Container(
    padding: const EdgeInsets.fromLTRB(16, 8, 16, 18),
    decoration: const BoxDecoration(
      gradient: LinearGradient(
        begin: Alignment.topRight,
        end: Alignment.bottomLeft,
        colors: [Color(0xFF0A3678), Color(0xFF041B3D)],
      ),
      borderRadius: BorderRadius.vertical(bottom: Radius.circular(30)),
      boxShadow: [
        BoxShadow(
          color: Color(0x30041B3D),
          blurRadius: 22,
          offset: Offset(0, 10),
        ),
      ],
    ),
    child: Column(
      children: [
        Row(
          children: [
            Builder(
              builder: (context) => IconButton(
                tooltip: 'منوی کارت‌یار',
                onPressed: () => Scaffold.of(context).openDrawer(),
                icon: const Icon(
                  Icons.menu_rounded,
                  color: Colors.white,
                  size: 28,
                ),
              ),
            ),
            const Spacer(),
            IconButton(
              tooltip: 'اعلان‌ها',
              onPressed: () {},
              icon: const Icon(
                Icons.notifications_none_rounded,
                color: Colors.white,
                size: 28,
              ),
            ),
          ],
        ),
        const _Brand(),
        const SizedBox(height: 16),
        TextField(
          controller: _searchController,
          onChanged: (_) => setState(() {}),
          textAlign: TextAlign.right,
          style: const TextStyle(color: Colors.white, fontSize: 15),
          decoration: InputDecoration(
            hintText: 'جست‌وجوی نام، شماره کارت، شبا یا بانک',
            hintStyle: const TextStyle(color: Color(0xFFB7C9E4)),
            suffixIcon: const Icon(
              Icons.search_rounded,
              color: Color(0xFFBFD4F2),
              size: 28,
            ),
            prefixIcon: _searchController.text.isEmpty
                ? null
                : IconButton(
                    tooltip: 'پاک کردن جست‌وجو',
                    onPressed: () {
                      _searchController.clear();
                      setState(() {});
                    },
                    icon: const Icon(
                      Icons.close_rounded,
                      color: Color(0xFFBFD4F2),
                    ),
                  ),
            filled: true,
            fillColor: const Color(0x1FFFFFFF),
            contentPadding: const EdgeInsets.symmetric(
              horizontal: 16,
              vertical: 14,
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(17),
              borderSide: const BorderSide(color: Color(0x3DFFFFFF)),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(17),
              borderSide: const BorderSide(color: Color(0x3DFFFFFF)),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(17),
              borderSide: const BorderSide(
                color: Color(0xFF8DBBFF),
                width: 1.4,
              ),
            ),
          ),
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: _Stat(
                value: '${widget.people.length}',
                label: 'مخاطب',
                icon: Icons.people_alt_rounded,
              ),
            ),
            Container(width: 1, height: 42, color: const Color(0x42FFFFFF)),
            Expanded(
              child: _Stat(
                value: '$_cardCount',
                label: 'کارت',
                icon: Icons.credit_card_rounded,
              ),
            ),
            Container(width: 1, height: 42, color: const Color(0x42FFFFFF)),
            Padding(
              padding: const EdgeInsetsDirectional.only(start: 12),
              child: Badge(
                isLabelVisible: _activeFilterCount > 0,
                label: Text('$_activeFilterCount'),
                child: IconButton.filledTonal(
                  tooltip: 'فیلتر و مرتب‌سازی',
                  onPressed: _showFilters,
                  icon: const Icon(Icons.tune_rounded),
                  style: IconButton.styleFrom(
                    backgroundColor: const Color(0x26FFFFFF),
                    foregroundColor: Colors.white,
                  ),
                ),
              ),
            ),
          ],
        ),
      ],
    ),
  );

  void _clearFilters() {
    _searchController.clear();
    setState(() {
      _bankId = null;
      _cardKind = HomeCardKind.all;
      _sortOrder = HomeSortOrder.nameAscending;
    });
  }

  Future<void> _showFilters() async {
    var bankId = _bankId;
    var cardKind = _cardKind;
    var sortOrder = _sortOrder;
    await showModalBottomSheet<void>(
      context: context,
      showDragHandle: true,
      useSafeArea: true,
      isScrollControlled: true,
      builder: (context) => Directionality(
        textDirection: TextDirection.rtl,
        child: StatefulBuilder(
          builder: (context, setSheetState) => Padding(
            padding: const EdgeInsets.fromLTRB(20, 0, 20, 24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const Text(
                  'فیلتر و مرتب‌سازی',
                  textAlign: TextAlign.right,
                  style: TextStyle(fontSize: 20, fontWeight: FontWeight.w800),
                ),
                const SizedBox(height: 16),
                DropdownButtonFormField<String?>(
                  initialValue: bankId,
                  decoration: const InputDecoration(labelText: 'بانک'),
                  items: [
                    const DropdownMenuItem<String?>(
                      value: null,
                      child: Text('همه بانک‌ها'),
                    ),
                    ...homeBanks.values.map(
                      (bank) => DropdownMenuItem<String?>(
                        value: bank.id,
                        child: Text(bank.name),
                      ),
                    ),
                  ],
                  onChanged: (value) => setSheetState(() => bankId = value),
                ),
                const SizedBox(height: 12),
                SegmentedButton<HomeCardKind>(
                  segments: const [
                    ButtonSegment(value: HomeCardKind.all, label: Text('همه')),
                    ButtonSegment(
                      value: HomeCardKind.personal,
                      label: Text('شخصی'),
                    ),
                    ButtonSegment(
                      value: HomeCardKind.customer,
                      label: Text('مشتری'),
                    ),
                  ],
                  selected: {cardKind},
                  onSelectionChanged: (value) =>
                      setSheetState(() => cardKind = value.first),
                ),
                const SizedBox(height: 12),
                SegmentedButton<HomeSortOrder>(
                  segments: const [
                    ButtonSegment(
                      value: HomeSortOrder.nameAscending,
                      label: Text('نام الف تا ی'),
                    ),
                    ButtonSegment(
                      value: HomeSortOrder.nameDescending,
                      label: Text('نام ی تا الف'),
                    ),
                  ],
                  selected: {sortOrder},
                  onSelectionChanged: (value) =>
                      setSheetState(() => sortOrder = value.first),
                ),
                const SizedBox(height: 20),
                FilledButton(
                  onPressed: () {
                    setState(() {
                      _bankId = bankId;
                      _cardKind = cardKind;
                      _sortOrder = sortOrder;
                    });
                    Navigator.pop(context);
                  },
                  child: const Text('اعمال فیلتر'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _Brand extends StatelessWidget {
  const _Brand();
  @override
  Widget build(BuildContext context) => const Column(
    children: [
      Icon(Icons.wallet_rounded, color: Colors.white, size: 50),
      SizedBox(height: 2),
      Text(
        'کارت‌یار',
        style: TextStyle(
          color: Colors.white,
          fontSize: 30,
          height: 1.2,
          fontWeight: FontWeight.w800,
        ),
      ),
      Text(
        'همه کارت‌ها، امن و در دسترس',
        style: TextStyle(color: Color(0xFFBFD0EA), fontSize: 11.5),
      ),
    ],
  );
}

class _Stat extends StatelessWidget {
  const _Stat({required this.value, required this.label, required this.icon});
  final String value;
  final String label;
  final IconData icon;
  @override
  Widget build(BuildContext context) => Row(
    mainAxisAlignment: MainAxisAlignment.center,
    children: [
      Container(
        width: 39,
        height: 39,
        decoration: const BoxDecoration(
          color: Color(0xFF82B5FF),
          shape: BoxShape.circle,
        ),
        child: Icon(icon, color: const Color(0xFF072B64), size: 21),
      ),
      const SizedBox(width: 8),
      Column(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Text(
            value,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 20,
              height: 1,
              fontWeight: FontWeight.w800,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            label,
            style: const TextStyle(color: Color(0xFFC1D1E8), fontSize: 11),
          ),
        ],
      ),
    ],
  );
}

class _EmptyState extends StatelessWidget {
  const _EmptyState({required this.onClear});
  final VoidCallback onClear;
  @override
  Widget build(BuildContext context) => Center(
    child: Padding(
      padding: const EdgeInsets.all(28),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(
            Icons.manage_search_rounded,
            size: 52,
            color: Color(0xFF7890B2),
          ),
          const SizedBox(height: 12),
          const Text(
            'موردی پیدا نشد',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.w800),
          ),
          const SizedBox(height: 6),
          const Text(
            'عبارت جست‌وجو یا فیلترها را تغییر دهید.',
            textAlign: TextAlign.center,
            style: TextStyle(color: Color(0xFF667085)),
          ),
          const SizedBox(height: 14),
          OutlinedButton(
            onPressed: onClear,
            child: const Text('پاک کردن فیلترها'),
          ),
        ],
      ),
    ),
  );
}
