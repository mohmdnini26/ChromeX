package com.example.model

import android.graphics.Bitmap
import java.util.UUID

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "برگه جدید",
    val url: String = "chrome://newtab",
    val displayUrl: String = "",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isIncognito: Boolean = false,
    val isDesktopSite: Boolean = false,
    val isSecure: Boolean = false,
    val previewBitmap: Bitmap? = null
) {
    val isNewTabPage: Boolean
        get() = url.isEmpty() || url == "chrome://newtab" || url == "about:blank"
}

data class WebShortcut(
    val title: String,
    val url: String,
    val iconLetter: String,
    val colorHex: Long
)

enum class SearchEngine(val displayName: String, val searchUrl: String) {
    GOOGLE("گوگل (Google)", "https://www.google.com/search?q="),
    BING("بینگ (Bing)", "https://www.bing.com/search?q="),
    DUCKDUCKGO("داک‌داک‌گو (DuckDuckGo)", "https://duckduckgo.com/?q="),
    YAHOO("یاهو (Yahoo)", "https://search.yahoo.com/search?q=")
}
