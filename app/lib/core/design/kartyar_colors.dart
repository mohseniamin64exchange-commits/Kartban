import 'package:flutter/material.dart';

abstract final class KartyarColors {
  static const navy950 = Color(0xFF041B3D);
  static const navy900 = Color(0xFF06275A);
  static const navy800 = Color(0xFF0A3678);
  static const electricBlue = Color(0xFF1769E0);
  static const cyan = Color(0xFF12A9C7);
  static const ink = Color(0xFF111827);
  static const slate = Color(0xFF667085);
  static const canvas = Color(0xFFF4F7FB);
  static const surface = Color(0xFFFFFFFF);
  static const success = Color(0xFF11845B);
  static const warning = Color(0xFFC67A12);
  static const danger = Color(0xFFC83B45);
  static const darkCanvas = Color(0xFF07111F);
  static const darkSurface = Color(0xFF0E1B2D);
  static const darkSurfaceRaised = Color(0xFF14243A);
  static const darkInk = Color(0xFFF5F7FB);
  static const darkSlate = Color(0xFFB3BED0);
}

@immutable
class KartyarPalette extends ThemeExtension<KartyarPalette> {
  const KartyarPalette({
    required this.header,
    required this.headerRaised,
    required this.onHeader,
    required this.onHeaderMuted,
    required this.canvas,
    required this.surfaceRaised,
    required this.textPrimary,
    required this.textSecondary,
    required this.outlineSoft,
    required this.success,
    required this.warning,
    required this.danger,
  });

  final Color header;
  final Color headerRaised;
  final Color onHeader;
  final Color onHeaderMuted;
  final Color canvas;
  final Color surfaceRaised;
  final Color textPrimary;
  final Color textSecondary;
  final Color outlineSoft;
  final Color success;
  final Color warning;
  final Color danger;

  static const light = KartyarPalette(
    header: KartyarColors.navy950,
    headerRaised: KartyarColors.navy800,
    onHeader: Colors.white,
    onHeaderMuted: Color(0xFFB8C8E4),
    canvas: KartyarColors.canvas,
    surfaceRaised: KartyarColors.surface,
    textPrimary: KartyarColors.ink,
    textSecondary: KartyarColors.slate,
    outlineSoft: Color(0x1F667085),
    success: KartyarColors.success,
    warning: KartyarColors.warning,
    danger: KartyarColors.danger,
  );

  static const dark = KartyarPalette(
    header: Color(0xFF020F24),
    headerRaised: KartyarColors.navy900,
    onHeader: Colors.white,
    onHeaderMuted: Color(0xFFBBC8DE),
    canvas: KartyarColors.darkCanvas,
    surfaceRaised: KartyarColors.darkSurfaceRaised,
    textPrimary: KartyarColors.darkInk,
    textSecondary: KartyarColors.darkSlate,
    outlineSoft: Color(0x3DB3BED0),
    success: Color(0xFF54C997),
    warning: Color(0xFFF0B35F),
    danger: Color(0xFFFF818A),
  );

  @override
  KartyarPalette copyWith({
    Color? header,
    Color? headerRaised,
    Color? onHeader,
    Color? onHeaderMuted,
    Color? canvas,
    Color? surfaceRaised,
    Color? textPrimary,
    Color? textSecondary,
    Color? outlineSoft,
    Color? success,
    Color? warning,
    Color? danger,
  }) {
    return KartyarPalette(
      header: header ?? this.header,
      headerRaised: headerRaised ?? this.headerRaised,
      onHeader: onHeader ?? this.onHeader,
      onHeaderMuted: onHeaderMuted ?? this.onHeaderMuted,
      canvas: canvas ?? this.canvas,
      surfaceRaised: surfaceRaised ?? this.surfaceRaised,
      textPrimary: textPrimary ?? this.textPrimary,
      textSecondary: textSecondary ?? this.textSecondary,
      outlineSoft: outlineSoft ?? this.outlineSoft,
      success: success ?? this.success,
      warning: warning ?? this.warning,
      danger: danger ?? this.danger,
    );
  }

  @override
  KartyarPalette lerp(ThemeExtension<KartyarPalette>? other, double t) {
    if (other is! KartyarPalette) return this;
    return KartyarPalette(
      header: Color.lerp(header, other.header, t)!,
      headerRaised: Color.lerp(headerRaised, other.headerRaised, t)!,
      onHeader: Color.lerp(onHeader, other.onHeader, t)!,
      onHeaderMuted: Color.lerp(onHeaderMuted, other.onHeaderMuted, t)!,
      canvas: Color.lerp(canvas, other.canvas, t)!,
      surfaceRaised: Color.lerp(surfaceRaised, other.surfaceRaised, t)!,
      textPrimary: Color.lerp(textPrimary, other.textPrimary, t)!,
      textSecondary: Color.lerp(textSecondary, other.textSecondary, t)!,
      outlineSoft: Color.lerp(outlineSoft, other.outlineSoft, t)!,
      success: Color.lerp(success, other.success, t)!,
      warning: Color.lerp(warning, other.warning, t)!,
      danger: Color.lerp(danger, other.danger, t)!,
    );
  }
}

extension KartyarPaletteContext on BuildContext {
  KartyarPalette get kartyarPalette =>
      Theme.of(this).extension<KartyarPalette>() ?? KartyarPalette.light;
}
