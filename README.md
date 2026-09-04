<h1 align="center">BaLeX</h1><p align="center">
  <strong>Android Client for Bale Bot Management</strong>
</p><p align="center">
  A lightweight Android client for managing and interacting with Bale bot accounts.
</p><p align="center">
  <a href="https://dev.bale.ai">
    <img src="https://img.shields.io/badge/Bale-Bot%20API-6574AC?style=for-the-badge" alt="Bale Bot API">
  </a>
  <a href="https://www.android.com/">
    <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  </a>
  <img src="https://img.shields.io/badge/Java-Android%20SDK-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License">
</p><p align="center">
  <a href="#معرفی">معرفی</a> •
  <a href="#قابلیت‌ها">قابلیت‌ها</a> •
  <a href="#معماری">معماری</a> •
  <a href="#نصب">نصب</a> •
  <a href="#build-از-source">Build</a> •
  <a href="#امنیت">امنیت</a> •
  <a href="#roadmap">Roadmap</a>
</p>---

معرفی

BaLeX یک کلاینت مستقل اندرویدی برای مدیریت و تعامل با ربات‌های پیام‌رسان بله (Bale) است.

این پروژه با هدف فراهم کردن یک محیط اختصاصی برای مدیریت ربات از طریق دستگاه Android توسعه داده شده است.

BaLeX امکاناتی مانند:

- مشاهده گفتگوهای ربات
- ارسال و دریافت پیام
- Reply به پیام‌ها
- ارسال تصویر و فایل
- دریافت فایل‌های رسانه‌ای
- ارسال موقعیت مکانی
- مشاهده اطلاعات ربات
- ثبت و بررسی رویدادها و خطاها

را فراهم می‌کند.

BaLeX برای ارتباط با Bot API به سرور واسط اختصاصی نیاز ندارد و درخواست‌ها مستقیماً از دستگاه کاربر به API ارسال می‌شوند.

«توجه: BaLeX یک پروژه مستقل و غیررسمی است و هیچ ارتباط، همکاری یا وابستگی رسمی با شرکت یا تیم توسعه‌دهنده پیام‌رسان بله ندارد.»

---

وضعیت پروژه

مورد| وضعیت
نسخه| "1.0.0"
وضعیت توسعه| Active Development
پلتفرم| Android
زبان| Java
Minimum SDK| 21
Target SDK| 34
API| Bale Bot API
Network Layer| "HttpURLConnection"
دریافت Update| Long Polling
رابط کاربری| Android Views / XML
License| MIT

«وضعیت قابلیت‌ها ممکن است در طول توسعه پروژه تغییر کند.»

---

قابلیت‌ها

مدیریت ربات

- ورود با Bot Token
- اعتبارسنجی Bot Token
- دریافت اطلاعات ربات با "getMe"
- نمایش نام، Username و شناسه ربات
- ذخیره اطلاعات مورد نیاز برنامه
- خروج از حساب
- پاک‌سازی اطلاعات ذخیره‌شده

مدیریت گفتگو

- نمایش گفتگوهای ربات
- پشتیبانی از گفتگوهای خصوصی و گروهی
- فیلتر گفتگوها
- باز کردن گفتگوی اختصاصی
- نمایش پیام‌های دریافتی و ارسال‌شده
- Reply به پیام‌ها

پیام‌رسانی

- ارسال پیام متنی
- دریافت Updateهای جدید
- دریافت تقریباً بلادرنگ پیام‌ها با Long Polling
- Reply به پیام‌ها
- مدیریت وضعیت پیام‌ها

فایل و رسانه

- ارسال تصویر
- ارسال فایل
- انتخاب فایل از دستگاه
- دانلود فایل‌های رسانه‌ای
- مدیریت فایل‌های دریافت‌شده

موقعیت مکانی

- دریافت موقعیت مکانی دستگاه
- ارسال Location به گفتگو
- استفاده از GPS در صورت نیاز

سیستم Logging

- ثبت خطاهای برنامه
- ثبت خطاهای شبکه
- ثبت اطلاعات مرتبط با API
- کمک به بررسی و رفع مشکلات

«نکته امنیتی: Logها نباید شامل Bot Token یا سایر اطلاعات حساس باشند.»

---

معماری

