# متجر التطبيقات الحرة (F-Droid Store)

تطبيق أندرويد يتصل **مباشرة بواجهات F-Droid الرسمية والموثقة** (وليس أي إعادة هندسة
أو محاكاة لمتجر آخر):

- بحث: `https://search.f-droid.org/api/search_apps`
- تفاصيل/إصدارات الحزمة: `https://f-droid.org/api/v1/packages/{packageName}`
- تحميل ملف APK: `https://f-droid.org/repo/{packageName}_{versionCode}.apk`

كل التطبيقات في مستودع F-Droid مرخّصة برخص مفتوحة المصدر تسمح بإعادة التوزيع، لذلك
لا توجد مشكلة قانونية في عرضها وتنزيلها عبر هذا التطبيق — على عكس محاولة نسخ متاجر
تعتمد على الوصول غير الرسمي لمتجر Google Play.

## البنية

```
app/src/main/java/com/hisham/fdroidstore/
├── model/        نماذج البيانات (نتائج البحث، تفاصيل الحزمة)
├── network/      Retrofit + OkHttp لاستدعاء واجهات F-Droid
├── repository/   طبقة وسيطة بين الشبكة والواجهة
├── download/      تحميل APK إلى الكاش + فتح شاشة تثبيت أندرويد القياسية
├── ui/           شاشات Jetpack Compose (بحث، قائمة، تفاصيل/تحميل)
└── MainActivity.kt
```

## البناء عبر GitHub Actions (بدون كمبيوتر)

نفس الأسلوب المستخدم في مشاريعك الأخرى:

1. ارفع المشروع إلى GitHub عبر Termux:
   ```bash
   cd fdroid-store
   git init
   git remote add origin <رابط المستودع الخاص بك>
   git add .
   git commit -m "مشروع متجر F-Droid الأولي"
   git branch -M main
   git push -u origin main
   ```
2. سير عمل `.github/workflows/build-apk.yml` سيبني ملف APK تلقائيًا عند كل push
   إلى `main`، ويمكن تنزيله من تبويب **Actions → Artifacts**.

## ملاحظات

- التطبيق يطلب صلاحية `REQUEST_INSTALL_PACKAGES` لتتمكن من تثبيت الملفات المحمّلة.
- عند أول تثبيت، سيطلب أندرويد منك تفعيل "السماح من هذا المصدر" لتطبيق المتجر نفسه.
- شاشة البحث حاليًا هي نقطة الدخول الرئيسية (لا يوجد استعراض تصنيفات بعد) — يمكن
  إضافتها لاحقًا بجلب `index-v2.json` وتخزينه محليًا وفهرسته حسب التصنيف.
