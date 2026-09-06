import 'package:flutter/material.dart';

import 'home_models.dart';

class CardYarDrawer extends StatelessWidget {
  const CardYarDrawer({required this.actions, super.key});
  final HomeActions actions;

  @override
  Widget build(BuildContext context) {
    void run(VoidCallback action) {
      Navigator.of(context).pop();
      action();
    }

    return Directionality(
      textDirection: TextDirection.rtl,
      child: Drawer(
        width: MediaQuery.sizeOf(context).width.clamp(280, 340).toDouble(),
        shape: const RoundedRectangleBorder(
          borderRadius: BorderRadiusDirectional.horizontal(
            start: Radius.circular(28),
          ),
        ),
        backgroundColor: const Color(0xFFF7F9FC),
        child: SafeArea(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const _DrawerHeader(),
              Expanded(
                child: ListView(
                  padding: const EdgeInsets.fromLTRB(12, 8, 12, 12),
                  children: [
                    _DrawerItem(
                      icon: Icons.lock_rounded,
                      title: 'قفل فوری',
                      subtitle: 'همین حالا برنامه را قفل کن',
                      emphasized: true,
                      onTap: () => run(actions.lockNow),
                    ),
                    const SizedBox(height: 7),
                    _DrawerItem(
                      icon: Icons.shield_outlined,
                      title: 'امنیت و قفل برنامه',
                      subtitle: 'رمز، پترن، اثر انگشت و قفل خودکار',
                      onTap: () => run(actions.openSecurity),
                    ),
                    _DrawerItem(
                      icon: Icons.health_and_safety_outlined,
                      title: 'پشتیبان‌گیری و بازیابی',
                      subtitle: 'آزمایش سلامت، پشتیبان و بازیابی امن',
                      onTap: () => run(actions.openBackup),
                    ),
                    _DrawerItem(
                      icon: Icons.privacy_tip_outlined,
                      title: 'مرکز حریم خصوصی',
                      subtitle: 'نحوه نگهداری و اشتراک اطلاعات',
                      onTap: () => run(actions.openPrivacyCenter),
                    ),
                    const Padding(
                      padding: EdgeInsets.symmetric(vertical: 8),
                      child: Divider(height: 1, color: Color(0xFFE3E8F0)),
                    ),
                    _DrawerItem(
                      icon: Icons.text_fields_rounded,
                      title: 'تنظیمات نمایش',
                      subtitle: 'اندازه نوشته و پوسته برنامه',
                      onTap: () => run(actions.openDisplaySettings),
                    ),
                    _DrawerItem(
                      icon: Icons.info_outline_rounded,
                      title: 'درباره کارت‌یار',
                      subtitle: 'نسخه ۱.۰.۰',
                      onTap: () => run(actions.openAbout),
                    ),
                  ],
                ),
              ),
              const Padding(
                padding: EdgeInsets.fromLTRB(22, 8, 22, 18),
                child: Text(
                  'تمام اطلاعات فقط روی همین دستگاه نگهداری می‌شود.',
                  textAlign: TextAlign.right,
                  style: TextStyle(
                    color: Color(0xFF7B8799),
                    fontSize: 11,
                    height: 1.6,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _DrawerHeader extends StatelessWidget {
  const _DrawerHeader();

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(12),
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(22),
        gradient: const LinearGradient(
          begin: Alignment.topRight,
          end: Alignment.bottomLeft,
          colors: [Color(0xFF0A3678), Color(0xFF041B3D)],
        ),
      ),
      child: const Row(
        children: [
          _MiniCardLogo(),
          SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  'کارت‌یار',
                  textAlign: TextAlign.right,
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.w800,
                  ),
                ),
                SizedBox(height: 2),
                Text(
                  'مدیریت امن کارت‌های بانکی',
                  textAlign: TextAlign.right,
                  style: TextStyle(color: Color(0xFFC7D8F3), fontSize: 11),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _DrawerItem extends StatelessWidget {
  const _DrawerItem({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.onTap,
    this.emphasized = false,
  });
  final IconData icon;
  final String title;
  final String subtitle;
  final VoidCallback onTap;
  final bool emphasized;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      minTileHeight: 66,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      tileColor: emphasized ? const Color(0xFFE8F1FF) : Colors.transparent,
      leading: Container(
        width: 40,
        height: 40,
        decoration: BoxDecoration(
          color: emphasized ? const Color(0xFF1769E0) : Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: emphasized
              ? null
              : Border.all(color: const Color(0xFFE2E8F0)),
        ),
        child: Icon(
          icon,
          size: 21,
          color: emphasized ? Colors.white : const Color(0xFF264F88),
        ),
      ),
      title: Text(
        title,
        textAlign: TextAlign.right,
        style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w700),
      ),
      subtitle: Text(
        subtitle,
        maxLines: 2,
        overflow: TextOverflow.ellipsis,
        textAlign: TextAlign.right,
        style: const TextStyle(
          color: Color(0xFF7A8698),
          fontSize: 10.5,
          height: 1.45,
        ),
      ),
      trailing: const Icon(
        Icons.chevron_left_rounded,
        color: Color(0xFF98A3B3),
      ),
      onTap: onTap,
    );
  }
}

class _MiniCardLogo extends StatelessWidget {
  const _MiniCardLogo();

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 48,
      height: 38,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(9),
        boxShadow: const [
          BoxShadow(
            color: Color(0x33000000),
            blurRadius: 8,
            offset: Offset(0, 4),
          ),
        ],
      ),
      child: const Icon(Icons.credit_card_rounded, color: Color(0xFF0A3678)),
    );
  }
}