BaLeX با Java و Android SDK توسعه داده شده و ساختار آن بر پایه Activityها، API Layer و کلاس‌های کمکی اختصاصی شکل گرفته است.

ساختار واقعی پروژه در Repository:

BaLeX/
│
├── src/
│   └── 1.0Source/
│       │
│       ├── app/
│       │   ├── build.gradle
│       │   ├── proguard-rules.pro
│       │   │
│       │   └── src/
│       │       └── main/
│       │           ├── AndroidManifest.xml
│       │           │
│       │           ├── java/
│       │           │   └── ir/
│       │           │       └── BaleX/
│       │           │           ├── MainActivity.java
│       │           │           ├── HomeActivity.java
│       │           │           ├── BaleApi.java
│       │           │           ├── PollingManager.java
│       │           │           ├── StorageHelper.java
│       │           │           ├── FileDownloader.java
│       │           │           ├── FileUtil.java
│       │           │           ├── ChatAdapter.java
│       │           │           ├── MessageAdapter.java
│       │           │           ├── LogHelper.java
│       │           │           ├── ProfileCache.java
│       │           │           ├── SketchwareUtil.java
│       │           │           ├── ReplyState.java
│       │           │           ├── PendingUpload.java
│       │           │           ├── IncomingMessage.java
│       │           │           ├── ChatOpener.java
│       │           │           └── ...
│       │           │
│       │           └── res/
│       │               ├── drawable/
│       │               ├── layout/
│       │               ├── mipmap-*/
│       │               ├── values/
│       │               └── ...
│       │
│       ├── build.gradle
│       ├── settings.gradle
│       └── gradle.properties
│
├── .gitignore
├── README.md
└── LICENSE

اجزای اصلی

کلاس| مسئولیت
"MainActivity"| صفحه ورود و دریافت Bot Token
"HomeActivity"| صفحه اصلی و مدیریت گفتگوها
"BaleApi"| ارتباط با Bale Bot API
"PollingManager"| دریافت Updateهای جدید
"StorageHelper"| مدیریت داده‌های محلی
"FileDownloader"| دریافت فایل‌های رسانه‌ای
"FileUtil"| عملیات مرتبط با فایل
"ChatAdapter"| نمایش لیست گفتگوها
"MessageAdapter"| نمایش پیام‌ها
"LogHelper"| ثبت و مدیریت Log
"ProfileCache"| Cache اطلاعات پروفایل
"SketchwareUtil"| توابع کمکی
"ReplyState"| مدیریت وضعیت Reply
"PendingUpload"| مدیریت فایل‌های در انتظار ارسال
"IncomingMessage"| مدل پیام دریافتی
"ChatOpener"| مدیریت باز کردن گفتگو

---

جریان عملکرد

فرآیند کلی برنامه:

User
  │
  ▼
MainActivity
  │
  │ Bot Token
  ▼
BaleApi
  │
  │ getMe
  ▼
Bale Bot API
  │
  │ Bot Information
  ▼
HomeActivity
  │
  ├───────────────┐
  │               │
  ▼               ▼
PollingManager   User Actions
  │               │
  │ getUpdates    │ API Requests
  ▼               ▼
Bale Bot API    BaleApi
  │               │
  └───────┬───────┘
          ▼
     Local Storage
          │
          ▼
    Chats / Messages

---

فناوری‌های استفاده‌شده

بخش| فناوری
Programming Language| Java
Platform| Android
Minimum SDK| 21
Target SDK| 34
API| Bale Bot API
HTTP Client| "HttpURLConnection"
Message Updates| Long Polling
Local Preferences| "SharedPreferences"
Local Data| JSON / Internal Storage
UI| Android Views
Layout| XML
Build System| Gradle

BaLeX برای ارتباط شبکه از "HttpURLConnection" استفاده می‌کند و به Retrofit یا OkHttp وابسته نیست.

---

نصب

نسخه‌های قابل نصب BaLeX از بخش Releases مخزن پروژه منتشر می‌شوند.

برای نصب APK:

1. وارد بخش Releases شوید.
2. نسخه مورد نظر را انتخاب کنید.
3. فایل APK موجود در بخش Assets را دریافت کنید.
4. APK را روی دستگاه Android نصب کنید.

