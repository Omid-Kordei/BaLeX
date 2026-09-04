BaLeX

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="96" height="96" alt="BaLeX Icon" />
</p><p align="center">
  یک کلاینت اندرویدی برای مدیریت و تعامل با ربات‌های پیام‌رسان بله
</p><p align="center">
  <a href="https://bale.ai">Bale</a>
</p><p align="center">
  <img alt="Platform" src="https://img.shields.io/badge/platform-Android-3ddc84?logo=android&logoColor=white">
  <img alt="Min SDK" src="https://img.shields.io/badge/minSdk-21-blue">
  <img alt="Target SDK" src="https://img.shields.io/badge/targetSdk-34-blue">
  <img alt="License" src="https://img.shields.io/badge/license-MIT-green">
</p>---

معرفی

BaLeX یک اپلیکیشن اندرویدی برای مدیریت و تعامل مستقیم با ربات‌های پیام‌رسان بله است.

این برنامه با هدف فراهم کردن یک محیط اختصاصی برای مدیریت ربات، مشاهده و مدیریت گفتگوها، ارسال و دریافت پیام، ارسال فایل و موقعیت مکانی و مشاهده اطلاعات و لاگ‌های مربوط به عملکرد برنامه توسعه داده شده است.

BaLeX برای انجام عملیات اصلی خود به سرور واسط اختصاصی نیاز ندارد و ارتباطات مربوط به ربات مستقیماً از طریق Bot API بله انجام می‌شود.

این پروژه به‌صورت مستقل توسعه داده شده و هیچ ارتباط رسمی با شرکت یا تیم توسعه‌دهنده پیام‌رسان بله ندارد.

---

قابلیت‌ها

- ورود و مدیریت حساب ربات با استفاده از Bot Token
- اعتبارسنجی توکن و دریافت اطلاعات ربات
- نمایش گفتگوهای ربات
- تفکیک گفتگوها بر اساس نوع
- دریافت پیام‌های جدید
- ارسال پیام
- پاسخ به پیام‌ها
- ارسال تصاویر و فایل‌ها
- ارسال موقعیت مکانی
- ذخیره‌سازی محلی اطلاعات مورد نیاز برنامه
- نمایش اطلاعات پروفایل ربات
- سیستم ثبت و نمایش Log
- مدیریت وضعیت حساب
- خروج از حساب و پاک‌سازی اطلاعات ذخیره‌شده
- رابط کاربری سازگار با زبان فارسی و راست‌به‌چپ
- پشتیبانی از Android

---

معماری پروژه

BaLeX با Java و Android SDK توسعه داده شده و ساختار پروژه بر پایه Activityها و کلاس‌های کمکی اختصاصی شکل گرفته است.

BaLeX/
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── ir/
│           │       └── BaleX/
│           │           ├── MainActivity.java
│           │           ├── HomeActivity.java
│           │           ├── BaleApi.java
│           │           ├── PollingManager.java
│           │           ├── StorageHelper.java
│           │           ├── FileDownloader.java
│           │           ├── FileUtil.java
│           │           ├── ChatAdapter.java
│           │           ├── MessageAdapter.java
│           │           ├── LogHelper.java
│           │           ├── ProfileCache.java
│           │           ├── SketchwareUtil.java
│           │           ├── ReplyState.java
│           │           ├── PendingUpload.java
│           │           ├── IncomingMessage.java
│           │           ├── ChatOpener.java
│           │           └── ...
│           └── res/
│               ├── drawable/
│               ├── layout/
│               ├── mipmap-*/
│               ├── values/
│               └── ...
├── build.gradle
├── settings.gradle
└── gradle.properties

اجزای اصلی

کلاس| وظیفه
"MainActivity"| صفحه ورود و دریافت Bot Token
"HomeActivity"| رابط اصلی برنامه و مدیریت گفتگوها
"BaleApi"| ارتباط با Bot API بله
"PollingManager"| دریافت به‌روزرسانی‌ها و پیام‌های جدید
"StorageHelper"| مدیریت داده‌های ذخیره‌شده محلی
"FileDownloader"| مدیریت دریافت فایل‌ها
"FileUtil"| ابزارهای مدیریت فایل
"ChatAdapter"| نمایش لیست گفتگوها
"MessageAdapter"| نمایش پیام‌های داخل گفتگو
"LogHelper"| ثبت و مدیریت Logها
"ProfileCache"| نگهداری موقت اطلاعات پروفایل
"SketchwareUtil"| توابع کمکی مورد استفاده رابط کاربری

---

جریان عملکرد

