import 'package:flutter/material.dart';

import 'kartyar_colors.dart';
import 'kartyar_tokens.dart';
import 'kartyar_typography.dart';

abstract final class KartyarTheme {
  static ThemeData light({
    KartyarTextScale textScale = KartyarTextScale.medium,
  }) => _build(Brightness.light, textScale);

  static ThemeData dark({
    KartyarTextScale textScale = KartyarTextScale.medium,
  }) => _build(Brightness.dark, textScale);

  static ThemeData _build(Brightness brightness, KartyarTextScale textScale) {
    final isDark = brightness == Brightness.dark;
    final palette = isDark ? KartyarPalette.dark : KartyarPalette.light;
    final scheme = ColorScheme(
      brightness: brightness,
      primary: KartyarColors.electricBlue,
      onPrimary: Colors.white,
      primaryContainer: isDark
          ? const Color(0xFF123C73)
          : const Color(0xFFDCEAFF),
      onPrimaryContainer: isDark
          ? const Color(0xFFE7F0FF)
          : KartyarColors.navy950,
      secondary: KartyarColors.cyan,
      onSecondary: KartyarColors.navy950,
      secondaryContainer: isDark
          ? const Color(0xFF0A4654)
          : const Color(0xFFD5F5FA),
      onSecondaryContainer: isDark ? Colors.white : KartyarColors.navy950,
      tertiary: palette.success,
      onTertiary: Colors.white,
      error: palette.danger,
      onError: Colors.white,
      surface: isDark ? KartyarColors.darkSurface : KartyarColors.surface,
      onSurface: palette.textPrimary,
      outline: isDark ? const Color(0xFF718096) : const Color(0xFF98A2B3),
      outlineVariant: palette.outlineSoft,
      shadow: KartyarColors.navy950,
      scrim: Colors.black,
      inverseSurface: isDark ? KartyarColors.surface : KartyarColors.ink,
      onInverseSurface: isDark ? KartyarColors.ink : Colors.white,
      inversePrimary: isDark
          ? const Color(0xFF84B2FF)
          : const Color(0xFF8DB8FF),
      surfaceTint: KartyarColors.electricBlue,
    );
    final textTheme = KartyarTypography.textTheme(brightness, scale: textScale);
    final base = ThemeData(
      useMaterial3: true,
      brightness: brightness,
      colorScheme: scheme,
      scaffoldBackgroundColor: palette.canvas,
      textTheme: textTheme,
      extensions: [palette],
      visualDensity: VisualDensity.standard,
      materialTapTargetSize: MaterialTapTargetSize.padded,
      splashFactory: InkSparkle.splashFactory,
    );

    return base.copyWith(
      appBarTheme: AppBarTheme(
        backgroundColor: palette.canvas,
        foregroundColor: palette.textPrimary,
        elevation: 0,
        scrolledUnderElevation: 0,
        centerTitle: false,
        titleTextStyle: textTheme.titleLarge,
        surfaceTintColor: Colors.transparent,
      ),
      cardTheme: CardThemeData(
        color: palette.surfaceRaised,
        elevation: KartyarElevation.none,
        margin: EdgeInsets.zero,
        shape: RoundedRectangleBorder(
          borderRadius: KartyarRadii.rowBorder,
          side: BorderSide(color: palette.outlineSoft),
        ),
      ),
      dividerTheme: DividerThemeData(
        color: palette.outlineSoft,
        thickness: 1,
        space: 1,
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: palette.surfaceRaised,
        contentPadding: const EdgeInsets.symmetric(
          horizontal: 16,
          vertical: 14,
        ),
        hintStyle: textTheme.bodyMedium?.copyWith(color: palette.textSecondary),
        errorStyle: textTheme.bodySmall?.copyWith(color: palette.danger),
        border: _inputBorder(palette.outlineSoft),
        enabledBorder: _inputBorder(palette.outlineSoft),
        focusedBorder: _inputBorder(scheme.primary, width: 1.5),
        errorBorder: _inputBorder(palette.danger),
        focusedErrorBorder: _inputBorder(palette.danger, width: 1.5),
      ),
      filledButtonTheme: FilledButtonThemeData(
        style: ButtonStyle(
          minimumSize: const WidgetStatePropertyAll(
            Size(48, KartyarSizes.minimumTouchTarget),
          ),
          padding: const WidgetStatePropertyAll(
            EdgeInsets.symmetric(horizontal: 20, vertical: 12),
          ),
          shape: const WidgetStatePropertyAll(
            RoundedRectangleBorder(borderRadius: KartyarRadii.controlBorder),
          ),
          textStyle: WidgetStatePropertyAll(textTheme.labelLarge),
          elevation: const WidgetStatePropertyAll(0),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: ButtonStyle(
          minimumSize: const WidgetStatePropertyAll(
            Size(48, KartyarSizes.minimumTouchTarget),
          ),
          shape: const WidgetStatePropertyAll(
            RoundedRectangleBorder(borderRadius: KartyarRadii.controlBorder),
          ),
          side: WidgetStateProperty.resolveWith((states) {
            final color = states.contains(WidgetState.disabled)
                ? palette.outlineSoft
                : scheme.primary;
            return BorderSide(color: color);
          }),
          textStyle: WidgetStatePropertyAll(textTheme.labelLarge),
        ),
      ),
      iconButtonTheme: const IconButtonThemeData(
        style: ButtonStyle(
          minimumSize: WidgetStatePropertyAll(Size.square(48)),
        ),
      ),
      bottomSheetTheme: BottomSheetThemeData(
        backgroundColor: palette.surfaceRaised,
        modalBackgroundColor: palette.surfaceRaised,
        showDragHandle: true,
        shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
        ),
      ),
      snackBarTheme: SnackBarThemeData(
        backgroundColor: isDark
            ? KartyarColors.darkSurfaceRaised
            : KartyarColors.ink,
        contentTextStyle: textTheme.bodyMedium?.copyWith(color: Colors.white),
        behavior: SnackBarBehavior.floating,
        shape: const RoundedRectangleBorder(
          borderRadius: KartyarRadii.controlBorder,
        ),
      ),
      progressIndicatorTheme: const ProgressIndicatorThemeData(
        color: KartyarColors.electricBlue,
      ),
    );
  }

  static OutlineInputBorder _inputBorder(Color color, {double width = 1}) {
    return OutlineInputBorder(
      borderRadius: KartyarRadii.controlBorder,
      borderSide: BorderSide(color: color, width: width),
    );
  }
}