در صورت نمایش هشدار امنیتی توسط Android، ممکن است لازم باشد اجازه نصب برنامه از منبع مورد نظر را در تنظیمات دستگاه فعال کنید.

«فقط APKهایی را نصب کنید که از منبع معتبر پروژه دریافت شده‌اند.»

---

استفاده

مرحله 1 — ورود

یک Bot Token معتبر وارد کنید.

مرحله 2 — اعتبارسنجی

BaLeX Token را از طریق Bot API بررسی می‌کند.

مرحله 3 — دریافت اطلاعات ربات

پس از اعتبارسنجی موفق، اطلاعات ربات دریافت می‌شود.

مرحله 4 — مدیریت ربات

پس از ورود به محیط اصلی می‌توانید:

- گفتگوها را مشاهده کنید.
- پیام ارسال کنید.
- پیام‌های جدید را دریافت کنید.
- به پیام‌ها Reply کنید.
- فایل و تصویر ارسال کنید.
- فایل‌های رسانه‌ای را دریافت کنید.
- در صورت نیاز Location ارسال کنید.

---

دریافت Bot Token

برای استفاده از BaLeX به یک Bot Token معتبر نیاز دارید.

مستندات Bot API:

https://dev.bale.ai

Bot Token یک اطلاعات حساس است.

هرگز Token خود را در موارد زیر منتشر نکنید:

- GitHub Repository
- GitHub Issues
- Pull Requests
- Screenshots
- Logs
- گروه‌ها
- کانال‌های عمومی
- پیام‌های عمومی

در صورت افشای Token، آن را در سریع‌ترین زمان ممکن غیرفعال یا تعویض کنید.

---

Build از Source

پیش‌نیازها

- Android Studio
- JDK سازگار با Android Gradle Plugin پروژه
- Android SDK
- Android SDK Platform 34

Clone کردن Repository

Repository را Clone کنید:

git clone https://github.com/Omid-Kordei/BaLeX.git

سپس وارد مسیر واقعی پروژه Android شوید:

cd BaLeX/src/1.0Source

«مهم: پروژه Gradle اصلی BaLeX در مسیر "src/1.0Source" قرار دارد، نه Root Repository.»

باز کردن در Android Studio

مسیر زیر را در Android Studio باز کنید:

BaLeX/src/1.0Source

سپس منتظر بمانید Gradle Sync کامل شود.

Build نسخه Debug

Linux / macOS:

./gradlew assembleDebug

Windows:

gradlew.bat assembleDebug

APK معمولاً در مسیر زیر ایجاد می‌شود:

app/build/outputs/apk/debug/

---

امنیت

امنیت Bot Token و اطلاعات محلی یکی از بخش‌های مهم BaLeX است.

Bot Token

در نسخه "1.0.0"، Token در "SharedPreferences" نگهداری می‌شود و ذخیره‌سازی آن در این نسخه به‌صورت رمزنگاری‌شده نیست.

بنابراین در دستگاه‌های Root شده یا شرایطی که دسترسی غیرمجاز به داده‌های برنامه وجود داشته باشد، احتمال استخراج Token وجود دارد.

«برای نسخه‌های آینده، استفاده از Android Keystore و ذخیره‌سازی رمزنگاری‌شده در نظر گرفته شده است.»

HTTPS

ارتباط اصلی BaLeX با Bale Bot API از HTTPS انجام می‌شود.

این پروژه در حال حاضر Certificate Pinning اختصاصی ندارد و امنیت اتصال بر TLS و سازوکارهای امنیتی Android متکی است.

Cleartext Traffic

BaLeX برای ارتباط اصلی API به HTTPS متکی است.

در صورتی که "usesCleartextTraffic" در Manifest غیرفعال باشد، Android از برقراری ترافیک HTTP معمولی جلوگیری می‌کند.

«ارتباطات غیرضروری HTTP نباید در نسخه Release فعال باشند.»

Local Storage

داده‌های محلی برنامه در فضای اختصاصی Android ذخیره می‌شوند.

این فضا به‌صورت عادی برای سایر برنامه‌ها قابل دسترسی نیست؛ با این حال، دستگاه‌های Root شده یا محیط‌های دارای دسترسی غیرمجاز می‌توانند محدودیت‌های امنیتی Android را دور بزنند.

Logging

