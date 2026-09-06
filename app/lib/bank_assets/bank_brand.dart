import 'package:flutter/material.dart';

enum BankBrand { melli, mellat, saderat, pasargad, tejarat }

class BankBrandData {
  const BankBrandData(this.title, this.shortName, this.color, this.mark);
  final String title, shortName, mark;
  final Color color;
}

BankBrandData bankBrandData(BankBrand brand) {
  switch (brand) {
    case BankBrand.melli:
      return const BankBrandData(
        'بانک ملی ایران',
        'ملی',
        Color(0xFF0A3678),
        'م',
      );
    case BankBrand.mellat:
      return const BankBrandData('بانک ملت', 'ملت', Color(0xFFB51F32), 'م');
    case BankBrand.saderat:
      return const BankBrandData(
        'بانک صادرات ایران',
        'صادرات',
        Color(0xFF087EA4),
        'ص',
      );
    case BankBrand.pasargad:
      return const BankBrandData(
        'بانک پاسارگاد',
        'پاسارگاد',
        Color(0xFF263E88),
        'پ',
      );
    case BankBrand.tejarat:
      return const BankBrandData('بانک تجارت', 'تجارت', Color(0xFF008B73), 'ت');
  }
}

class BankMark extends StatelessWidget {
  const BankMark({super.key, required this.brand, this.size = 42});
  final BankBrand brand;
  final double size;
  @override
  Widget build(BuildContext context) {
    final data = bankBrandData(brand);
    return Container(
      width: size,
      height: size,
      alignment: Alignment.center,
      decoration: BoxDecoration(
        color: data.color,
        borderRadius: BorderRadius.circular(size * .28),
      ),
      child: Text(
        data.mark,
        style: TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.w800,
          fontSize: size * .42,
        ),
      ),
    );
  }
}
