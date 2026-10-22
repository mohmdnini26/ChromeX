package com.example.ui.components

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.BrowserTab
import com.example.viewmodel.BrowserAction
import kotlinx.coroutines.flow.SharedFlow

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    tab: BrowserTab,
    javascriptEnabled: Boolean,
    browserActionFlow: SharedFlow<BrowserAction>,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String, String?) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onNavigationStateChanged: (Boolean, Boolean) -> Unit,
    onFindResult: (Int, Int) -> Unit,
    onDownloadRequested: (fileName: String, url: String, filePath: String, fileSize: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Hold reference to WebView for this tab
    val webView = remember(tab.id) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = false
                allowContentAccess = false
            }

            // Incognito session isolated settings
            if (tab.isIncognito) {
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                clearHistory()
                clearCache(true)
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    onProgressChanged(newProgress)
                    view?.let {
                        onNavigationStateChanged(it.canGoBack(), it.canGoForward())
                    }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    val url = view?.url ?: tab.url
                    onPageFinished(url, title)
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    url?.let {
                        onPageStarted(it)
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let {
                        onPageFinished(it, view?.title)
                        onNavigationStateChanged(view?.canGoBack() == true, view?.canGoForward() == true)
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false

                    // Handle intent:, tel:, mailto: links
                    if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("intent:")) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "امکان باز کردن پیوند وجود ندارد", Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                    return false
                }
            }

            setFindListener { activeMatchOrdinal, numberOfMatches, isDoneCounting ->
                if (isDoneCounting) {
                    onFindResult(if (numberOfMatches > 0) activeMatchOrdinal + 1 else 0, numberOfMatches)
                }
            }

            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                try {
                    val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
                    val request = DownloadManager.Request(Uri.parse(url)).apply {
                        setMimeType(mimetype)
                        addRequestHeader("User-Agent", userAgent)
                        setDescription("در حال بارگیری فایل از طریق کروم")
                        setTitle(filename)
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                    }

                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                    dm?.enqueue(request)

                    val fileSizeFormatted = if (contentLength > 0) {
                        "${contentLength / (1024 * 1024)} مگابایت"
                    } else "نامشخص"

                    val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$filename"
                    onDownloadRequested(filename, url, path, fileSizeFormatted)
                    Toast.makeText(context, "شروع بارگیری: $filename", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "خطا در شروع بارگیری فایل", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Configure User-Agent for Desktop Mode
    LaunchedEffect(tab.isDesktopSite) {
        if (tab.isDesktopSite) {
            webView.settings.userAgentString = DESKTOP_USER_AGENT
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true
        } else {
            webView.settings.userAgentString = null
        }
    }

    // Configure JavaScript
    LaunchedEffect(javascriptEnabled) {
        webView.settings.javaScriptEnabled = javascriptEnabled
    }

    // Load initial url if not empty and not new tab
    LaunchedEffect(tab.url) {
        if (!tab.isNewTabPage && webView.url != tab.url) {
            webView.loadUrl(tab.url)
        }
    }

    // Listen to ViewModel actions (GoBack, GoForward, Reload, Find, Clear)
    LaunchedEffect(browserActionFlow) {
        browserActionFlow.collect { action ->
            when (action) {
                is BrowserAction.LoadUrl -> {
                    webView.loadUrl(action.url)
                }
                is BrowserAction.GoBack -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    }
                }
                is BrowserAction.GoForward -> {
                    if (webView.canGoForward()) {
                        webView.goForward()
                    }
                }
                is BrowserAction.Reload -> {
                    webView.reload()
                }
                is BrowserAction.StopLoading -> {
                    webView.stopLoading()
                }
                is BrowserAction.FindInPage -> {
                    webView.findAllAsync(action.query)
                }
                is BrowserAction.FindNext -> {
                    webView.findNext(action.forward)
                }
                is BrowserAction.ClearMatches -> {
                    webView.clearMatches()
                }
                is BrowserAction.ClearCacheAndCookies -> {
                    webView.clearCache(true)
                    CookieManager.getInstance().removeAllCookies(null)
                    WebStorage.getInstance().deleteAllData()
                    Toast.makeText(context, "داده‌های مرور و حافظه پنهان پاک شدند", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DisposableEffect(tab.id) {
        onDispose {
            webView.stopLoading()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}
