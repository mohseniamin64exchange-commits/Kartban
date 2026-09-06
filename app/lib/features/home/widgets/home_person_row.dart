import 'package:flutter/material.dart';

import '../home_models.dart';

class HomePersonRow extends StatelessWidget {
  const HomePersonRow({required this.person, required this.onTap, super.key});

  final HomePersonSummary person;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final isLargeText = MediaQuery.textScalerOf(context).scale(1) > 1.05;
    final isCompactWidth = MediaQuery.sizeOf(context).width < 380;
    final visibleBankLimit = isLargeText || isCompactWidth ? 2 : 3;
    final visibleBanks = person.banks
        .take(visibleBankLimit)
        .toList(growable: false);
    final hiddenCount = person.banks.length - visibleBanks.length;

    return Semantics(
      button: true,
      label: '${person.name}، ${person.cardCount} کارت بانکی',
      child: Material(
        color: Colors.white,
        borderRadius: BorderRadius.circular(18),
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(18),
          child: Container(
            constraints: BoxConstraints(minHeight: isLargeText ? 82 : 72),
            padding: const EdgeInsetsDirectional.fromSTEB(10, 8, 12, 8),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(18),
              border: Border.all(color: const Color(0x12405273)),
              boxShadow: const [
                BoxShadow(
                  color: Color(0x10102847),
                  blurRadius: 18,
                  offset: Offset(0, 6),
                ),
              ],
            ),
            child: Row(
              textDirection: TextDirection.rtl,
              children: [
                _PersonAvatar(kind: person.kind),
                const SizedBox(width: 10),
                Expanded(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Text(
                        person.name,
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                        textAlign: TextAlign.right,
                        style: const TextStyle(
                          color: Color(0xFF111827),
                          fontSize: 16,
                          height: 1.25,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      const SizedBox(height: 2),
                      Text(
                        '${person.cardCount} کارت',
                        textAlign: TextAlign.right,
                        style: const TextStyle(
                          color: Color(0xFF788497),
                          fontSize: 12,
                          height: 1.2,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: 6),
                Flexible(
                  flex: 0,
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    textDirection: TextDirection.ltr,
                    children: [
                      for (
                        var index = 0;
                        index < visibleBanks.length;
                        index++
                      ) ...[
                        if (index > 0) const SizedBox(width: 4),
                        _BankBadge(bank: visibleBanks[index]),
                      ],
                      if (hiddenCount > 0) ...[
                        const SizedBox(width: 4),
                        _MoreBadge(count: hiddenCount),
                      ],
                    ],
                  ),
                ),
                const SizedBox(width: 4),
                const Icon(
                  Icons.chevron_left_rounded,
                  size: 27,
                  color: Color(0xFF8A96A8),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _PersonAvatar extends StatelessWidget {
  const _PersonAvatar({required this.kind});
  final HomePersonKind kind;

  @override
  Widget build(BuildContext context) {
    final icon = switch (kind) {
      HomePersonKind.man => Icons.person_rounded,
      HomePersonKind.woman => Icons.person_2_rounded,
      HomePersonKind.store => Icons.storefront_rounded,
      HomePersonKind.company => Icons.apartment_rounded,
    };
    final label = switch (kind) {
      HomePersonKind.man => 'مخاطب مرد',
      HomePersonKind.woman => 'مخاطب زن',
      HomePersonKind.store => 'فروشگاه',
      HomePersonKind.company => 'شرکت',
    };
    return Semantics(
      image: true,
      label: label,
      child: Container(
        width: 50,
        height: 50,
        decoration: const BoxDecoration(
          shape: BoxShape.circle,
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [Color(0xFFF0F6FF), Color(0xFFD9E8FF)],
          ),
        ),
        child: Icon(icon, color: const Color(0xFF3E6CBA), size: 35),
      ),
    );
  }
}

class _BankBadge extends StatelessWidget {
  const _BankBadge({required this.bank});
  final HomeBankSummary bank;

  @override
  Widget build(BuildContext context) {
    final palette = switch (bank.id) {
      'mellat' => (const Color(0xFFE51E3C), const Color(0xFFFFD54A)),
      'melli' => (const Color(0xFF09236F), Colors.white),
      'saderat' => (const Color(0xFF079DBE), Colors.white),
      'sepah' => (const Color(0xFF2457A8), Colors.white),
      _ => (const Color(0xFF45556F), Colors.white),
    };
    final icon = switch (bank.id) {
      'mellat' => Icons.hexagon_outlined,
      'melli' => Icons.account_balance_rounded,
      'saderat' => Icons.hub_outlined,
      'sepah' => Icons.shield_outlined,
      _ => Icons.account_balance_outlined,
    };
    return Semantics(
      label: bank.name,
      child: Container(
        height: 30,
        constraints: const BoxConstraints(minWidth: 46, maxWidth: 62),
        padding: const EdgeInsets.symmetric(horizontal: 6),
        decoration: BoxDecoration(
          color: palette.$1,
          borderRadius: BorderRadius.circular(9),
          border: Border.all(color: Colors.white.withValues(alpha: .55)),
          boxShadow: [
            BoxShadow(
              color: palette.$1.withValues(alpha: .18),
              blurRadius: 8,
              offset: const Offset(0, 3),
            ),
          ],
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          textDirection: TextDirection.rtl,
          children: [
            Icon(icon, size: 12, color: palette.$2),
            const SizedBox(width: 3),
            Flexible(
              child: Text(
                bank.shortName,
                maxLines: 1,
                overflow: TextOverflow.fade,
                softWrap: false,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 9,
                  height: 1,
                  fontWeight: FontWeight.w800,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _MoreBadge extends StatelessWidget {
  const _MoreBadge({required this.count});
  final int count;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 30,
      constraints: const BoxConstraints(minWidth: 34),
      padding: const EdgeInsets.symmetric(horizontal: 6),
      alignment: Alignment.center,
      decoration: BoxDecoration(
        color: const Color(0xFFEEF2F7),
        borderRadius: BorderRadius.circular(9),
        border: Border.all(color: const Color(0xFFDDE3EC)),
      ),
      child: Text(
        '+$count',
        textDirection: TextDirection.ltr,
        style: const TextStyle(
          color: Color(0xFF4D596B),
          fontSize: 11,
          fontWeight: FontWeight.w800,
        ),
      ),
    );
  }
}
