import 'package:flutter/material.dart';

const navy = Color(0xFF041B3D);
const ink = Color(0xFF111827);
const slate = Color(0xFF667085);
const canvas = Color(0xFFF4F7FB);
const electricBlue = Color(0xFF1769E0);
const success = Color(0xFF11845B);
const warning = Color(0xFFC67A12);
const danger = Color(0xFFC83B45);

class SettingsPageScaffold extends StatelessWidget {
  const SettingsPageScaffold({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.children,
    super.key,
  });

  final String title;
  final String subtitle;
  final IconData icon;
  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        backgroundColor: canvas,
        appBar: AppBar(
          title: Text(title, textAlign: TextAlign.right),
          centerTitle: false,
          elevation: 0,
          scrolledUnderElevation: 0,
          foregroundColor: Colors.white,
          backgroundColor: navy,
        ),
        body: SafeArea(
          top: false,
          child: Align(
            alignment: Alignment.topCenter,
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 600),
              child: ListView(
                padding: const EdgeInsets.fromLTRB(16, 18, 16, 28),
                children: [
                  _PageIntro(icon: icon, title: title, subtitle: subtitle),
                  const SizedBox(height: 20),
                  ...children,
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class SettingsSection extends StatelessWidget {
  const SettingsSection({
    required this.title,
    required this.children,
    this.description,
    super.key,
  });

  final String title;
  final String? description;
  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 22),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(
            title,
            textAlign: TextAlign.right,
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              color: ink,
              fontWeight: FontWeight.w800,
            ),
          ),
          if (description != null) ...[
            const SizedBox(height: 4),
            Text(
              description!,
              textAlign: TextAlign.right,
              style: Theme.of(
                context,
              ).textTheme.bodySmall?.copyWith(color: slate, height: 1.6),
            ),
          ],
          const SizedBox(height: 10),
          DecoratedBox(
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.surface,
              borderRadius: BorderRadius.circular(18),
              border: Border.all(color: navy.withValues(alpha: 0.08)),
              boxShadow: [
                BoxShadow(
                  color: navy.withValues(alpha: 0.06),
                  blurRadius: 18,
                  offset: const Offset(0, 6),
                ),
              ],
            ),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(18),
              child: Column(children: _withDividers(children)),
            ),
          ),
        ],
      ),
    );
  }

  List<Widget> _withDividers(List<Widget> items) => [
    for (var index = 0; index < items.length; index++) ...[
      items[index],
      if (index != items.length - 1)
        const Divider(height: 1, indent: 16, endIndent: 16),
    ],
  ];
}

class SettingsRow extends StatelessWidget {
  const SettingsRow({
    required this.title,
    required this.icon,
    this.subtitle,
    this.trailing,
    this.onTap,
    this.enabled = true,
    super.key,
  });

  final String title;
  final String? subtitle;
  final IconData icon;
  final Widget? trailing;
  final VoidCallback? onTap;
  final bool enabled;

  @override
  Widget build(BuildContext context) {
    final foreground = enabled ? ink : slate.withValues(alpha: 0.6);
    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: enabled ? onTap : null,
        child: ConstrainedBox(
          constraints: const BoxConstraints(minHeight: 72),
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        title,
                        textAlign: TextAlign.right,
                        style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                          color: foreground,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                      if (subtitle != null) ...[
                        const SizedBox(height: 3),
                        Text(
                          subtitle!,
                          textAlign: TextAlign.right,
                          style: Theme.of(context).textTheme.bodySmall
                              ?.copyWith(color: slate, height: 1.45),
                        ),
                      ],
                    ],
                  ),
                ),
                const SizedBox(width: 12),
                if (trailing != null) ...[trailing!, const SizedBox(width: 10)],
                Container(
                  width: 42,
                  height: 42,
                  decoration: BoxDecoration(
                    color: electricBlue.withValues(alpha: enabled ? 0.1 : 0.05),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Icon(
                    icon,
                    color: enabled ? electricBlue : slate,
                    size: 22,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class SettingsNotice extends StatelessWidget {
  const SettingsNotice({
    required this.text,
    this.icon = Icons.info_outline_rounded,
    this.color = electricBlue,
    super.key,
  });

  final String text;
  final IconData icon;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.08),
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: color.withValues(alpha: 0.18)),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Expanded(
            child: Text(
              text,
              textAlign: TextAlign.right,
              style: Theme.of(
                context,
              ).textTheme.bodySmall?.copyWith(color: ink, height: 1.6),
            ),
          ),
          const SizedBox(width: 10),
          Icon(icon, color: color, size: 21),
        ],
      ),
    );
  }
}

class _PageIntro extends StatelessWidget {
  const _PageIntro({
    required this.icon,
    required this.title,
    required this.subtitle,
  });

  final IconData icon;
  final String title;
  final String subtitle;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                textAlign: TextAlign.right,
                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: navy,
                  fontWeight: FontWeight.w900,
                ),
              ),
              const SizedBox(height: 5),
              Text(
                subtitle,
                textAlign: TextAlign.right,
                style: Theme.of(
                  context,
                ).textTheme.bodyMedium?.copyWith(color: slate, height: 1.6),
              ),
            ],
          ),
        ),
        const SizedBox(width: 14),
        Container(
          width: 56,
          height: 56,
          decoration: BoxDecoration(
            color: navy,
            borderRadius: BorderRadius.circular(18),
          ),
          child: Icon(icon, color: Colors.white, size: 28),
        ),
      ],
    );
  }
}
