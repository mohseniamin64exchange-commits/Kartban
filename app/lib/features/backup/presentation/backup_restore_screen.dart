import 'package:flutter/material.dart';
import '../application/local_backup_workflow.dart';
import '../domain/backup_models.dart';
import '../../settings/presentation/settings_chrome.dart';

class BackupRestoreScreen extends StatefulWidget {
  const BackupRestoreScreen({super.key, required this.workflow});
  final LocalBackupWorkflow workflow;
  @override
  State<BackupRestoreScreen> createState() => _BackupRestoreScreenState();
}

class _BackupRestoreScreenState extends State<BackupRestoreScreen> {
  DatabaseHealthReport? _health;
  RestorePreview? _preview;
  bool _busy = false;

  @override
  Widget build(BuildContext context) => SettingsPageScaffold(
    title: 'پشتیبان‌گیری و بازیابی',
    subtitle:
        'سلامت داده بررسی می‌شود؛ سپس فایل محلی رمزنگاری‌شده ساخته یا پیش از بازیابی اعتبارسنجی می‌شود.',
    icon: Icons.health_and_safety_outlined,
    children: [
      SettingsNotice(
        text: _health == null
            ? 'برای شروع، سلامت پایگاه داده را بررسی کنید.'
            : _health!.safeMessages.join(' '),
        icon: _health?.level == HealthLevel.healthy
            ? Icons.verified_rounded
            : Icons.health_and_safety_outlined,
        color: _health?.level == HealthLevel.healthy ? success : electricBlue,
      ),
      const SizedBox(height: 20),
      SettingsSection(
        title: 'عملیات امن',
        children: [
          SettingsRow(
            title: 'آزمایش سلامت',
            subtitle: 'ساختار پایگاه داده بدون نمایش محتوا بررسی می‌شود.',
            icon: Icons.fact_check_outlined,
            onTap: _busy ? null : _check,
          ),
          SettingsRow(
            title: 'ساخت پشتیبان رمزنگاری‌شده',
            subtitle: 'فقط پس از تأیید سلامت و با رمز مستقل.',
            icon: Icons.enhanced_encryption_outlined,
            onTap: _busy ? null : _backup,
          ),
          SettingsRow(
            title: 'بررسی و بازیابی فایل',
            subtitle:
                'رمزگشایی، امضا و نسخه فایل پیش از تأیید نهایی بررسی می‌شود.',
            icon: Icons.settings_backup_restore_rounded,
            onTap: _busy ? null : _inspect,
          ),
        ],
      ),
      if (_preview != null) ...[
        SettingsNotice(
          text:
              'فایل سالم است: ${_preview!.metadata.summary.peopleCount} شخص و ${_preview!.metadata.summary.cardCount} کارت. اطلاعات حساس در خلاصه نمایش داده نمی‌شود.',
          icon: Icons.check_circle_outline_rounded,
          color: success,
        ),
        const SizedBox(height: 12),
        FilledButton.icon(
          onPressed: _busy ? null : _restore,
          icon: const Icon(Icons.restore_rounded),
          label: const Text('تأیید بازیابی'),
        ),
      ],
      if (_busy)
        const Padding(
          padding: EdgeInsets.all(20),
          child: Center(child: CircularProgressIndicator()),
        ),
    ],
  );

  Future<String?> _password(String title) async {
    final controller = TextEditingController();
    final value = await showDialog<String>(
      context: context,
      builder: (context) => Directionality(
        textDirection: TextDirection.rtl,
        child: AlertDialog(
          title: Text(title),
          content: TextField(
            controller: controller,
            obscureText: true,
            textAlign: TextAlign.right,
            decoration: const InputDecoration(
              labelText: 'رمز فایل (حداقل ۶ نویسه)',
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('انصراف'),
            ),
            FilledButton(
              onPressed: () => Navigator.pop(context, controller.text),
              child: const Text('ادامه'),
            ),
          ],
        ),
      ),
    );
    controller.clear();
    controller.dispose();
    return value;
  }

  Future<void> _run(Future<void> Function() operation) async {
    setState(() => _busy = true);
    try {
      await operation();
    } on BackupWorkflowException catch (error) {
      if (mounted) _message(error.safeMessage);
    } catch (_) {
      if (mounted) _message('عملیات انجام نشد. دوباره تلاش کنید.');
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  Future<void> _check() => _run(() async {
    final report = await widget.workflow.checkHealth();
    if (mounted) setState(() => _health = report);
  });
  Future<void> _backup() => _run(() async {
    final password = await _password('رمز پشتیبان');
    if (password == null) return;
    final result = await widget.workflow.createEncryptedBackup(password);
    if (result != null && mounted)
      _message('پشتیبان رمزنگاری‌شده با موفقیت ذخیره شد.');
  });
  Future<void> _inspect() => _run(() async {
    final password = await _password('رمز فایل پشتیبان');
    if (password == null) return;
    final result = await widget.workflow.inspectBackup(password);
    if (mounted) setState(() => _preview = result);
  });
  Future<void> _restore() => _run(() async {
    final preview = _preview;
    if (preview == null) return;
    await widget.workflow.confirmRestore(preview.restoreToken);
    if (mounted) {
      setState(() => _preview = null);
      _message('بازیابی با موفقیت و پس از آزمایش سلامت انجام شد.');
    }
  });
  void _message(String text) =>
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(text)));
}
