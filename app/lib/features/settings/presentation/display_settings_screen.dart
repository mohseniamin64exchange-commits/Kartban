import 'package:flutter/material.dart';

import '../models/display_preferences.dart';
import 'settings_chrome.dart';

class DisplaySettingsScreen extends StatelessWidget {
  const DisplaySettingsScreen({
    required this.preferences,
    required this.onTextSizeChanged,
    required this.onBrightnessChanged,
    super.key,
  });

  final DisplayPreferences preferences;
  final ValueChanged<CardYarTextSize> onTextSizeChanged;
  final ValueChanged<CardYarBrightness> onBrightnessChanged;

  @override
  Widget build(BuildContext context) {
    return SettingsPageScaffold(
      title: 'تنظیمات نمایش',
      subtitle:
          'اندازه نوشته و ظاهر برنامه را تنظیم کنید؛ چیدمان در هر سه اندازه پایدار می‌ماند.',
      icon: Icons.tune_rounded,
      children: [
        SettingsSection(
          title: 'اندازه نوشته',
          description: 'تغییر اندازه روی تمام متن‌های برنامه اعمال می‌شود.',
          children: [
            Padding(
              padding: const EdgeInsets.all(12),
              child: SegmentedButton<CardYarTextSize>(
                showSelectedIcon: false,
                segments: [
                  for (final size in CardYarTextSize.values)
                    ButtonSegment(value: size, label: Text(size.label)),
                ],
                selected: {preferences.textSize},
                onSelectionChanged: (value) => onTextSizeChanged(value.first),
              ),
            ),
          ],
        ),
        SettingsSection(
          title: 'حالت رنگ',
          children: [
            for (final brightness in CardYarBrightness.values)
              SettingsRow(
                title: brightness.label,
                subtitle: brightness == CardYarBrightness.system
                    ? 'هماهنگ با تنظیمات گوشی'
                    : brightness == CardYarBrightness.dark
                    ? 'مناسب محیط کم‌نور'
                    : 'پس‌زمینه روشن و خوانا',
                icon: brightness.icon,
                onTap: () => onBrightnessChanged(brightness),
                trailing: Radio<CardYarBrightness>(
                  value: brightness,
                  groupValue: preferences.brightness,
                  onChanged: (value) {
                    if (value != null) onBrightnessChanged(value);
                  },
                ),
              ),
          ],
        ),
        _DisplayPreview(preferences: preferences),
      ],
    );
  }
}

class _DisplayPreview extends StatelessWidget {
  const _DisplayPreview({required this.preferences});

  final DisplayPreferences preferences;

  @override
  Widget build(BuildContext context) {
    final isDark = preferences.brightness == CardYarBrightness.dark;
    final surface = isDark ? const Color(0xFF0A2142) : Colors.white;
    final primaryText = isDark ? Colors.white : ink;
    final secondaryText = isDark ? Colors.white70 : slate;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Text(
          'پیش‌نمایش',
          textAlign: TextAlign.right,
          style: Theme.of(context).textTheme.titleMedium?.copyWith(
            color: ink,
            fontWeight: FontWeight.w800,
          ),
        ),
        const SizedBox(height: 10),
        AnimatedContainer(
          duration: const Duration(milliseconds: 180),
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: surface,
            borderRadius: BorderRadius.circular(18),
            border: Border.all(color: navy.withValues(alpha: 0.1)),
          ),
          child: MediaQuery.withClampedTextScaling(
            minScaleFactor: preferences.textSize.scale,
            maxScaleFactor: preferences.textSize.scale,
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'علی رضایی',
                        textAlign: TextAlign.right,
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                        style: Theme.of(context).textTheme.titleMedium
                            ?.copyWith(
                              color: primaryText,
                              fontWeight: FontWeight.w800,
                            ),
                      ),
                      const SizedBox(height: 3),
                      Text(
                        '۳ کارت بانکی',
                        textAlign: TextAlign.right,
                        style: Theme.of(
                          context,
                        ).textTheme.bodySmall?.copyWith(color: secondaryText),
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: 12),
                Container(
                  width: 44,
                  height: 44,
                  decoration: const BoxDecoration(
                    color: Color(0xFFDCE9FF),
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(Icons.person_rounded, color: electricBlue),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
