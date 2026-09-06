# اجرای کارت‌یار در Google AI Studio

این مخزن دو بخش دارد:

- `app/`: برنامه اصلی Android با Flutter؛ مرجع نهایی منطق، امنیت و APK.
- `index.html` و `person-cards.html`: همزاد طراحی وب برای پیش‌نمایش و ویرایش سریع در Google AI Studio.

## ورود به AI Studio

1. وارد **Google AI Studio → Build** شوید.
2. از علامت **+** کنار کادر پیام، **Import from GitHub** را انتخاب کنید.
3. مخزن `mohseniamin64exchange-commits/kartyar` را انتخاب کنید.
4. پلتفرم را روی **Web app** نگه دارید.
5. پس از باز شدن پیش‌نمایش، متن `AI_STUDIO_PROMPT.md` را به عامل بدهید.
6. تغییرات را از **Settings → GitHub** روی همین مخزن Push کنید.
7. اینجا تغییرات را Pull می‌کنیم و طراحی تأییدشده را به Flutter منتقل می‌کنیم.

> Google AI Studio پروژه Flutter را مستقیماً اجرا نمی‌کند. نسخه وب فقط محیط طراحی است و نباید قواعد امنیتی برنامه Android را تغییر دهد.

## اجرای محلی نسخه طراحی

```
npm install
npm run dev
```

برای کنترل خروجی:

```
npm run build
```