Logها نباید شامل موارد زیر باشند:

Bot Token
Authorization Header
Session Data
Private User Data
Sensitive API Responses

---

Roadmap

قابلیت| وضعیت
Multi-Bot| Planned
Multi-Account| Planned
Advanced Dashboard| Planned
Advanced Logging| Planned
Backup / Restore| Planned
Improved File Management| Planned
Improved Polling| Planned
Token Encryption| Planned
Connection State Management| Planned
UI Improvements| Planned
Performance Optimization| Planned
Additional Administrative Tools| Planned

Roadmap ممکن است در طول توسعه پروژه تغییر کند.

---

مشارکت در توسعه

از مشارکت در توسعه BaLeX استقبال می‌شود.

ابتدا Repository را Fork کنید و سپس یک Branch ایجاد کنید:

git checkout -b feature/my-feature

پس از اعمال تغییرات:

git add .
git commit -m "Add my feature"
git push origin feature/my-feature

سپس یک Pull Request ایجاد کنید.

برای تغییرات بزرگ یا قابلیت‌های جدید، بهتر است ابتدا یک Issue ایجاد شود تا درباره طراحی و نحوه پیاده‌سازی آن گفتگو شود.

---

گزارش Bug

در صورت مشاهده مشکل، یک Issue ایجاد کنید.

اطلاعات زیر می‌تواند برای بررسی مشکل مفید باشد:

اطلاعات| توضیح
BaLeX Version| نسخه برنامه
Android Version| نسخه Android
Device| مدل دستگاه
Description| توضیح دقیق مشکل
Reproduction Steps| مراحل بازتولید
Logs| Log مرتبط
Screenshot| تصویر در صورت نیاز

⚠️ اطلاعات حساس را ارسال نکنید

هرگز موارد زیر را در Issue قرار ندهید:

Bot Token
Authorization Header
Session Data
Private Messages
Personal Information

قبل از ارسال Log، اطلاعات حساس را حذف یا Mask کنید.

---

نسخه‌بندی

BaLeX از Semantic Versioning استفاده می‌کند.

ساختار:

MAJOR.MINOR.PATCH

مثال:

v1.0.0

معنی نسخه‌ها:

- MAJOR — تغییرات بزرگ و ناسازگار
- MINOR — قابلیت‌های جدید سازگار
- PATCH — Bug Fix و اصلاحات کوچک

نسخه Release و "versionName" برنامه باید با یکدیگر هماهنگ باشند.

---

License

BaLeX تحت مجوز MIT License منتشر شده است.

متن کامل مجوز در فایل زیر قرار دارد:

LICENSE

استفاده، تغییر و توزیع پروژه مطابق شرایط MIT License مجاز است.

---

Disclaimer

BaLeX یک پروژه مستقل و غیررسمی است.

این پروژه هیچ ارتباط، همکاری، حمایت یا وابستگی رسمی با شرکت یا تیم توسعه‌دهنده پیام‌رسان بله ندارد.

BaLeX صرفاً از API ارائه‌شده توسط بله برای ارتباط با ربات‌ها استفاده می‌کند.

کاربر مسئول حفاظت از Bot Token و اطلاعات حساب خود است.

توسعه‌دهندگان BaLeX مسئولیتی در قبال موارد زیر ندارند:

- افشای Bot Token توسط کاربر
- استفاده نادرست از نرم‌افزار
- سوءاستفاده از حساب ربات
- از دست رفتن اطلاعات ناشی از استفاده نادرست
- مشکلات ناشی از سرویس یا API شخص ثالث

استفاده از BaLeX باید مطابق قوانین و شرایط استفاده از سرویس بله و API مربوطه انجام شود.

---

Project Information

Property| Value
Project| BaLeX
Platform| Android
Language| Java
API| Bale Bot API
Minimum SDK| 21
Target SDK| 34
License| MIT
Version| 1.0.0
Status| Active Development

---

<p align="center">
  <strong>BaLeX</strong>
</p><p align="center">
  Independent Android Client for Bale Bot Management
</p><p align="center">
  Developed for Android and the open-source community.
</p>  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License">
</p>

<p align="center">
  <a href="#معرفی">معرفی</a> •
  <a href="#قابلیت‌ها">قابلیت‌ها</a> •
  <a href="#معماری">معماری</a> •
  <a href="#نصب">نصب</a> •
  <a href="#توسعه">توسعه</a> •
  <a href="#امنیت">امنیت</a>
