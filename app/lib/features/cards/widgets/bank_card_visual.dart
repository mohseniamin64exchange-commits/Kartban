import 'dart:math' as math;

import 'package:flutter/material.dart';

import '../models/card_view_data.dart';

class BankCardVisual extends StatelessWidget {
  const BankCardVisual({
    required this.card,
    this.selected = false,
    this.compact = true,
    this.onTap,
    this.onDoubleTap,
    this.onEdit,
    super.key,
  });

  final CardViewData card;
  final bool selected;
  final bool compact;
  final VoidCallback? onTap;
  final VoidCallback? onDoubleTap;
  final VoidCallback? onEdit;

  @override
  Widget build(BuildContext context) {
    final palette = BankCardPalette.forBrand(card.bank);
    final radius = BorderRadius.circular(compact ? 18 : 22);
    return Semantics(
      button: true,
      selected: selected,
      label: '${card.bankName}، ${card.cardNumber}',
      hint: card.ownership == CardOwnership.personal
          ? 'برای انتخاب یک بار و برای نمایش امن دو بار ضربه بزنید'
          : 'برای انتخاب ضربه بزنید',
      child: GestureDetector(
        behavior: HitTestBehavior.opaque,
        onTap: onTap,
        onDoubleTap: card.ownership == CardOwnership.personal
            ? onDoubleTap
            : null,
        onLongPress: onEdit,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 180),
          curve: Curves.easeOutCubic,
          decoration: BoxDecoration(
            borderRadius: radius,
            border: Border.all(
              color: selected ? const Color(0xFF78B7FF) : Colors.white12,
              width: selected ? 2.5 : 1,
            ),
            boxShadow: [
              BoxShadow(
                color: selected
                    ? const Color(0xFF1769E0).withValues(alpha: .28)
                    : const Color(0xFF102B52).withValues(alpha: .20),
                blurRadius: selected ? 20 : 13,
                offset: const Offset(0, 7),
              ),
            ],
          ),
          child: ClipRRect(
            borderRadius: radius,
            child: Stack(
              fit: StackFit.expand,
              children: [
                CustomPaint(painter: _BankPatternPainter(palette)),
                Padding(
                  padding: EdgeInsets.fromLTRB(
                    compact ? 14 : 18,
                    compact ? 11 : 16,
                    compact ? 14 : 18,
                    compact ? 10 : 14,
                  ),
                  child: _CardContent(card: card, compact: compact),
                ),
                Positioned(
                  left: compact ? 11 : 15,
                  top: compact ? 10 : 14,
                  child: _SelectionMark(selected: selected),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _CardContent extends StatelessWidget {
  const _CardContent({required this.card, required this.compact});
  final CardViewData card;
  final bool compact;

  @override
  Widget build(BuildContext context) {
    final foreground = BankCardPalette.forBrand(card.bank).foreground;
    final secondary = foreground.withValues(alpha: .76);
    return DefaultTextStyle(
      style: TextStyle(color: foreground, fontFamily: 'Vazirmatn'),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Row(
            textDirection: TextDirection.rtl,
            children: [
              BankLogomark(brand: card.bank, size: compact ? 29 : 39),
              const SizedBox(width: 8),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      card.bankName,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      textAlign: TextAlign.right,
                      style: TextStyle(
                        fontSize: compact ? 12.5 : 16,
                        fontWeight: FontWeight.w800,
                        height: 1.2,
                      ),
                    ),
                    Text(
                      card.bankLatinName,
                      textDirection: TextDirection.ltr,
                      style: TextStyle(
                        color: secondary,
                        fontSize: compact ? 6.5 : 8,
                        fontWeight: FontWeight.w600,
                        letterSpacing: .8,
                      ),
                    ),
                  ],
                ),
              ),
              SizedBox(width: compact ? 31 : 38),
            ],
          ),
          const Spacer(),
          FittedBox(
            fit: BoxFit.scaleDown,
            alignment: Alignment.center,
            child: Text(
              groupCardNumber(card.cardNumber),
              textDirection: TextDirection.ltr,
              style: TextStyle(
                color: foreground,
                fontFamily: 'monospace',
                fontSize: compact ? 16 : 20,
                fontWeight: FontWeight.w700,
                letterSpacing: compact ? 1.1 : 1.8,
                height: 1,
              ),
            ),
          ),
          SizedBox(height: compact ? 6 : 11),
          Row(
            textDirection: TextDirection.rtl,
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Expanded(
                flex: 4,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      card.holderName,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      textAlign: TextAlign.right,
                      style: TextStyle(
                        fontSize: compact ? 10 : 12,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    Text(
                      card.ownership == CardOwnership.personal
                          ? 'کارت شخصی'
                          : 'کارت مخاطب',
                      style: TextStyle(
                        color: secondary,
                        fontSize: compact ? 7 : 9,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                flex: 7,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    _Identifier(card.accountNumber, compact: compact),
                    _Identifier(card.iban, compact: compact),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _Identifier extends StatelessWidget {
  const _Identifier(this.value, {required this.compact});
  final String value;
  final bool compact;

  @override
  Widget build(BuildContext context) => FittedBox(
    fit: BoxFit.scaleDown,
    alignment: Alignment.centerLeft,
    child: Text(
      value,
      maxLines: 1,
      textDirection: TextDirection.ltr,
      style: TextStyle(
        color: Colors.white.withValues(alpha: .88),
        fontFamily: 'monospace',
        fontSize: compact ? 7.2 : 9.5,
        fontWeight: FontWeight.w600,
        height: 1.28,
        letterSpacing: .25,
      ),
    ),
  );
}

class _SelectionMark extends StatelessWidget {
  const _SelectionMark({required this.selected});
  final bool selected;

  @override
  Widget build(BuildContext context) => AnimatedContainer(
    duration: const Duration(milliseconds: 160),
    width: 27,
    height: 27,
    decoration: BoxDecoration(
      color: selected ? Colors.white : Colors.white.withValues(alpha: .14),
      borderRadius: BorderRadius.circular(8),
      border: Border.all(color: Colors.white.withValues(alpha: .9), width: 1.5),
    ),
    child: selected
        ? const Icon(Icons.check_rounded, color: Color(0xFF1769E0), size: 20)
        : null,
  );
}

class BankLogomark extends StatelessWidget {
  const BankLogomark({required this.brand, this.size = 38, super.key});
  final BankBrand brand;
  final double size;

  @override
  Widget build(BuildContext context) {
    final palette = BankCardPalette.forBrand(brand);
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: .96),
        borderRadius: BorderRadius.circular(size * .3),
        boxShadow: const [BoxShadow(color: Colors.black12, blurRadius: 5)],
      ),
      alignment: Alignment.center,
      child: CustomPaint(
        size: Size.square(size * .62),
        painter: _LogomarkPainter(brand, palette.accent),
      ),
    );
  }
}

class BankCardPalette {
  const BankCardPalette({
    required this.start,
    required this.end,
    required this.accent,
    this.foreground = Colors.white,
  });
  final Color start;
  final Color end;
  final Color accent;
  final Color foreground;

  static BankCardPalette forBrand(BankBrand brand) => switch (brand) {
    BankBrand.mellat => const BankCardPalette(
      start: Color(0xFF711126),
      end: Color(0xFFE72C4E),
      accent: Color(0xFFD91E42),
    ),
    BankBrand.melli => const BankCardPalette(
      start: Color(0xFF061849),
      end: Color(0xFF1264B6),
      accent: Color(0xFF153B8E),
    ),
    BankBrand.saderat => const BankCardPalette(
      start: Color(0xFF034E6A),
      end: Color(0xFF13B6CF),
      accent: Color(0xFF048FAD),
    ),
    BankBrand.tejarat => const BankCardPalette(
      start: Color(0xFF292D78),
      end: Color(0xFF7667C9),
      accent: Color(0xFF4A49A5),
    ),
  };
}

class _BankPatternPainter extends CustomPainter {
  const _BankPatternPainter(this.palette);
  final BankCardPalette palette;

  @override
  void paint(Canvas canvas, Size size) {
    final rect = Offset.zero & size;
    canvas.drawRect(
      rect,
      Paint()
        ..shader = LinearGradient(
          begin: Alignment.topRight,
          end: Alignment.bottomLeft,
          colors: [palette.start, palette.end],
        ).createShader(rect),
    );
    final glow = Paint()
      ..color = Colors.white.withValues(alpha: .08)
      ..style = PaintingStyle.stroke
      ..strokeWidth = math.max(12, size.shortestSide * .12);
    canvas.drawCircle(
      Offset(size.width * .1, size.height * .05),
      size.width * .43,
      glow,
    );
    final line = Paint()
      ..color = Colors.white.withValues(alpha: .055)
      ..strokeWidth = 1;
    for (double x = -size.height; x < size.width; x += 30) {
      canvas.drawLine(Offset(x, size.height), Offset(x + size.height, 0), line);
    }
  }

  @override
  bool shouldRepaint(covariant _BankPatternPainter oldDelegate) =>
      oldDelegate.palette != palette;
}

class _LogomarkPainter extends CustomPainter {
  const _LogomarkPainter(this.brand, this.color);
  final BankBrand brand;
  final Color color;

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = color
      ..style = PaintingStyle.stroke
      ..strokeCap = StrokeCap.round
      ..strokeJoin = StrokeJoin.round
      ..strokeWidth = size.width * .14;
    final center = Offset(size.width / 2, size.height / 2);
    if (brand == BankBrand.mellat) {
      for (var i = 0; i < 3; i++) {
        final angle = -math.pi / 2 + i * math.pi * 2 / 3;
        final end =
            center + Offset(math.cos(angle), math.sin(angle)) * size.width * .4;
        canvas.drawLine(center, end, paint);
        canvas.drawCircle(end, size.width * .12, paint);
      }
    } else if (brand == BankBrand.melli) {
      canvas.drawCircle(center, size.width * .31, paint);
      canvas.drawLine(
        Offset(center.dx, size.height * .1),
        Offset(center.dx, size.height * .9),
        paint,
      );
    } else if (brand == BankBrand.saderat) {
      canvas.drawArc(
        Rect.fromLTWH(
          0,
          size.height * .06,
          size.width * .55,
          size.height * .86,
        ),
        -math.pi / 2,
        math.pi,
        false,
        paint,
      );
      canvas.drawArc(
        Rect.fromLTWH(
          size.width * .45,
          size.height * .06,
          size.width * .55,
          size.height * .86,
        ),
        math.pi / 2,
        math.pi,
        false,
        paint,
      );
    } else {
      canvas.drawLine(
        Offset(size.width * .15, size.height * .28),
        Offset(size.width * .85, size.height * .28),
        paint,
      );
      canvas.drawLine(
        Offset(size.width * .3, size.height * .12),
        Offset(size.width * .3, size.height * .88),
        paint,
      );
      canvas.drawLine(
        Offset(size.width * .7, size.height * .12),
        Offset(size.width * .7, size.height * .88),
        paint,
      );
      canvas.drawLine(
        Offset(size.width * .15, size.height * .72),
        Offset(size.width * .85, size.height * .72),
        paint,
      );
    }
  }

  @override
  bool shouldRepaint(covariant _LogomarkPainter oldDelegate) =>
      oldDelegate.brand != brand || oldDelegate.color != color;
}

String groupCardNumber(String input) {
  final value = input.replaceAll(RegExp(r'\s'), '');
  return value
      .replaceAllMapped(RegExp(r'.{1,4}'), (match) => '${match.group(0)} ')
      .trim();
}
