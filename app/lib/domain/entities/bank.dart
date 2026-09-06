enum IranianBank {
  melli('ملی', 'MELLI'),
  mellat('ملت', 'MELLAT'),
  saderat('صادرات', 'SADERAT'),
  tejarat('تجارت', 'TEJARAT'),
  pasargad('پاسارگاد', 'PASARGAD'),
  iranZamin('ایران زمین', 'IRAN ZAMIN'),
  unknown('بانک دیگر', 'BANK');

  const IranianBank(this.persianName, this.latinName);
  final String persianName;
  final String latinName;
}