</p>

---

## معرفی

BaLeX یک کلاینت اندرویدی مستقل برای مدیریت و تعامل با ربات‌های پیام‌رسان بله (Bale) است.

این برنامه با هدف ایجاد یک محیط اختصاصی و کاربردی برای مدیریت ربات از طریق تلفن همراه توسعه داده شده و امکاناتی مانند مشاهده گفتگوها، ارسال و دریافت پیام، مدیریت فایل‌ها، ارسال موقعیت مکانی، مشاهده اطلاعات ربات و ثبت رویدادهای برنامه را فراهم می‌کند.

BaLeX برای ارتباط با ربات به سرور واسط اختصاصی نیاز ندارد و درخواست‌های مربوط به Bot API مستقیماً از دستگاه کاربر ارسال می‌شوند.

> **توجه:** BaLeX یک پروژه مستقل و غیررسمی است و هیچ ارتباط رسمی با شرکت یا تیم توسعه‌دهنده پیام‌رسان بله ندارد.

---

## وضعیت پروژه

| مورد              | وضعیت              |
|-------------------|--------------------|
| نسخه فعلی         | 1.0.0              |
| وضعیت             | Stable             |
| پلتفرم            | Android            |
| زبان              | Java               |
| Minimum SDK       | 21                 |
| Target SDK        | 34                 |
| ارتباط API        | Bale Bot API       |
| Network Layer     | HttpURLConnection  |
| دریافت پیام       | Long Polling       |
| رابط کاربری       | Android Views      |
| License           | MIT                |

---

## قابلیت‌ها

### مدیریت ربات
- ورود با Bot Token
- اعتبارسنجی Token
- دریافت اطلاعات ربات
- نمایش نام، Username و شناسه ربات
- ذخیره اطلاعات مورد نیاز حساب
- خروج از حساب و پاک‌سازی اطلاعات ذخیره‌شده

### مدیریت گفتگو
- نمایش لیست گفتگوهای ربات
- پشتیبانی از گفتگوهای خصوصی و گروهی
- فیلتر گفتگوها
- باز کردن صفحه اختصاصی هر گفتگو
- نمایش پیام‌های دریافتی و ارسال‌شده
- پاسخ مستقیم به پیام‌ها

### پیام‌رسانی
- ارسال پیام متنی
- دریافت پیام‌های جدید
- دریافت تقریباً بلادرنگ پیام‌ها
- Reply به پیام‌ها
- مدیریت وضعیت پیام‌ها

### فایل و رسانه
- ارسال تصویر
- ارسال فایل
- انتخاب فایل از حافظه دستگاه
- دانلود فایل‌های رسانه‌ای
- مدیریت فایل‌های دریافت‌شده

### موقعیت مکانی
- دریافت موقعیت مکانی از دستگاه
- ارسال Location به گفتگو
- استفاده از GPS در صورت نیاز

### سیستم Log
- ثبت خطاهای برنامه
- ثبت خطاهای شبکه
- نمایش اطلاعات مربوط به API
- کمک به بررسی و رفع مشکلات

---

## معماری

BaLeX با Java و Android SDK توسعه داده شده و ساختار آن بر پایه Activityها و کلاس‌های کمکی اختصاصی شکل گرفته است.  </a>
  <img src="https://img.shields.io/badge/Java-Android%20SDK-orange?style=for-the-badge&logo=java&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License">
</p><p align="center">
  <a href="#معرفی">معرفی</a> •
  <a href="#قابلیت‌ها">قابلیت‌ها</a> •
  <a href="#معماری">معماری</a> •
  <a href="#نصب">نصب</a> •
  <a href="#توسعه">توسعه</a> •
  <a href="#امنیت">امنیت</a>
</p>---

معرفی

BaLeX یک کلاینت اندرویدی مستقل برای مدیریت و تعامل با ربات‌های پیام‌رسان بله (Bale) است.

این برنامه با هدف ایجاد یک محیط اختصاصی و کاربردی برای مدیریت ربات از طریق تلفن همراه توسعه داده شده است و امکاناتی مانند مشاهده گفتگوها، ارسال و دریافت پیام، مدیریت فایل‌ها، ارسال موقعیت مکانی، مشاهده اطلاعات ربات و ثبت رویدادهای برنامه را فراهم می‌کند.

