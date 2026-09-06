import 'package:flutter/material.dart';

enum KartyarTextScale { small, medium, large }

extension KartyarTextScaleValue on KartyarTextScale {
  double get factor => switch (this) {
    KartyarTextScale.small => 0.90,
    KartyarTextScale.medium => 1.0,
    KartyarTextScale.large => 1.12,
  };
}

abstract final class KartyarTypography {
  static const _fallbacks = <String>['Vazirmatn', 'Noto Sans Arabic', 'Tahoma'];

  static TextTheme textTheme(
    Brightness brightness, {
    KartyarTextScale scale = KartyarTextScale.medium,
  }) {
    final factor = scale.factor;
    final foreground = brightness == Brightness.dark
        ? const Color(0xFFF5F7FB)
        : const Color(0xFF111827);

    TextStyle style(double size, FontWeight weight, {double height = 1.45}) {
      return TextStyle(
        color: foreground,
        fontFamilyFallback: _fallbacks,
        fontSize: size * factor,
        fontWeight: weight,
        height: height,
      );
    }

    return TextTheme(
      displaySmall: style(28, FontWeight.w700, height: 1.28),
      headlineMedium: style(24, FontWeight.w700, height: 1.32),
      headlineSmall: style(20, FontWeight.w700, height: 1.35),
      titleLarge: style(20, FontWeight.w700, height: 1.4),
      titleMedium: style(16, FontWeight.w700),
      titleSmall: style(14, FontWeight.w700),
      bodyLarge: style(16, FontWeight.w400),
      bodyMedium: style(14, FontWeight.w400),
      bodySmall: style(12, FontWeight.w400, height: 1.4),
      labelLarge: style(14, FontWeight.w700, height: 1.3),
      labelMedium: style(12, FontWeight.w600, height: 1.3),
      labelSmall: style(11, FontWeight.w600, height: 1.25),
    );
  }

  static TextScaler safeScaler(BuildContext context) {
    final requested = MediaQuery.textScalerOf(context).scale(1);
    return TextScaler.linear(requested.clamp(0.90, 1.30));
  }
}
