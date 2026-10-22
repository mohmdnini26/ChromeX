package com.example.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppColorTheme(val displayNameFa: String, val displayNameEn: String, val previewColor: Color) {
    CHROME_BLUE("آبی کروم", "Chrome Blue", Color(0xFF1A73E8)),
    PINK("صورتی", "Pink", Color(0xFFFF4081)),
    YELLOW("زرد", "Yellow", Color(0xFFFFD600)),
    PHOSPHOR_GREEN("سبز فسفری", "Neon Green", Color(0xFF00E676)),
    BLACK("سیاه (AMOLED)", "Pure Black", Color(0xFF121212)),
    ORANGE("نارنجی", "Vivid Orange", Color(0xFFFF6D00))
}

enum class AppLanguage(val code: String, val title: String) {
    PERSIAN("fa", "فارسی"),
    ENGLISH("en", "English"),
    ARABIC("ar", "العربية")
}

data class LocalizedStrings(
    val searchOrTypeUrl: String,
    val newTab: String,
    val newIncognitoTab: String,
    val history: String,
    val bookmarks: String,
    val downloads: String,
    val share: String,
    val findInPage: String,
    val desktopSite: String,
    val settings: String,
    val tabs: String,
    val incognitoTabs: String,
    val regular: String,
    val incognito: String,
    val closeAllTabs: String,
    val noTabsOpen: String,
    val topSites: String,
    val youHaveGoneIncognito: String,
    val incognitoDescription: String,
    val incognitoDoesNotSave: String,
    val incognitoBullets1: String,
    val searchEngine: String,
    val clearBrowsingData: String,
    val clearDataSubtitle: String,
    val javascript: String,
    val javascriptAllowed: String,
    val javascriptBlocked: String,
    val themes: String,
    val language: String,
    val dancingLights: String,
    val dancingLightsSubtitle: String,
    val clear: String,
    val cancel: String,
    val clearAll: String,
    val noBookmarks: String,
    val noHistory: String,
    val noDownloads: String
)

val StringsFa = LocalizedStrings(
    searchOrTypeUrl = "جستجو یا نشانی وب را تایپ کنید",
    newTab = "برگه جدید",
    newIncognitoTab = "برگه ناشناس جدید",
    history = "تاریخچه",
    bookmarks = "نشانک‌ها",
    downloads = "بارگیری‌ها",
    share = "اشتراک‌گذاری...",
    findInPage = "یافتن در صفحه",
    desktopSite = "سایت مخصوص رایانه",
    settings = "تنظیمات",
    tabs = "برگه‌ها",
    incognitoTabs = "برگه‌های ناشناس",
    regular = "عادی",
    incognito = "ناشناس",
    closeAllTabs = "بستن همه برگه‌ها",
    noTabsOpen = "هیچ برگه‌ای باز نیست",
    topSites = "سایت‌های برتر",
    youHaveGoneIncognito = "وارد حالت ناشناس شدید",
    incognitoDescription = "اکنون می‌توانید به‌صورت خصوصی وب‌گردی کنید و سایر افرادی که از این دستگاه استفاده می‌کنند، فعالیت شما را نخواهند دید.",
    incognitoDoesNotSave = "کروم موارد زیر را ذخیره نمی‌کند:",
    incognitoBullets1 = "• تاریخچه مرور شما\n• کوکی‌ها و داده‌های سایت\n• اطلاعات فرم‌ها",
    searchEngine = "موتور جستجو",
    clearBrowsingData = "پاک کردن داده‌های مرور",
    clearDataSubtitle = "پاک‌سازی تاریخچه، کوکی‌ها و حافظه پنهان",
    javascript = "جاوا اسکریپت (JavaScript)",
    javascriptAllowed = "مجاز است (پیشنهادی)",
    javascriptBlocked = "مسدود شده است",
    themes = "تم رنگی برنامه",
    language = "زبان برنامه",
    dancingLights = "رقص نور دور میانبرها و جستجو",
    dancingLightsSubtitle = "افکت نوری RGB متحرک",
    clear = "پاک کردن",
    cancel = "لغو",
    clearAll = "پاک‌سازی همه",
    noBookmarks = "هیچ نشانکی ذخیره نشده است",
    noHistory = "تاریخچه‌ای وجود ندارد",
    noDownloads = "فایل بارگیری‌شده‌ای وجود ندارد"
)