BaLeX برای ارتباط با ربات به سرور واسط اختصاصی نیاز ندارد و درخواست‌های مربوط به Bot API مستقیماً از دستگاه کاربر ارسال می‌شوند.

«BaLeX یک پروژه مستقل و غیررسمی است و هیچ ارتباط رسمی با شرکت یا تیم توسعه‌دهنده پیام‌رسان بله ندارد.»

---

وضعیت پروژه

مورد| وضعیت
نسخه فعلی| "1.0.0"
وضعیت| Stable
پلتفرم| Android
زبان| Java
Minimum SDK| 21
Target SDK| 34
ارتباط API| Bale Bot API
Network Layer| HttpURLConnection
دریافت پیام| Long Polling
رابط کاربری| Android Views
License| MIT

---

قابلیت‌ها

مدیریت ربات

- ورود با Bot Token
- اعتبارسنجی Token
- دریافت اطلاعات ربات
- نمایش نام، Username و شناسه ربات
- ذخیره اطلاعات مورد نیاز حساب
- خروج از حساب و پاک‌سازی اطلاعات ذخیره‌شده

مدیریت گفتگو

- نمایش لیست گفتگوهای ربات
- پشتیبانی از گفتگوهای خصوصی و گروهی
- فیلتر گفتگوها
- باز کردن صفحه اختصاصی هر گفتگو
- نمایش پیام‌های دریافتی و ارسال‌شده
- پاسخ مستقیم به پیام‌ها

پیام‌رسانی

- ارسال پیام متنی
- دریافت پیام‌های جدید
- دریافت تقریباً بلادرنگ پیام‌ها
- Reply به پیام‌ها
- مدیریت وضعیت پیام‌ها

فایل و رسانه

- ارسال تصویر
- ارسال فایل
- انتخاب فایل از حافظه دستگاه
- دانلود فایل‌های رسانه‌ای
- مدیریت فایل‌های دریافت‌شده

موقعیت مکانی

- دریافت موقعیت مکانی از دستگاه
- ارسال Location به گفتگو
- استفاده از GPS در صورت نیاز

سیستم Log

- ثبت خطاهای برنامه
- ثبت خطاهای شبکه
- نمایش اطلاعات مربوط به API
- کمک به بررسی و رفع مشکلات

---

معماری

BaLeX با Java و Android SDK توسعه داده شده و ساختار آن بر پایه Activityها و کلاس‌های کمکی اختصاصی شکل گرفته است.

BaLeX/
│
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   │
│   └── src/
│       └── main/
│           │
│           ├── AndroidManifest.xml
│           │
│           ├── java/
│           │   └── ir/
│           │       └── BaleX/
│           │           │
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
│           │
│           └── res/
│               ├── drawable/
│               ├── layout/
│               ├── mipmap-*/
│               ├── values/
│               └── ...
│
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md

اجزای اصلی

کلاس| مسئولیت
"MainActivity"| صفحه ورود و دریافت Bot Token
"HomeActivity"| صفحه اصلی و مدیریت گفتگوها
"BaleApi"| لایه ارتباط با Bale Bot API
"PollingManager"| دریافت Updateهای جدید
"StorageHelper"| مدیریت داده‌های محلی
"FileDownloader"| دریافت فایل‌های رسانه‌ای
"FileUtil"| عملیات مرتبط با فایل
"ChatAdapter"| نمایش لیست گفتگوها
"MessageAdapter"| نمایش پیام‌های گفتگو
"LogHelper"| ثبت و مدیریت Log
"ProfileCache"| Cache اطلاعات پروفایل
"SketchwareUtil"| توابع کمکی رابط کاربری
"ReplyState"| مدیریت وضعیت Reply
"PendingUpload"| مدیریت فایل‌های در انتظار ارسال
"IncomingMessage"| مدل پیام‌های دریافتی
"ChatOpener"| مدیریت باز کردن گفتگو

---

جریان عملکرد

فرآیند اصلی BaLeX به شکل زیر انجام می‌شود:

User
  |
  v
MainActivity
  |
  | Bot Token
  v
