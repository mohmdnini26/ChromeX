package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.BrowserTab
import com.example.ui.components.BrowserWebView
import com.example.ui.components.ChromeMenu
import com.example.ui.components.ChromeOmnibox
import com.example.ui.components.FindInPageBar
import com.example.ui.components.NewTabPage
import com.example.ui.components.TabSwitcherScreen
import com.example.ui.dialogs.BookmarksSheet
import com.example.ui.dialogs.ClearBrowsingDataDialog
import com.example.ui.dialogs.DownloadsSheet
import com.example.ui.dialogs.HistorySheet
import com.example.ui.dialogs.SettingsSheet
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.getAppStrings
import com.example.viewmodel.BrowserViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val tabs by viewModel.tabs.collectAsStateWithLifecycle()
            val activeTabId by viewModel.activeTabId.collectAsStateWithLifecycle()
            val colorTheme by viewModel.colorTheme.collectAsStateWithLifecycle()
            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
            val activeTab = tabs.firstOrNull { it.id == activeTabId } ?: tabs.firstOrNull() ?: BrowserTab()

            val layoutDirection = when (appLanguage) {
                AppLanguage.PERSIAN, AppLanguage.ARABIC -> LayoutDirection.Rtl
                AppLanguage.ENGLISH -> LayoutDirection.Ltr
            }

            MyApplicationTheme(
                appColorTheme = colorTheme,
                isIncognito = activeTab.isIncognito
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    ChromeBrowserScreen(
                        viewModel = viewModel,
                        tabs = tabs,
                        activeTab = activeTab
                    )
                }
            }
        }
    }
}

