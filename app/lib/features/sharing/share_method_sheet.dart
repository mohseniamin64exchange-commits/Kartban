import 'package:flutter/material.dart';

enum CardShareMethod { sms, image }

class ShareMethodSheet extends StatelessWidget {
  const ShareMethodSheet({required this.selectionCount, super.key});
  final int selectionCount;

  static Future<CardShareMethod?> show(
    BuildContext context, {
    required int selectionCount,
  }) => showModalBottomSheet<CardShareMethod>(
    context: context,
    useSafeArea: true,
    backgroundColor: Colors.transparent,
    builder: (_) => ShareMethodSheet(selectionCount: selectionCount),
  );

  @override
  Widget build(BuildContext context) => Directionality(
    textDirection: TextDirection.rtl,
    child: Material(
      color: const Color(0xFFFAFCFF),
      borderRadius: const BorderRadius.vertical(top: Radius.circular(28)),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(18, 12, 18, 18),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Center(
              child: Container(
                width: 42,
                height: 5,
                decoration: BoxDecoration(
                  color: const Color(0xFFD6DEE9),
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
            ),
            const SizedBox(height: 18),
            Text(
              'ارسال $selectionCount کارت',
              textAlign: TextAlign.right,
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.w900,
                color: const Color(0xFF111827),
              ),
            ),
            const SizedBox(height: 6),
            const Text(
              'اطلاعات حساس در هیچ‌کدام از روش‌ها ارسال نمی‌شود.',
              textAlign: TextAlign.right,
              style: TextStyle(color: Color(0xFF667085), fontSize: 12),
            ),
            const SizedBox(height: 14),
            _ShareTile(
              icon: Icons.sms_outlined,
              iconColor: const Color(0xFF11845B),
              title: 'پیامک',
              subtitle: 'مشخصات کارت به‌صورت متن امن',
              onTap: () => Navigator.pop(context, CardShareMethod.sms),
            ),
            const SizedBox(height: 10),
            _ShareTile(
              icon: Icons.image_outlined,
              iconColor: const Color(0xFF1769E0),
              title: 'تلگرام، بله و سایر برنامه‌ها',
              subtitle: 'تصویر کامل و باکیفیت کارت',
              onTap: () => Navigator.pop(context, CardShareMethod.image),
            ),
          ],
        ),
      ),
    ),
  );
}

class _ShareTile extends StatelessWidget {
  const _ShareTile({
    required this.icon,
    required this.iconColor,
    required this.title,
    required this.subtitle,
    required this.onTap,
  });
  final IconData icon;
  final Color iconColor;
  final String title;
  final String subtitle;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) => InkWell(
    onTap: onTap,
    borderRadius: BorderRadius.circular(16),
    child: Ink(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFE1E7F0)),
      ),
      child: Row(
        children: [
          Container(
            width: 44,
            height: 44,
            decoration: BoxDecoration(
              color: iconColor.withValues(alpha: .10),
              borderRadius: BorderRadius.circular(13),
            ),
            child: Icon(icon, color: iconColor),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  textAlign: TextAlign.right,
                  style: const TextStyle(fontWeight: FontWeight.w800),
                ),
                const SizedBox(height: 2),
                Text(
                  subtitle,
                  textAlign: TextAlign.right,
                  style: const TextStyle(
                    color: Color(0xFF667085),
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
          const Icon(Icons.chevron_left_rounded, color: Color(0xFF98A2B3)),
        ],
      ),
    ),
  );
}
