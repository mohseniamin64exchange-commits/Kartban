import 'package:flutter/material.dart';

abstract final class KartyarSpacing {
  static const xxs = 4.0;
  static const xs = 8.0;
  static const sm = 12.0;
  static const md = 16.0;
  static const lg = 20.0;
  static const xl = 24.0;
  static const xxl = 32.0;
  static const screenPadding = EdgeInsets.symmetric(horizontal: md);
}

abstract final class KartyarRadii {
  static const control = 12.0;
  static const row = 18.0;
  static const bankCard = 20.0;
  static const sheet = 28.0;
  static const controlBorder = BorderRadius.all(Radius.circular(control));
  static const rowBorder = BorderRadius.all(Radius.circular(row));
  static const bankCardBorder = BorderRadius.all(Radius.circular(bankCard));
  static const sheetBorder = BorderRadius.all(Radius.circular(sheet));
}

abstract final class KartyarElevation {
  static const none = 0.0;
  static const selected = 1.0;
  static const floating = 3.0;

  static List<BoxShadow> softRow(Color color) => [
    BoxShadow(
      color: color.withValues(alpha: 0.07),
      blurRadius: 18,
      offset: const Offset(0, 6),
    ),
  ];
}

abstract final class KartyarMotion {
  static const quick = Duration(milliseconds: 160);
  static const standard = Duration(milliseconds: 200);
  static const emphasized = Duration(milliseconds: 240);
  static const curve = Curves.easeOutCubic;

  static Duration adaptive(BuildContext context, Duration duration) {
    return MediaQuery.maybeOf(context)?.disableAnimations == true
        ? Duration.zero
        : duration;
  }
}

abstract final class KartyarSizes {
  static const minimumTouchTarget = 48.0;
  static const compactButtonHeight = 52.0;
  static const bankBadgeHeight = 32.0;
  static const personAvatar = 52.0;
}
