import 'package:flutter/material.dart';

import 'settings_chrome.dart';

class PrivacyCenterScreen extends StatelessWidget {
  const PrivacyCenterScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const SettingsPageScaffold(
      title: 'مرکز حریم خصوصی',
      subtitle:
          'کارت‌یار داده‌ها را فقط روی همین گوشی نگه می‌دارد و بدون اجازه چیزی ارسال نمی‌کند.',
      icon: Icons.privacy_tip_outlined,
      children: [
        SettingsNotice(
          text:
              'این برنامه کاملاً آفلاین است: بدون حساب کاربری، تبلیغ، تحلیل‌گر و همگام‌سازی ابری.',
          icon: Icons.phonelink_lock_outlined,
          color: success,
        ),
        SizedBox(height: 22),
        SettingsSection(
          title: 'داده‌های شما',
          children: [
            SettingsRow(
              title: 'ذخیره‌سازی روی دستگاه',
              subtitle:
                  'اطلاعات عادی در پایگاه محلی و اطلاعات حساس کارت شخصی به‌صورت رمزنگاری‌شده نگه‌داری می‌شود.',
              icon: Icons.smartphone_outlined,
            ),
            SettingsRow(
              title: 'بدون دسترسی اینترنت',
              subtitle:
                  'کارت‌یار برای کارکرد اصلی هیچ اتصال شبکه‌ای درخواست نمی‌کند.',
              icon: Icons.wifi_off_rounded,
            ),
            SettingsRow(
              title: 'بدون پشتیبان خودکار سیستم',
              subtitle:
                  'انتقال خودکار داده برنامه و Android Auto Backup غیرفعال است.',
              icon: Icons.cloud_off_outlined,
            ),
          ],
        ),
        SettingsSection(
          title: 'اطلاعات حساس',
          children: [
            SettingsRow(
              title: 'فقط کارت شخصی',
              subtitle:
                  'CVV2 و تاریخ انقضا برای کارت افراد دیگر ذخیره نمی‌شود.',
              icon: Icons.credit_card_off_outlined,
            ),
            SettingsRow(
              title: 'نمایش موقت و محافظت‌شده',
              subtitle:
                  'نمایش پس از احراز هویت انجام می‌شود و اسکرین‌شات، ضبط و پیش‌نمایش برنامه مسدود می‌شود.',
              icon: Icons.visibility_off_outlined,
            ),
            SettingsRow(
              title: 'هرگز در اشتراک‌گذاری نیست',
              subtitle:
                  'CVV2 و انقضا در پیامک، تصویر، خروجی، اعلان و خلاصه پشتیبان قرار نمی‌گیرد.',
              icon: Icons.ios_share_outlined,
            ),
          ],
        ),
        SettingsSection(
          title: 'اشتراک‌گذاری کنترل‌شده',
          children: [
            SettingsRow(
              title: 'پیامک',
              subtitle:
                  'فقط نام بانک، صاحب کارت، شماره کارت، حساب و شبا به شکل متن ساخته می‌شود.',
              icon: Icons.sms_outlined,
            ),
            SettingsRow(
              title: 'پیام‌رسان‌ها',
              subtitle:
                  'یک تصویر محلی از کارت با فیلدهای عادی ساخته و به صفحه اشتراک‌گذاری گوشی تحویل می‌شود.',
              icon: Icons.image_outlined,
            ),
            SettingsRow(
              title: 'حافظه موقت',
              subtitle:
                  'متن کپی‌شده پس از مهلت کوتاه به‌طور خودکار پاک می‌شود.',
              icon: Icons.content_paste_off_outlined,
            ),
          ],
        ),
      ],
    );
  }
}