val StringsEn = LocalizedStrings(
    searchOrTypeUrl = "Search or type URL",
    newTab = "New tab",
    newIncognitoTab = "New incognito tab",
    history = "History",
    bookmarks = "Bookmarks",
    downloads = "Downloads",
    share = "Share...",
    findInPage = "Find in page",
    desktopSite = "Desktop site",
    settings = "Settings",
    tabs = "Tabs",
    incognitoTabs = "Incognito tabs",
    regular = "Regular",
    incognito = "Incognito",
    closeAllTabs = "Close all tabs",
    noTabsOpen = "No tabs open",
    topSites = "Top sites",
    youHaveGoneIncognito = "You've gone incognito",
    incognitoDescription = "Now you can browse privately, and other people who use this device won't see your activity.",
    incognitoDoesNotSave = "Chrome won't save:",
    incognitoBullets1 = "• Your browsing history\n• Cookies and site data\n• Info entered in forms",
    searchEngine = "Search engine",
    clearBrowsingData = "Clear browsing data",
    clearDataSubtitle = "Clear history, cookies, and cache",
    javascript = "JavaScript",
    javascriptAllowed = "Allowed (recommended)",
    javascriptBlocked = "Blocked",
    themes = "Color Theme",
    language = "App Language",
    dancingLights = "RGB Dancing Lights",
    dancingLightsSubtitle = "Animated rainbow glow borders",
    clear = "Clear",
    cancel = "Cancel",
    clearAll = "Clear all",
    noBookmarks = "No bookmarks saved",
    noHistory = "No history found",
    noDownloads = "No downloads"
)

val StringsAr = LocalizedStrings(
    searchOrTypeUrl = "البحث أو كتابة عنوان URL",
    newTab = "علامة تبويب جديدة",
    newIncognitoTab = "علامة تبويب للتصفح المتخفي",
    history = "السجل",
    bookmarks = "الإشارات المرجعية",
    downloads = "التنزيلات",
    share = "مشاركة...",
    findInPage = "البحث في الصفحة",
    desktopSite = "الموقع المخصص للكمبيوتر",
    settings = "الإعدادات",
    tabs = "علامات التبويب",
    incognitoTabs = "علامات التبويب المتخفية",
    regular = "عادي",
    incognito = "متخفٍ",
    closeAllTabs = "إغلاق جميع علامات التبويب",
    noTabsOpen = "لا توجد علامات تبويب مفتوحة",
    topSites = "المواقع الأكثر زيارة",
    youHaveGoneIncognito = "لقد انتقلت إلى وضع التصفح المتخفي",
    incognitoDescription = "يمكنك الآن التصفح بخصوصية ولن يرى المستخدمون الآخرون لهذا الجهاز نشاطك.",
    incognitoDoesNotSave = "لن يحفظ Chrome ما يلي:",
    incognitoBullets1 = "• سجل التصفح\n• ملفات تعريف الارتباط وبيانات الموقع\n• المعلومات المدخلة في النماذج",
    searchEngine = "محرك البحث",
    clearBrowsingData = "محو بيانات التصفح",
    clearDataSubtitle = "محو السجل وملفات تعريف الارتباط وذاكرة التخزين المؤقت",
    javascript = "جافا سكريبت (JavaScript)",
    javascriptAllowed = "مسموح به (مستحسن)",
    javascriptBlocked = "محظور",
    themes = "سمة اللون",
    language = "لغة التطبيق",
    dancingLights = "أضواء RGB متحركة حول البحث والمواقع",
    dancingLightsSubtitle = "تأثير إضاءة ملون نابض بالحياة",
    clear = "محو",
    cancel = "إلغاء",
    clearAll = "محو الكل",
    noBookmarks = "لا توجد إشارات مرجعية محفوظة",
    noHistory = "لا يوجد سجل",
    noDownloads = "لا توجد تنزيلات"
)