فرآیند اصلی برنامه به شکل زیر انجام می‌شود:

1. کاربر Bot Token را در "MainActivity" وارد می‌کند.
2. برنامه با استفاده از API مربوطه، اعتبار توکن را بررسی می‌کند.
3. در صورت معتبر بودن توکن، اطلاعات مورد نیاز برنامه ذخیره می‌شود.
4. کاربر وارد "HomeActivity" می‌شود.
5. "PollingManager" به‌صورت دوره‌ای یا با استفاده از Long Polling، به‌روزرسانی‌های جدید را دریافت می‌کند.
6. پیام‌ها و اطلاعات مورد نیاز چت‌ها در حافظه محلی برنامه مدیریت می‌شوند.
7. عملیات ارسال پیام، فایل و موقعیت مکانی از طریق "BaleApi" انجام می‌شود.

---

فناوری‌های استفاده‌شده

بخش| فناوری
زبان برنامه‌نویسی| Java
پلتفرم| Android
Minimum SDK| 21
Target SDK| 34
ارتباط شبکه| "HttpURLConnection"
API| Bale Bot API
دریافت پیام| Long Polling
ذخیره‌سازی| "SharedPreferences" و فایل‌های محلی
رابط کاربری| Android Views
طراحی| Android XML
کتابخانه‌های اصلی| AndroidX AppCompat و Material Components

BaLeX برای ارتباط شبکه از "HttpURLConnection" استفاده می‌کند و برای لایه ارتباطی خود به Retrofit یا OkHttp وابسته نیست.

---

امنیت

امنیت اطلاعات ربات یکی از بخش‌های مهم استفاده از BaLeX است.

ذخیره‌سازی Bot Token

در نسخه فعلی، Bot Token در "SharedPreferences" ذخیره می‌شود و به‌صورت رمزنگاری‌شده نگهداری نمی‌شود.

بنابراین در دستگاه‌های Root شده یا شرایطی که دسترسی غیرمجاز به داده‌های برنامه وجود داشته باشد، امکان استخراج آن وجود دارد.

بهبود این بخش یکی از موارد قابل بررسی برای نسخه‌های آینده است.

ارتباط HTTPS

ارتباطات اصلی برنامه با API بله از طریق HTTPS انجام می‌شوند.

با این حال، نسخه فعلی از Certificate Pinning استفاده نمی‌کند و امنیت اتصال بر سازوکارهای استاندارد TLS و Android متکی است.

اطلاعات محلی

برخی اطلاعات برنامه، از جمله داده‌های مربوط به گفتگوها و پیام‌ها، ممکن است در حافظه داخلی برنامه ذخیره شوند.

حافظه داخلی Android به‌صورت پیش‌فرض برای سایر برنامه‌ها قابل دسترسی نیست، اما در دستگاه‌های Root شده یا شرایط خاص دسترسی به داده‌ها ممکن است امکان‌پذیر باشد.

Bot Token

Bot Token را مانند یک اطلاعات محرمانه در نظر بگیرید.

هر شخصی که به توکن ربات دسترسی داشته باشد، بسته به سطح دسترسی API، می‌تواند از طرف ربات عملیات مختلفی انجام دهد.

Bot Token را در موارد زیر منتشر نکنید:

- GitHub
- Screenshots
- Issues
- Pull Requests
- فایل‌های عمومی پروژه
- کانال‌ها و گروه‌های عمومی

در صورت افشای Token، آن را در سریع‌ترین زمان ممکن از طریق سازوکارهای مدیریت ربات در بله تغییر یا غیرفعال کنید.

---

دسترسی‌های برنامه

BaLeX تنها برای قابلیت‌های مورد نیاز خود از دسترسی‌های Android استفاده می‌کند.

دسترسی موقعیت مکانی برای قابلیت ارسال Location استفاده می‌شود و در صورت درخواست این قابلیت توسط کاربر مورد نیاز است.

---

نصب

فایل APK نسخه‌های منتشرشده در بخش Releases مخزن GitHub قرار می‌گیرد.

برای نصب نسخه منتشرشده:

1. وارد بخش Releases شوید.
2. نسخه مورد نظر را انتخاب کنید.
3. فایل APK را دریافت کنید.
4. APK را روی دستگاه Android نصب کنید.

در صورت نصب نسخه‌ای خارج از Google Play، ممکن است Android نیاز به فعال بودن اجازه نصب برنامه از منبع مورد نظر داشته باشد.

---

Build از Source

پیش‌نیازها

- Android Studio
- JDK سازگار با نسخه Gradle پروژه
- Android SDK
- Android SDK Platform 34

