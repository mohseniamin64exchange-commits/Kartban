import 'package:flutter/material.dart';

import '../models/security_preferences.dart';
import 'settings_chrome.dart';

class SecuritySettingsScreen extends StatelessWidget {
  const SecuritySettingsScreen({
    required this.preferences,
    required this.onAppLockChanged,
    required this.onMethodChanged,
    required this.onBiometricChanged,
    required this.onAutoLockChanged,
    required this.onChangeCredential,
    this.onLockNow,
    super.key,
  });

  final SecurityPreferences preferences;
  final ValueChanged<bool> onAppLockChanged;
  final ValueChanged<AppLockMethod> onMethodChanged;
  final ValueChanged<bool> onBiometricChanged;
  final ValueChanged<AutoLockDelay> onAutoLockChanged;
  final VoidCallback onChangeCredential;
  final VoidCallback? onLockNow;

  @override
  Widget build(BuildContext context) {
    return SettingsPageScaffold(
      title: 'امنیت و قفل برنامه',
      subtitle:
          'دسترسی به کارت‌یار و نمایش اطلاعات حساس را با روش امن دستگاه کنترل کنید.',
      icon: Icons.shield_outlined,
      children: [
        const SettingsNotice(
          text:
              'اگر کارت شخصی دارای اطلاعات حساس ذخیره شود، فعال بودن حداقل یک روش قفل الزامی است.',
          icon: Icons.lock_person_outlined,
        ),
        const SizedBox(height: 22),
        SettingsSection(
          title: 'قفل برنامه',
          children: [
            SettingsRow(
              title: 'فعال‌سازی قفل ورود',
              subtitle: 'هنگام ورود دوباره هویت شما بررسی می‌شود.',
              icon: Icons.lock_outline_rounded,
              trailing: Switch.adaptive(
                value: preferences.appLockEnabled,
                onChanged: onAppLockChanged,
              ),
            ),
            SettingsRow(
              title: 'روش اصلی ورود',
              subtitle: preferences.method.label,
              icon: preferences.method == AppLockMethod.pattern
                  ? Icons.pattern_rounded
                  : Icons.password_rounded,
              enabled: preferences.appLockEnabled,
              onTap: preferences.appLockEnabled
                  ? () => _chooseMethod(context)
                  : null,
              trailing: const Icon(Icons.chevron_left_rounded, color: slate),
            ),
            SettingsRow(
              title: 'تغییر ${preferences.method.label}',
              subtitle: 'برای تغییر، هویت فعلی دوباره بررسی می‌شود.',
              icon: Icons.key_rounded,
              enabled: preferences.appLockEnabled,
              onTap: preferences.appLockEnabled ? onChangeCredential : null,
            ),
          ],
        ),
        SettingsSection(
          title: 'اثر انگشت',
          children: [
            SettingsRow(
              title: 'باز کردن با اثر انگشت',
              subtitle: preferences.biometricAvailable
                  ? 'روش سریع در کنار رمز یا الگو'
                  : 'در این دستگاه آماده نیست',
              icon: Icons.fingerprint_rounded,
              enabled:
                  preferences.appLockEnabled && preferences.biometricAvailable,
              trailing: Switch.adaptive(
                value:
                    preferences.biometricEnabled &&
                    preferences.biometricAvailable,
                onChanged:
                    preferences.appLockEnabled && preferences.biometricAvailable
                    ? onBiometricChanged
                    : null,
              ),
            ),
          ],
        ),
        SettingsSection(
          title: 'قفل خودکار',
          description:
              'پس از خروج از برنامه، دسترسی باز برای همیشه باقی نمی‌ماند.',
          children: [
            SettingsRow(
              title: 'زمان قفل شدن',
              subtitle: preferences.autoLockDelay.label,
              icon: Icons.timer_outlined,
              onTap: () => _chooseDelay(context),
              trailing: const Icon(Icons.chevron_left_rounded, color: slate),
            ),
            if (onLockNow != null)
              SettingsRow(
                title: 'همین حالا قفل کن',
                subtitle: 'کارت‌یار فوراً به صفحه احراز هویت می‌رود.',
                icon: Icons.lock_clock_outlined,
                onTap: onLockNow,
              ),
          ],
        ),
        const SettingsNotice(
          text:
              'مسیر «فراموشی رمز» هنوز تعریف نشده است؛ کارت‌یار هیچ راه پشتی یا بازیابی ابری ایجاد نمی‌کند.',
          icon: Icons.cloud_off_outlined,
          color: warning,
        ),
      ],
    );
  }

  Future<void> _chooseMethod(BuildContext context) async {
    final selected = await showModalBottomSheet<AppLockMethod>(
      context: context,
      showDragHandle: true,
      builder: (context) => Directionality(
        textDirection: TextDirection.rtl,
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(16, 4, 16, 18),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  'روش اصلی ورود',
                  textAlign: TextAlign.right,
                  style: Theme.of(
                    context,
                  ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w800),
                ),
                const SizedBox(height: 10),
                for (final method in AppLockMethod.values)
                  RadioListTile<AppLockMethod>(
                    value: method,
                    groupValue: preferences.method,
                    title: Text(method.label, textAlign: TextAlign.right),
                    onChanged: (value) => Navigator.pop(context, value),
                  ),
              ],
            ),
          ),
        ),
      ),
    );
    if (selected != null) onMethodChanged(selected);
  }

  Future<void> _chooseDelay(BuildContext context) async {
    final selected = await showModalBottomSheet<AutoLockDelay>(
      context: context,
      showDragHandle: true,
      builder: (context) => Directionality(
        textDirection: TextDirection.rtl,
        child: SafeArea(
          child: ListView(
            shrinkWrap: true,
            padding: const EdgeInsets.fromLTRB(16, 4, 16, 18),
            children: [
              Text(
                'زمان قفل خودکار',
                textAlign: TextAlign.right,
                style: Theme.of(
                  context,
                ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w800),
              ),
              const SizedBox(height: 8),
              for (final delay in AutoLockDelay.values)
                RadioListTile<AutoLockDelay>(
                  value: delay,
                  groupValue: preferences.autoLockDelay,
                  title: Text(delay.label, textAlign: TextAlign.right),
                  onChanged: (value) => Navigator.pop(context, value),
                ),
            ],
          ),
        ),
      ),
    );
    if (selected != null) onAutoLockChanged(selected);
  }
}
