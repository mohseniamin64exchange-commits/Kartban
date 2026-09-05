# CardYar collaboration contract

CardYar is a Persian, offline-first Android application built with Flutter.

## Non-negotiable product rules

- All user-facing copy is Persian and layouts are true RTL: primary text stays right-aligned; supporting icons, bank marks, and chevrons stay on the left unless they are semantic leading controls.
- Customer cards never store CVV2 or expiry. Personal cards may store them only encrypted and reveal them temporarily after authentication.
- CVV2 and expiry must never appear in SMS, shared images, exports, logs, previews, notifications, or visible backup summaries.
- Card number, IBAN, and account number are unique. Duplicate names show a warning but are allowed after confirmation.
- The app is fully offline. Do not add INTERNET permission, analytics, cloud sync, ads, or remote fonts/assets.
- Use fake sample data only.
- Respect text scale Small/Medium/Large without clipping or horizontal overflow.

## Architecture and ownership

- lib/core: shared design tokens, utilities, routing, and app shell.
- lib/domain: immutable entities, validation, repository interfaces.
- lib/data: local persistence and repository implementations.
- lib/features: feature-owned presentation and controllers.
- lib/security: authentication, encryption, secure-screen policy.
- test: automated tests.

Agents must edit only their assigned paths, run formatting/tests relevant to their slice, and report every changed file.
