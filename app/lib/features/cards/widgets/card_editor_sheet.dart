import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../models/card_view_data.dart';

class CardEditorSheet extends StatefulWidget {
  const CardEditorSheet({
    required this.holderName,
    this.initialCard,
    super.key,
  });
  final String holderName;
  final CardViewData? initialCard;

  static Future<CardEditDraft?> show(
    BuildContext context, {
    required String holderName,
    CardViewData? initialCard,
  }) => showModalBottomSheet<CardEditDraft>(
    context: context,
    isScrollControlled: true,
    useSafeArea: true,
    backgroundColor: Colors.transparent,
    builder: (_) =>
        CardEditorSheet(holderName: holderName, initialCard: initialCard),
  );

  @override
  State<CardEditorSheet> createState() => _CardEditorSheetState();
}

class _CardEditorSheetState extends State<CardEditorSheet> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _cardNumber;
  late final TextEditingController _account;
  late final TextEditingController _iban;
  late final TextEditingController _cvv2;
  late final TextEditingController _expiry;
  late BankBrand _bank;
  late CardOwnership _ownership;

  @override
  void initState() {
    super.initState();
    final card = widget.initialCard;
    _bank = card?.bank ?? BankBrand.melli;
    _ownership = card?.ownership ?? CardOwnership.customer;
    _cardNumber = TextEditingController(text: card?.cardNumber);
    _account = TextEditingController(text: card?.accountNumber);
    _iban = TextEditingController(text: card?.iban);
    _cvv2 = TextEditingController();
    _expiry = TextEditingController();
  }

  @override
  void dispose() {
    _cardNumber.dispose();
    _account.dispose();
    _iban.dispose();
    _cvv2.clear();
    _cvv2.dispose();
    _expiry.clear();
    _expiry.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final editing = widget.initialCard != null;
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Material(
        color: const Color(0xFFF9FBFE),
        borderRadius: const BorderRadius.vertical(top: Radius.circular(28)),
        clipBehavior: Clip.antiAlias,
        child: Padding(
          padding: EdgeInsets.only(
            left: 18,
            right: 18,
            top: 12,
            bottom: MediaQuery.viewInsetsOf(context).bottom + 16,
          ),
          child: Form(
            key: _formKey,
            child: ListView(
              shrinkWrap: true,
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
                const SizedBox(height: 16),
                Text(
                  editing ? 'ویرایش کارت' : 'افزودن کارت',
                  textAlign: TextAlign.right,
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: const Color(0xFF111827),
                    fontWeight: FontWeight.w900,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  'برای ${widget.holderName}',
                  textAlign: TextAlign.right,
                  style: const TextStyle(color: Color(0xFF667085)),
                ),
                const SizedBox(height: 18),
                DropdownButtonFormField<BankBrand>(
                  initialValue: _bank,
                  decoration: decoration('بانک', Icons.account_balance_rounded),
                  items: BankBrand.values
                      .map(
                        (bank) => DropdownMenuItem(
                          value: bank,
                          child: Text(bankName(bank)),
                        ),
                      )
                      .toList(),
                  onChanged: (value) => setState(() => _bank = value!),
                ),
                const SizedBox(height: 12),
                SegmentedButton<CardOwnership>(
                  segments: const [
                    ButtonSegment(
                      value: CardOwnership.customer,
                      label: Text('کارت مخاطب'),
                      icon: Icon(Icons.person_outline_rounded),
                    ),
                    ButtonSegment(
                      value: CardOwnership.personal,
                      label: Text('کارت شخصی'),
                      icon: Icon(Icons.shield_outlined),
                    ),
                  ],
                  selected: {_ownership},
                  onSelectionChanged: (value) => setState(() {
                    _ownership = value.first;
                    if (_ownership == CardOwnership.customer) {
                      _cvv2.clear();
                      _expiry.clear();
                    }
                  }),
                  showSelectedIcon: false,
                ),
                const SizedBox(height: 12),
                numberField(
                  _cardNumber,
                  'شماره کارت',
                  16,
                  (value) => digits(value).length == 16
                      ? null
                      : 'شماره کارت باید ۱۶ رقم باشد',
                ),
                const SizedBox(height: 12),
                numberField(
                  _account,
                  'شماره حساب',
                  24,
                  (value) => digits(value).isNotEmpty
                      ? null
                      : 'شماره حساب را وارد کنید',
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: _iban,
                  textDirection: TextDirection.ltr,
                  textAlign: TextAlign.left,
                  textCapitalization: TextCapitalization.characters,
                  inputFormatters: [
                    FilteringTextInputFormatter.allow(RegExp('[a-zA-Z0-9]')),
                    LengthLimitingTextInputFormatter(26),
                  ],
                  decoration: decoration(
                    'شماره شبا',
                    Icons.tag_rounded,
                  ).copyWith(hintText: 'IR000000000000000000000000'),
                  validator: (value) =>
                      RegExp(
                        r'^IR\d{24}$',
                      ).hasMatch((value ?? '').toUpperCase())
                      ? null
                      : 'شماره شبا باید با IR و ۲۴ رقم وارد شود',
                ),
                if (_ownership == CardOwnership.personal) ...[
                  const SizedBox(height: 16),
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: const Color(0xFFEAF2FF),
                      borderRadius: BorderRadius.circular(14),
                    ),
                    child: const Row(
                      children: [
                        Icon(Icons.lock_rounded, color: Color(0xFF1769E0)),
                        SizedBox(width: 9),
                        Expanded(
                          child: Text(
                            'اطلاعات زیر فقط رمزنگاری‌شده ذخیره می‌شود و هرگز قابل ارسال نیست.',
                            textAlign: TextAlign.right,
                            style: TextStyle(fontSize: 12, height: 1.5),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Expanded(
                        child: numberField(
                          _cvv2,
                          'CVV2',
                          4,
                          (value) => editing || digits(value).length >= 3
                              ? null
                              : '۳ یا ۴ رقم',
                          obscure: true,
                        ),
                      ),
                      const SizedBox(width: 10),
                      Expanded(
                        child: numberField(
                          _expiry,
                          'تاریخ انقضا',
                          4,
                          (value) => editing || digits(value).length == 4
                              ? null
                              : '۴ رقم',
                          obscure: true,
                        ),
                      ),
                    ],
                  ),
                ],
                const SizedBox(height: 20),
                FilledButton.icon(
                  onPressed: _submit,
                  icon: const Icon(Icons.check_rounded),
                  label: Text(editing ? 'ذخیره تغییرات' : 'افزودن کارت'),
                  style: FilledButton.styleFrom(
                    minimumSize: const Size.fromHeight(52),
                    backgroundColor: const Color(0xFF0A3678),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(15),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget numberField(
    TextEditingController controller,
    String label,
    int maxLength,
    FormFieldValidator<String> validator, {
    bool obscure = false,
  }) => TextFormField(
    controller: controller,
    textDirection: TextDirection.ltr,
    textAlign: TextAlign.left,
    obscureText: obscure,
    keyboardType: TextInputType.number,
    inputFormatters: [
      FilteringTextInputFormatter.digitsOnly,
      LengthLimitingTextInputFormatter(maxLength),
    ],
    decoration: decoration(label, Icons.numbers_rounded),
    validator: validator,
  );

  InputDecoration decoration(String label, IconData icon) => InputDecoration(
    labelText: label,
    prefixIcon: Icon(icon),
    filled: true,
    fillColor: Colors.white,
    border: OutlineInputBorder(
      borderRadius: BorderRadius.circular(14),
      borderSide: const BorderSide(color: Color(0xFFDCE4EF)),
    ),
    enabledBorder: OutlineInputBorder(
      borderRadius: BorderRadius.circular(14),
      borderSide: const BorderSide(color: Color(0xFFDCE4EF)),
    ),
  );

  void _submit() {
    if (!_formKey.currentState!.validate()) return;
    final personal = _ownership == CardOwnership.personal;
    Navigator.of(context).pop(
      CardEditDraft(
        bank: _bank,
        cardNumber: digits(_cardNumber.text),
        accountNumber: digits(_account.text),
        iban: _iban.text.replaceAll(' ', '').toUpperCase(),
        ownership: _ownership,
        cvv2: personal && _cvv2.text.isNotEmpty ? _cvv2.text : null,
        expiry: personal && _expiry.text.isNotEmpty ? _expiry.text : null,
      ),
    );
    _cvv2.clear();
    _expiry.clear();
  }
}

String digits(String? input) => (input ?? '').replaceAll(RegExp(r'\D'), '');
String bankName(BankBrand bank) => switch (bank) {
  BankBrand.mellat => 'بانک ملت',
  BankBrand.melli => 'بانک ملی ایران',
  BankBrand.saderat => 'بانک صادرات ایران',
  BankBrand.tejarat => 'بانک تجارت',
};
