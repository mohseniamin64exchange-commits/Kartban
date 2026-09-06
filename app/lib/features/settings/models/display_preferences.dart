import 'package:flutter/material.dart';

enum CardYarTextSize {
  small(0.9, 'کوچک'),
  medium(1, 'متوسط'),
  large(1.12, 'بزرگ');

  const CardYarTextSize(this.scale, this.label);
  final double scale;
  final String label;
}

enum CardYarBrightness {
  light('روشن', Icons.light_mode_outlined),
  dark('تیره', Icons.dark_mode_outlined),
  system('خودکار', Icons.brightness_auto_outlined);

  const CardYarBrightness(this.label, this.icon);
  final String label;
  final IconData icon;
}

@immutable
class DisplayPreferences {
  const DisplayPreferences({
    this.textSize = CardYarTextSize.medium,
    this.brightness = CardYarBrightness.system,
  });

  final CardYarTextSize textSize;
  final CardYarBrightness brightness;

  DisplayPreferences copyWith({
    CardYarTextSize? textSize,
    CardYarBrightness? brightness,
  }) => DisplayPreferences(
    textSize: textSize ?? this.textSize,
    brightness: brightness ?? this.brightness,
  );
}