@Composable
fun ChromeBrowserScreen(
    viewModel: BrowserViewModel,
    tabs: List<BrowserTab>,
    activeTab: BrowserTab
) {
    val context = LocalContext.current
    var isMenuExpanded by remember { mutableStateOf(false) }

    val isTabSwitcherOpen by viewModel.isTabSwitcherOpen.collectAsStateWithLifecycle()
    val isIncognitoSelected by viewModel.tabSwitcherIncognitoSelected.collectAsStateWithLifecycle()
    val isFindInPageVisible by viewModel.isFindInPageVisible.collectAsStateWithLifecycle()
    val findQuery by viewModel.findQuery.collectAsStateWithLifecycle()
    val findMatchCount by viewModel.findMatchCount.collectAsStateWithLifecycle()

    val colorTheme by viewModel.colorTheme.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val dancingLightsEnabled by viewModel.dancingLightsEnabled.collectAsStateWithLifecycle()
    val currentWallpaperIndex by viewModel.currentWallpaperIndex.collectAsStateWithLifecycle()
    val wallpaperResId = if (viewModel.wallpapers.isNotEmpty()) {
        viewModel.wallpapers[currentWallpaperIndex % viewModel.wallpapers.size]
    } else null
    val strings = remember(appLanguage) { getAppStrings(appLanguage) }

    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val searchEngine by viewModel.searchEngine.collectAsStateWithLifecycle()
    val javascriptEnabled by viewModel.javascriptEnabled.collectAsStateWithLifecycle()
    val isCurrentBookmarked by viewModel.isCurrentBookmarked.collectAsStateWithLifecycle()

    val isHistoryOpen by viewModel.isHistoryOpen.collectAsStateWithLifecycle()
    val isBookmarksOpen by viewModel.isBookmarksOpen.collectAsStateWithLifecycle()
    val isDownloadsOpen by viewModel.isDownloadsOpen.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val isClearDataDialogOpen by viewModel.isClearDataDialogOpen.collectAsStateWithLifecycle()

    // Smart BackHandler: Chrome behaviour
    BackHandler(enabled = true) {
        when {
            isFindInPageVisible -> viewModel.closeFindInPage()
            isMenuExpanded -> isMenuExpanded = false
            isTabSwitcherOpen -> viewModel.toggleTabSwitcher()
            activeTab.canGoBack -> viewModel.goBack()
            tabs.size > 1 && !activeTab.isNewTabPage -> viewModel.navigateTo("chrome://newtab")
            else -> {
                (context as? ComponentActivity)?.finish()
            }
        }
    }

    if (isTabSwitcherOpen) {
        TabSwitcherScreen(
            tabs = tabs,
            activeTabId = activeTab.id,
            isIncognitoSelected = isIncognitoSelected,
            strings = strings,
            onTabSelected = { viewModel.selectTab(it) },
            onTabClosed = { viewModel.closeTab(it) },
            onNewTabClick = { isIncognito -> viewModel.openNewTab(isIncognito) },
            onCloseAllTabs = { isIncognito -> viewModel.closeAllTabs(isIncognito) },
            onIncognitoTabToggled = { viewModel.setTabSwitcherIncognito(it) },
            onDismiss = { viewModel.toggleTabSwitcher() }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ChromeOmnibox(
                            tab = activeTab,
                            tabCount = tabs.size,
                            dancingLights = dancingLightsEnabled,
                            strings = strings,
                            onNavigate = { viewModel.navigateTo(it) },
                            onTabSwitcherClick = { viewModel.toggleTabSwitcher() },
                            onMenuClick = { isMenuExpanded = true }
                        )

                        // Chrome 3-dot overflow menu
                        ChromeMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false },
                            tab = activeTab,
                            isBookmarked = isCurrentBookmarked,
                            strings = strings,
                            onNewTab = { viewModel.openNewTab(isIncognito = false) },
                            onNewIncognitoTab = { viewModel.openNewTab(isIncognito = true) },
                            onBookmarkToggle = { viewModel.toggleBookmarkCurrentPage() },
                            onForward = { viewModel.goForward() },
                            onReload = { viewModel.reload() },
                            onHistoryClick = { viewModel.setHistoryOpen(true) },
                            onBookmarksClick = { viewModel.setBookmarksOpen(true) },
                            onDownloadsClick = { viewModel.setDownloadsOpen(true) },
                            onShareClick = {
                                if (activeTab.url.isNotBlank() && !activeTab.isNewTabPage) {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, activeTab.title)
                                        putExtra(Intent.EXTRA_TEXT, activeTab.url)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share URL"))
                                }
                            },
                            onFindInPageClick = { viewModel.openFindInPage() },
                            onDesktopSiteToggle = { viewModel.toggleDesktopSite() },
                            onSettingsClick = { viewModel.setSettingsOpen(true) }
                        )
                    }

                    // Find in Page inline bar
                    AnimatedVisibility(visible = isFindInPageVisible) {
                        FindInPageBar(
                            query = findQuery,
                            matchCount = findMatchCount,
                            onQueryChange = { viewModel.updateFindQuery(it) },
                            onPrevious = { viewModel.findNext(false) },
                            onNext = { viewModel.findNext(true) },
                            onClose = { viewModel.closeFindInPage() }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (activeTab.isNewTabPage) {
                    NewTabPage(
                        isIncognito = activeTab.isIncognito,
                        shortcuts = viewModel.defaultShortcuts,
                        dancingLights = dancingLightsEnabled,
                        wallpaperResId = wallpaperResId,
                        onNextWallpaper = { viewModel.nextWallpaper() },
                        strings = strings,
                        onNavigate = { viewModel.navigateTo(it) },
                        onOpenBookmarks = { viewModel.setBookmarksOpen(true) },
                        onOpenHistory = { viewModel.setHistoryOpen(true) },
                        onOpenDownloads = { viewModel.setDownloadsOpen(true) }
                    )
                } else {
                    BrowserWebView(
                        tab = activeTab,
                        javascriptEnabled = javascriptEnabled,
                        browserActionFlow = viewModel.browserAction,
                        onPageStarted = { viewModel.onPageStarted(it) },
                        onPageFinished = { url, title -> viewModel.onPageFinished(url, title) },
                        onProgressChanged = { viewModel.onProgressChanged(it) },
                        onNavigationStateChanged = { canBack, canForward ->
                            viewModel.onNavigationStateChanged(canBack, canForward)
                        },
                        onFindResult = { current, total -> viewModel.setFindResult(current, total) },
                        onDownloadRequested = { name, url, path, size ->
                            viewModel.recordDownload(name, url, path, size)
                        }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheets and Dialogs
    if (isBookmarksOpen) {
        BookmarksSheet(
            bookmarks = bookmarks,
            onSelectBookmark = { viewModel.navigateTo(it) },
            onDeleteBookmark = { viewModel.deleteBookmark(it) },
            onDismiss = { viewModel.setBookmarksOpen(false) }
        )
    }

    if (isHistoryOpen) {
        HistorySheet(
            historyList = history,
            onSelectUrl = { viewModel.navigateTo(it) },
            onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
            onClearAllHistory = { viewModel.setClearDataDialogOpen(true) },
            onDismiss = { viewModel.setHistoryOpen(false) }
        )
    }

    if (isDownloadsOpen) {
        DownloadsSheet(
            downloads = downloads,
            onDeleteDownload = { viewModel.deleteDownload(it) },
            onClearAllDownloads = { viewModel.clearAllDownloads() },
            onDismiss = { viewModel.setDownloadsOpen(false) }
        )
    }

    if (isSettingsOpen) {
        SettingsSheet(
            searchEngine = searchEngine,
            onSearchEngineChange = { viewModel.setSearchEngine(it) },
            javascriptEnabled = javascriptEnabled,
            onJavascriptToggle = { viewModel.setJavascriptEnabled(it) },
            colorTheme = colorTheme,
            onColorThemeChange = { viewModel.setColorTheme(it) },
            currentLanguage = appLanguage,
            onLanguageChange = { viewModel.setAppLanguage(it) },
            dancingLightsEnabled = dancingLightsEnabled,
            onDancingLightsToggle = { viewModel.setDancingLightsEnabled(it) },
            strings = strings,
            onOpenClearData = { viewModel.setClearDataDialogOpen(true) },
            onDismiss = { viewModel.setSettingsOpen(false) }
        )
    }

    if (isClearDataDialogOpen) {
        ClearBrowsingDataDialog(
            onConfirm = { clearHistory, clearCookies ->
                viewModel.clearBrowsingData(clearHistory, clearCookies)
            },
            onDismiss = { viewModel.setClearDataDialogOpen(false) }
        )
    }
}