BaleApi
  |
  | getMe
  v
Bale Bot API
  |
  | Bot Information
  v
HomeActivity
  |
  +-------------------+
  |                   |
  v                   v
PollingManager     User Actions
  |                   |
  | getUpdates        |
  v                   v
Bale Bot API       BaleApi
  |                   |
  +---------+---------+
            |
            v
      Local Storage
            |
            v
       Chat / Messages

---

فناوری‌های استفاده‌شده

بخش| فناوری
Programming Language| Java
Platform| Android
Minimum SDK| 21
Target SDK| 34
API| Bale Bot API
HTTP Client| "HttpURLConnection"
Message Updates| Long Polling
Local Storage| "SharedPreferences"
Local Data| JSON / Internal Storage
UI| Android Views
Layout| XML
UI Components| AndroidX AppCompat / Material Components

BaLeX برای ارتباط شبکه از "HttpURLConnection" استفاده می‌کند و در لایه شبکه به Retrofit یا OkHttp وابسته نیست.

---

نصب

فایل APK نسخه‌های رسمی از طریق بخش Releases مخزن پروژه منتشر می‌شوند.

برای نصب نسخه منتشرشده:

1. وارد بخش Releases شوید.
2. نسخه مورد نظر را انتخاب کنید.
3. فایل APK را از بخش Assets دریافت کنید.
4. APK را روی دستگاه Android نصب کنید.

در صورتی که Android اجازه نصب از منبع مورد نظر را ندهد، باید مجوز نصب برنامه از آن منبع را در تنظیمات دستگاه فعال کنید.

---

استفاده

پس از اجرای برنامه:

مرحله اول

Bot Token معتبر ربات خود را وارد کنید.

مرحله دوم

BaLeX Token را از طریق API بررسی می‌کند.

مرحله سوم

پس از تأیید Token، اطلاعات ربات دریافت شده و کاربر وارد محیط اصلی برنامه می‌شود.

مرحله چهارم

در محیط اصلی می‌توانید گفتگوهای ربات را مشاهده کرده و عملیات مختلف پیام‌رسانی و مدیریت را انجام دهید.

---

دریافت Bot Token

برای استفاده از BaLeX به یک Bot Token معتبر نیاز دارید.

مستندات Bot API:

https://dev.bale.ai

Token خود را محرمانه نگه دارید و از انتشار آن در Repository، Screenshot، Issue یا سایر مکان‌های عمومی خودداری کنید.

---

Build از Source

پیش‌نیازها

- Android Studio
- JDK سازگار با نسخه Gradle پروژه
- Android SDK
- Android SDK Platform 34

Clone

git clone https://github.com/<username>/BaLeX.git
cd BaLeX

پروژه را در Android Studio باز کنید و منتظر بمانید Gradle Sync کامل شود.

Build Debug

Linux / macOS:

./gradlew assembleDebug

Windows:

gradlew.bat assembleDebug

خروجی APK در مسیر مشابه زیر قرار خواهد گرفت:

app/build/outputs/apk/

---

امنیت

امنیت اطلاعات ربات یکی از بخش‌های مهم استفاده از BaLeX است.

Bot Token

در نسخه فعلی، Bot Token در "SharedPreferences" ذخیره می‌شود و به‌صورت رمزنگاری‌شده نگهداری نمی‌شود.

بنابراین در دستگاه‌های Root شده یا شرایطی که دسترسی غیرمجاز به اطلاعات برنامه وجود داشته باشد، امکان استخراج Token وجود دارد.

HTTPS

ارتباطات اصلی BaLeX با API از HTTPS استفاده می‌کنند.

نسخه فعلی Certificate Pinning اختصاصی ندارد و امنیت اتصال بر TLS و سازوکارهای امنیتی سیستم Android متکی است.

Local Storage

اطلاعات مورد نیاز برنامه و برخی داده‌های مربوط به گفتگوها در حافظه داخلی برنامه ذخیره می‌شوند.

حافظه داخلی Android به‌صورت پیش‌فرض برای سایر برنامه‌ها قابل دسترسی نیست، اما در دستگاه‌های Root شده یا شرایط خاص امکان دسترسی وجود دارد.

Cleartext Traffic

