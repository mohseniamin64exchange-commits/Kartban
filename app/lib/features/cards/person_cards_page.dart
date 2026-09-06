import 'dart:io';
import 'dart:ui' as ui;

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:path_provider/path_provider.dart';
import 'package:share_plus/share_plus.dart';
import 'package:url_launcher/url_launcher.dart';

import 'models/card_view_data.dart';
import 'widgets/bank_card_visual.dart';
import 'widgets/card_editor_sheet.dart';
import '../sharing/card_share_service.dart';
import '../sharing/share_method_sheet.dart';

typedef SensitiveAuthenticator = Future<bool> Function(CardViewData card);

class PersonCardsPage extends StatefulWidget {
  const PersonCardsPage({
    super.key,
    required this.personName,
    required this.cards,
    this.onAuthenticateSensitive,
  });

  final String personName;
  final List<CardViewData> cards;
  final SensitiveAuthenticator? onAuthenticateSensitive;

  @override
  State<PersonCardsPage> createState() => _PersonCardsPageState();
}

class _PersonCardsPageState extends State<PersonCardsPage> {
  late final List<CardViewData> _cards = [...widget.cards];
  final Set<String> _selected = {};
  final Map<String, GlobalKey> _captureKeys = {};
  final CardShareService _shareFormatter = const CardShareService();
  bool _sharing = false;

  GlobalKey _keyFor(String id) => _captureKeys.putIfAbsent(id, GlobalKey.new);