Clone کردن پروژه

git clone https://github.com/<username>/BaLeX.git
cd BaLeX

سپس پروژه را در Android Studio باز کرده و اجازه دهید Gradle Sync تکمیل شود.

پس از تکمیل Sync، پروژه را روی یک دستگاه Android یا Emulator اجرا کنید.

Build کردن APK

برای ساخت نسخه Debug:

./gradlew assembleDebug

در Windows:

gradlew.bat assembleDebug

فایل خروجی معمولاً در مسیر زیر قرار می‌گیرد:

app/build/outputs/apk/

---

دریافت Bot Token

برای استفاده از BaLeX به یک Bot Token معتبر از سرویس Bot بله نیاز دارید.

Token را فقط از منبع رسمی دریافت کرده و آن را محرمانه نگه دارید.

مستندات رسمی Bot API بله:

https://dev.bale.ai

---

توسعه

BaLeX به‌گونه‌ای طراحی شده است که قابلیت توسعه و اضافه شدن امکانات جدید را داشته باشد.

برخی از زمینه‌های قابل توسعه در نسخه‌های آینده:

- پشتیبانی بهتر از چند ربات
- مدیریت هم‌زمان چند حساب
- سیستم مدیریت پیشرفته‌تر گفتگوها
- بهبود سیستم Log
- Backup و Restore
- بهبود مدیریت فایل‌ها
- بهینه‌سازی عملکرد Polling
- بهبود امنیت Token
- بهبود رابط کاربری
- مدیریت بهتر وضعیت اتصال
- قابلیت‌های مدیریتی بیشتر
- بهینه‌سازی مصرف منابع

Roadmap پروژه ممکن است در طول توسعه تغییر کند.

---

مشارکت در پروژه

از مشارکت در توسعه BaLeX استقبال می‌شود.

برای مشارکت:

1. Repository را Fork کنید.
2. یک Branch جدید ایجاد کنید.

git checkout -b feature/my-feature

3. تغییرات خود را اعمال کنید.
4. تغییرات را Commit کنید.

git commit -m "Add my feature"

5. Branch را Push کنید.

git push origin feature/my-feature

6. یک Pull Request ایجاد کنید.

برای تغییرات بزرگ یا قابلیت‌های جدید، پیشنهاد می‌شود ابتدا یک Issue ایجاد شود تا درباره طراحی و نحوه پیاده‌سازی آن گفتگو شود.

---

گزارش Bug

در صورت مشاهده مشکل، یک Issue ایجاد کنید و اطلاعات زیر را در اختیار توسعه‌دهندگان قرار دهید:

- نسخه BaLeX
- نسخه Android
- مدل دستگاه
- توضیح دقیق مشکل
- مراحل بازتولید
- Log مربوطه در صورت وجود
- Screenshot در صورت نیاز

از قرار دادن Bot Token، اطلاعات حساب یا سایر اطلاعات محرمانه در گزارش Bug خودداری کنید.

---

نسخه‌بندی

BaLeX از Semantic Versioning استفاده می‌کند.

ساختار نسخه‌ها:

MAJOR.MINOR.PATCH

برای مثال:

v1.0.0

تغییرات اساسی در بخش Major، قابلیت‌های جدید سازگار در Minor و اصلاحات و Bug Fixها در Patch منتشر می‌شوند.

---

License

این پروژه تحت مجوز MIT License منتشر شده است.

برای جزئیات کامل، فایل "LICENSE" موجود در Repository را مطالعه کنید.

---

Disclaimer

BaLeX یک پروژه مستقل و غیررسمی است و هیچ وابستگی یا ارتباط رسمی با شرکت یا تیم توسعه‌دهنده پیام‌رسان بله ندارد.

این پروژه از API ارائه‌شده توسط بله برای برقراری ارتباط با ربات‌ها استفاده می‌کند.

مسئولیت نگهداری و محافظت از Bot Token و اطلاعات حساب بر عهده کاربر است.

استفاده از BaLeX باید مطابق قوانین، مقررات و شرایط استفاده از سرویس بله و API مربوطه انجام شود.

توسعه‌دهندگان BaLeX مسئولیتی در قبال سوءاستفاده از Bot Token، از دست رفتن اطلاعات یا استفاده نادرست از نرم‌افزار ندارند.

---

Project Information

Project: BaLeX
Platform: Android
Language: Java
License: MIT
Current Version: 1.0.0
Status: Active Development

---

<p align="center">
  BaLeX
</p><p align="center">
  An independent Android client for managing Bale bot accounts.
</p>