در صورت فعال بودن "usesCleartextTraffic" در Manifest، سیستم Android امکان ترافیک HTTP را نیز فراهم می‌کند.

با توجه به اینکه ارتباط اصلی برنامه با API از HTTPS انجام می‌شود، این تنظیم می‌تواند در نسخه‌های آینده محدودتر شود.

مسئولیت Token

Bot Token یک اطلاعات حساس محسوب می‌شود.

در صورت افشای Token، فرد دیگری ممکن است بتواند از طرف ربات عملیات مجاز API را انجام دهد.

هرگز Token را در موارد زیر منتشر نکنید:

GitHub Repository
GitHub Issues
Pull Requests
Screenshots
Logs
Public Groups
Public Channels

---

توسعه‌های آینده

توسعه BaLeX در نسخه‌های آینده می‌تواند در زمینه‌های زیر ادامه پیدا کند:

قابلیت| وضعیت
Multi-Bot| Planned
Multi-Account| Planned
Advanced Dashboard| Planned
Advanced Logging| Planned
Backup / Restore| Planned
Improved File Management| Planned
Improved Polling| Planned
Token Encryption| Planned
UI Improvements| Planned
Performance Optimization| Planned
Additional Administrative Tools| Planned

این فهرست ممکن است در طول توسعه پروژه تغییر کند.

---

مشارکت در توسعه

مشارکت در توسعه BaLeX آزاد است.

برای شروع:

git clone https://github.com/<username>/BaLeX.git
cd BaLeX

یک Branch جدید ایجاد کنید:

git checkout -b feature/my-feature

پس از اعمال تغییرات:

git add .
git commit -m "Add my feature"
git push origin feature/my-feature

سپس یک Pull Request ایجاد کنید.

برای تغییرات بزرگ، پیشنهاد می‌شود ابتدا یک Issue ایجاد شود تا درباره طراحی و نحوه پیاده‌سازی تغییر مورد نظر گفتگو شود.

---

گزارش مشکلات

برای گزارش Bug یک Issue ایجاد کنید.

اطلاعات پیشنهادی:

اطلاعات| توضیح
BaLeX Version| نسخه برنامه
Android Version| نسخه Android
Device| مدل دستگاه
Description| توضیح مشکل
Reproduction Steps| مراحل ایجاد مشکل
Logs| Log مرتبط
Screenshot| تصویر در صورت نیاز

هرگز اطلاعات محرمانه مانند Bot Token، Session یا اطلاعات حساب را در Issue قرار ندهید.

---

GitHub

پروژه BaLeX در GitHub توسعه و مدیریت می‌شود.

برای دریافت آخرین نسخه، مشاهده Source Code، گزارش Bug و مشارکت در توسعه، به Repository پروژه مراجعه کنید.

---

License

BaLeX تحت MIT License منتشر شده است.

جزئیات کامل مجوز در فایل زیر قرار دارد:

LICENSE

استفاده، تغییر و توزیع پروژه مطابق شرایط MIT License مجاز است.

---

Disclaimer

BaLeX یک پروژه مستقل و غیررسمی است.

این پروژه هیچ ارتباط رسمی، همکاری رسمی یا وابستگی رسمی با شرکت یا تیم توسعه‌دهنده پیام‌رسان بله ندارد.

BaLeX صرفاً از API ارائه‌شده توسط بله برای ارتباط با ربات‌ها استفاده می‌کند.

کاربر مسئول حفاظت از Bot Token و اطلاعات حساب خود است.

توسعه‌دهندگان BaLeX مسئولیتی در قبال استفاده نادرست از نرم‌افزار، افشای Token، از دست رفتن اطلاعات یا سوءاستفاده از حساب ربات نخواهند داشت.

استفاده از BaLeX باید مطابق قوانین و شرایط استفاده از سرویس بله و API مربوطه انجام شود.

---

Project Information

Property| Value
Project| BaLeX
Platform| Android
Language| Java
API| Bale Bot API
Min SDK| 21
Target SDK| 34
License| MIT
Current Version| "1.0.0"
Status| Active Development

---

<p align="center">
  <strong>BaLeX</strong>
</p><p align="center">
  Independent Android Client for Bale Bot Management
</p><p align="center">
  Developed for Android and the open-source community.
</p>2. نسخه مورد نظر را انتخاب کنید.
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