  @override
  Widget build(BuildContext context) => Directionality(
    textDirection: TextDirection.rtl,
    child: Scaffold(
      backgroundColor: const Color(0xFFF4F7FB),
      appBar: AppBar(
        title: const Text('کارت‌های شخص'),
        centerTitle: true,
        backgroundColor: const Color(0xFFF4F7FB),
        surfaceTintColor: Colors.transparent,
      ),
      body: SafeArea(
        bottom: false,
        child: Column(
          children: [
            _PersonHeader(name: widget.personName, count: _cards.length),
            Expanded(
              child: _cards.isEmpty
                  ? const _NoCards()
                  : SingleChildScrollView(
                      padding: const EdgeInsets.fromLTRB(14, 8, 14, 106),
                      child: Column(
                        children: [
                          for (final card in _cards) ...[
                            SizedBox(
                              height: 128,
                              child: RepaintBoundary(
                                key: _keyFor(card.id),
                                child: BankCardVisual(
                                  card: card,
                                  compact: true,
                                  selected: _selected.contains(card.id),
                                  onTap: () => setState(() {
                                    if (!_selected.add(card.id))
                                      _selected.remove(card.id);
                                  }),
                                  onDoubleTap: () => _revealSensitive(card),
                                  onEdit: () => _editCard(card),
                                ),
                              ),
                            ),
                            const SizedBox(height: 10),
                          ],
                        ],
                      ),
                    ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: SafeArea(
        top: false,
        child: Container(
          padding: const EdgeInsets.fromLTRB(14, 8, 14, 12),
          decoration: const BoxDecoration(
            color: Color(0xFFF4F7FB),
            border: Border(top: BorderSide(color: Color(0xFFE3E8F0))),
          ),
          child: Row(
            children: [
              SizedBox(
                width: 56,
                height: 56,
                child: IconButton.filledTonal(
                  tooltip: 'افزودن کارت',
                  onPressed: _addCard,
                  icon: const Icon(Icons.add_rounded, size: 30),
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: SizedBox(
                  height: 56,
                  child: FilledButton.icon(
                    onPressed: _selected.isEmpty || _sharing
                        ? null
                        : _shareSelected,
                    icon: _sharing
                        ? const SizedBox.square(
                            dimension: 20,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              color: Colors.white,
                            ),
                          )
                        : const Icon(Icons.send_rounded),
                    label: Text(
                      _selected.isEmpty
                          ? 'ارسال کارت'
                          : 'ارسال ${_selected.length} کارت',
                      style: const TextStyle(fontWeight: FontWeight.w800),
                    ),
                    style: FilledButton.styleFrom(
                      backgroundColor: const Color(0xFF06275A),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(17),
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    ),
  );

  Future<void> _addCard() async {
    final draft = await CardEditorSheet.show(
      context,
      holderName: widget.personName,
    );
    if (draft == null || !mounted) return;
    setState(() => _cards.add(_fromDraft(draft)));
  }

  Future<void> _editCard(CardViewData card) async {
    final draft = await CardEditorSheet.show(
      context,
      holderName: widget.personName,
      initialCard: card,
    );
    if (draft == null || !mounted) return;
    final index = _cards.indexWhere((item) => item.id == card.id);
    if (index < 0) return;
    setState(() => _cards[index] = _fromDraft(draft, id: card.id));
  }

  CardViewData _fromDraft(CardEditDraft draft, {String? id}) => CardViewData(
    id: id ?? 'card-${DateTime.now().microsecondsSinceEpoch}',
    bank: draft.bank,
    holderName: widget.personName,
    cardNumber: draft.cardNumber,
    accountNumber: draft.accountNumber,
    iban: draft.iban,
    ownership: draft.ownership,
    hasEncryptedSensitiveData:
        draft.ownership == CardOwnership.personal &&
        (draft.cvv2?.isNotEmpty ?? false) &&
        (draft.expiry?.isNotEmpty ?? false),
  );

  Future<void> _revealSensitive(CardViewData card) async {
    if (card.ownership != CardOwnership.personal) return;
    final authenticated =
        await (widget.onAuthenticateSensitive?.call(card) ?? _demoAuthDialog());
    if (!authenticated || !mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('نمایش امن موقت فعال شد؛ ثبت تصویر صفحه مسدود است.'),
        duration: Duration(seconds: 3),
      ),
    );
  }

  Future<bool> _demoAuthDialog() async =>
      await showDialog<bool>(
        context: context,
        builder: (context) => AlertDialog(
          title: const Text('احراز هویت'),
          content: const Text(
            'برای نمایش CVV2 و تاریخ انقضا، رمز یا اثر انگشت لازم است.',
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text('انصراف'),
            ),
            FilledButton(
              onPressed: () => Navigator.pop(context, true),
              child: const Text('تأیید آزمایشی'),
            ),
          ],
        ),
      ) ??
      false;

  Future<void> _shareSelected() async {
    final method = await ShareMethodSheet.show(
      context,
      selectionCount: _selected.length,
    );
    if (method == null || !mounted) return;
    setState(() => _sharing = true);
    try {
      final chosen = _cards
          .where((card) => _selected.contains(card.id))
          .toList();
      if (method == CardShareMethod.sms) {
        final text = chosen
            .map(
              (card) => _shareFormatter.smsText(
                bank: card.bankName,
                holder: card.holderName,
                cardNumber: card.cardNumber,
                account: card.accountNumber,
                iban: card.iban,
              ),
            )
            .join('\n\n');
        final uri = Uri(scheme: 'sms', queryParameters: {'body': text});
        if (!await launchUrl(uri, mode: LaunchMode.externalApplication)) {
          await SharePlus.instance.share(
            ShareParams(text: text, subject: 'مشخصات کارت بانکی'),
          );
        }
      } else {
        final directory = await getTemporaryDirectory();
        final files = <XFile>[];
        for (final card in chosen) {
          final boundary =
              _keyFor(card.id).currentContext?.findRenderObject()
                  as RenderRepaintBoundary?;
          if (boundary == null) continue;
          final image = await boundary.toImage(pixelRatio: 3);
          final data = await image.toByteData(format: ui.ImageByteFormat.png);
          if (data == null) continue;
          final file = File('${directory.path}/kartyar_${card.id}.png');
          await file.writeAsBytes(data.buffer.asUint8List(), flush: true);
          files.add(XFile(file.path, mimeType: 'image/png'));
        }
        if (files.isEmpty) throw StateError('تصویر کارت آماده نشد.');
        await SharePlus.instance.share(
          ShareParams(
            files: files,
            text: 'مشخصات کارت بانکی — ساخته‌شده با کارت‌یار',
            subject: 'کارت بانکی',
          ),
        );
      }
    } catch (_) {
      if (mounted)
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('ارسال انجام نشد؛ دوباره تلاش کنید.')),
        );
    } finally {
      if (mounted) setState(() => _sharing = false);
    }
  }
}

class _PersonHeader extends StatelessWidget {
  const _PersonHeader({required this.name, required this.count});
  final String name;
  final int count;
  @override
  Widget build(BuildContext context) => Padding(
    padding: const EdgeInsets.fromLTRB(18, 4, 18, 10),
    child: Row(
      textDirection: TextDirection.rtl,
      children: [
        Container(
          width: 58,
          height: 58,
          decoration: const BoxDecoration(
            shape: BoxShape.circle,
            gradient: LinearGradient(
              colors: [Color(0xFFEAF2FF), Color(0xFFCFE1FF)],
            ),
          ),
          child: const Icon(
            Icons.person_rounded,
            color: Color(0xFF315EAA),
            size: 40,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(
                name,
                textAlign: TextAlign.right,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(
                  color: Color(0xFF10254A),
                  fontSize: 22,
                  fontWeight: FontWeight.w900,
                ),
              ),
              Text(
                '$count کارت بانکی',
                textAlign: TextAlign.right,
                style: const TextStyle(color: Color(0xFF7A8698), fontSize: 13),
              ),
            ],
          ),
        ),
      ],
    ),
  );
}

class _NoCards extends StatelessWidget {
  const _NoCards();
  @override
  Widget build(BuildContext context) => const Center(
    child: Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(Icons.credit_card_off_rounded, size: 54, color: Color(0xFF8B98AA)),
        SizedBox(height: 10),
        Text(
          'هنوز کارتی ثبت نشده',
          style: TextStyle(fontSize: 17, fontWeight: FontWeight.w800),
        ),
      ],
    ),
  );
}