fun getAppStrings(language: AppLanguage): LocalizedStrings {
    return when (language) {
        AppLanguage.PERSIAN -> StringsFa
        AppLanguage.ENGLISH -> StringsEn
        AppLanguage.ARABIC -> StringsAr
    }
}

fun createColorPalette(appTheme: AppColorTheme, isDark: Boolean) = when (appTheme) {
    AppColorTheme.PINK -> if (isDark) {
        darkColorScheme(
            primary = Color(0xFFFF4081),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF88003A),
            onPrimaryContainer = Color(0xFFFFD8E4),
            surface = Color(0xFF221A1D),
            surfaceVariant = Color(0xFF33242A),
            background = Color(0xFF191214)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFE91E63),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFD8E4),
            onPrimaryContainer = Color(0xFF3B0017),
            surface = Color.White,
            surfaceVariant = Color(0xFFFFF0F4),
            background = Color(0xFFFFF9FA)
        )
    }

    AppColorTheme.YELLOW -> if (isDark) {
        darkColorScheme(
            primary = Color(0xFFFFD600),
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF6B5800),
            onPrimaryContainer = Color(0xFFFFE082),
            surface = Color(0xFF222018),
            surfaceVariant = Color(0xFF332F20),
            background = Color(0xFF181711)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFFBC02D),
            onPrimary = Color.Black,
            primaryContainer = Color(0xFFFFF9C4),
            onPrimaryContainer = Color(0xFF2A2300),
            surface = Color.White,
            surfaceVariant = Color(0xFFFFFDE7),
            background = Color(0xFFFFFFF8)
        )
    }

    AppColorTheme.PHOSPHOR_GREEN -> if (isDark) {
        darkColorScheme(
            primary = Color(0xFF00E676),
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF006B33),
            onPrimaryContainer = Color(0xFFB9F6CA),
            surface = Color(0xFF16211A),
            surfaceVariant = Color(0xFF213227),
            background = Color(0xFF101913)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF00C853),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFB9F6CA),
            onPrimaryContainer = Color(0xFF003915),
            surface = Color.White,
            surfaceVariant = Color(0xFFE8F5E9),
            background = Color(0xFFF1F8F4)
        )
    }

    AppColorTheme.BLACK -> darkColorScheme(
        primary = Color(0xFF90CAF9),
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF1E3A5F),
        onPrimaryContainer = Color(0xFFD1E4FF),
        surface = Color(0xFF121212),
        surfaceVariant = Color(0xFF1E1E1E),
        background = Color(0xFF000000),
        onBackground = Color.White,
        onSurface = Color.White
    )

    AppColorTheme.ORANGE -> if (isDark) {
        darkColorScheme(
            primary = Color(0xFFFF6D00),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF7A3300),
            onPrimaryContainer = Color(0xFFFFDCC2),
            surface = Color(0xFF221C18),
            surfaceVariant = Color(0xFF332921),
            background = Color(0xFF19130F)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFFF6D00),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFE0B2),
            onPrimaryContainer = Color(0xFF4E1F00),
            surface = Color.White,
            surfaceVariant = Color(0xFFFFF3E0),
            background = Color(0xFFFFFBF7)
        )
    }

    AppColorTheme.CHROME_BLUE -> if (isDark) {
        darkColorScheme(
            primary = ChromeBlueDark,
            onPrimary = Color(0xFF003258),
            primaryContainer = Color(0xFF00497D),
            onPrimaryContainer = Color(0xFFD1E4FF),
            surface = SurfaceDark,
            surfaceVariant = SurfaceVariantDark,
            background = SurfaceDark
        )
    } else {
        lightColorScheme(
            primary = ChromeBlue,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFD1E4FF),
            onPrimaryContainer = Color(0xFF001D36),
            surface = SurfaceLight,
            surfaceVariant = OmniboxLight,
            background = Color(0xFFF8F9FA)
        )
    }
}
