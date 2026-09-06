import 'package:flutter/material.dart';

import 'settings_chrome.dart';

class AboutCardYarScreen extends StatelessWidget {
  const AboutCardYarScreen({
    required this.version,
    this.buildNumber,
    this.onOpenLicenses,
    super.key,
  });

  final String version;
  final String? buildNumber;
  final VoidCallback? onOpenLicenses;

  @override
  Widget build(BuildContext context) {
    final versionText = buildNumber == null
        ? 'نسخه $version'
        : 'نسخه $version • ساخت $buildNumber';
    return SettingsPageScaffold(
      title: 'درباره کارت‌یار',
      subtitle:
          'دفترچه امن و سریع اطلاعات بانکی برای استفاده روزمره، بدون وابستگی به اینترنت.',
      icon: Icons.credit_card_rounded,
      children: [
        Container(
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(
            gradient: const LinearGradient(
              colors: [Color(0xFF041B3D), Color(0xFF0A3678)],
            ),
            borderRadius: BorderRadius.circular(20),
          ),
          child: Column(
            children: [
              Container(
                width: 72,
                height: 72,
                decoration: BoxDecoration(
                  color: Colors.white.withValues(alpha: 0.12),
                  borderRadius: BorderRadius.circular(22),
                  border: Border.all(
                    color: Colors.white.withValues(alpha: 0.18),
                  ),
                ),
                child: const Icon(
                  Icons.wallet_rounded,
                  color: Colors.white,
                  size: 38,
                ),
              ),
              const SizedBox(height: 12),
              Text(
                'کارت‌یار',
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: Colors.white,
                  fontWeight: FontWeight.w900,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                versionText,
                textAlign: TextAlign.center,
                style: Theme.of(
                  context,
                ).textTheme.bodyMedium?.copyWith(color: Colors.white70),
              ),
            ],
          ),
        ),
        const SizedBox(height: 22),
        const SettingsSection(
          title: 'اصل طراحی محصول',
          children: [
            SettingsRow(
              title: 'حریم خصوصی از ابتدا',
              subtitle:
                  'داده‌ها روی گوشی شما می‌مانند و خروجی‌ها پیش از ارسال پالایش می‌شوند.',
              icon: Icons.verified_user_outlined,
            ),
            SettingsRow(
              title: 'ساخته‌شده برای فارسی',
              subtitle:
                  'چیدمان راست‌به‌چپ و اندازه‌های نوشته کوچک، متوسط و بزرگ.',
              icon: Icons.translate_rounded,
            ),
            SettingsRow(
              title: 'بدون اینترنت',
              subtitle: 'همه قابلیت‌های اصلی به‌صورت محلی کار می‌کنند.',
              icon: Icons.offline_bolt_outlined,
            ),
          ],
        ),
        if (onOpenLicenses != null)
          SettingsSection(
            title: 'اطلاعات حقوقی',
            children: [
              SettingsRow(
                title: 'مجوزهای نرم‌افزاری',
                icon: Icons.description_outlined,
                onTap: onOpenLicenses,
                trailing: const Icon(Icons.chevron_left_rounded, color: slate),
              ),
            ],
          ),
      ],
    );
  }
}
