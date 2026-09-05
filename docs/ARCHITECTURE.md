# CardYar architecture

## لایه‌ها

- Presentation: Flutter Material 3 with a custom CardYar visual system and Riverpod state.
- Domain: plain Dart entities, validators, use-cases, and repository interfaces.
- Data: SQLite local database plus encrypted sensitive payloads.
- Security: app lock, biometric gate, Android Keystore bridge, secure-screen controller.
- Sharing: secure text formatter and local PNG renderer; platform share sheet only.

## ناوبری

- Home -> Person cards -> Add/Edit card.
- Drawer -> Security, Backup, Privacy, Display, About.
- Back closes sheets/drawer first, then the child page; it never discards dirty forms without confirmation.

## تطبیق‌پذیری

- Primary phone targets: 360x800, 393x852, 412x915 logical pixels.
- Use SafeArea and keyboard-aware scrolling.
- Large text may grow row height but may not clip actions or banking identifiers.
- Tablet behavior centers a constrained 600dp content column; no stretched cards.
