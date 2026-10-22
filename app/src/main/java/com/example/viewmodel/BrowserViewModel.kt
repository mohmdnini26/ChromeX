package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.URLUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BookmarkEntity
import com.example.data.BrowserDatabase
import com.example.data.DownloadEntity
import com.example.data.HistoryEntity
import com.example.model.BrowserTab
import com.example.model.SearchEngine
import com.example.model.WebShortcut
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class BrowserAction {
    data class LoadUrl(val url: String) : BrowserAction()
    object GoBack : BrowserAction()
    object GoForward : BrowserAction()
    object Reload : BrowserAction()
    object StopLoading : BrowserAction()
    data class FindInPage(val query: String) : BrowserAction()
    data class FindNext(val forward: Boolean) : BrowserAction()
    object ClearMatches : BrowserAction()
    object ClearCacheAndCookies : BrowserAction()
}

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BrowserDatabase.getInstance(application)
    private val dao = db.browserDao()

    // Database Flows
    val bookmarks = dao.getAllBookmarks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val history = dao.getAllHistory().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val downloads = dao.getAllDownloads().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Tabs state
    private val _tabs = MutableStateFlow<List<BrowserTab>>(listOf(BrowserTab(id = "initial-tab", isIncognito = false)))
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String>("initial-tab")
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    // Tab switcher visibility and sub-tab selection (normal vs incognito)
    private val _isTabSwitcherOpen = MutableStateFlow(false)
    val isTabSwitcherOpen: StateFlow<Boolean> = _isTabSwitcherOpen.asStateFlow()

    private val _tabSwitcherIncognitoSelected = MutableStateFlow(false)
    val tabSwitcherIncognitoSelected: StateFlow<Boolean> = _tabSwitcherIncognitoSelected.asStateFlow()

    // Find in Page state
    private val _isFindInPageVisible = MutableStateFlow(false)
    val isFindInPageVisible: StateFlow<Boolean> = _isFindInPageVisible.asStateFlow()

    private val _findQuery = MutableStateFlow("")
    val findQuery: StateFlow<String> = _findQuery.asStateFlow()

    private val _findMatchCount = MutableStateFlow(Pair(0, 0)) // current, total
    val findMatchCount: StateFlow<Pair<Int, Int>> = _findMatchCount.asStateFlow()

    // Dialogs
    private val _isHistoryOpen = MutableStateFlow(false)
    val isHistoryOpen: StateFlow<Boolean> = _isHistoryOpen.asStateFlow()

    private val _isBookmarksOpen = MutableStateFlow(false)
    val isBookmarksOpen: StateFlow<Boolean> = _isBookmarksOpen.asStateFlow()

    private val _isDownloadsOpen = MutableStateFlow(false)
    val isDownloadsOpen: StateFlow<Boolean> = _isDownloadsOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isClearDataDialogOpen = MutableStateFlow(false)
    val isClearDataDialogOpen: StateFlow<Boolean> = _isClearDataDialogOpen.asStateFlow()

    // Search Engine
    private val _searchEngine = MutableStateFlow(SearchEngine.GOOGLE)
    val searchEngine: StateFlow<SearchEngine> = _searchEngine.asStateFlow()

    // Settings preferences
    private val _javascriptEnabled = MutableStateFlow(true)
    val javascriptEnabled: StateFlow<Boolean> = _javascriptEnabled.asStateFlow()

    // Color Theme, Language, and Dancing Lights preferences
    private val _colorTheme = MutableStateFlow(com.example.ui.theme.AppColorTheme.CHROME_BLUE)
    val colorTheme: StateFlow<com.example.ui.theme.AppColorTheme> = _colorTheme.asStateFlow()

    private val _appLanguage = MutableStateFlow(com.example.ui.theme.AppLanguage.PERSIAN)
    val appLanguage: StateFlow<com.example.ui.theme.AppLanguage> = _appLanguage.asStateFlow()

    private val _dancingLightsEnabled = MutableStateFlow(true)
    val dancingLightsEnabled: StateFlow<Boolean> = _dancingLightsEnabled.asStateFlow()

    // Dynamic Wallpapers for New Tab page
    val wallpapers = listOf(
        com.example.R.drawable.img_wallpaper_galaxy,
        com.example.R.drawable.img_wallpaper_nature,
        com.example.R.drawable.img_wallpaper_neon,
        com.example.R.drawable.img_wallpaper_aurora
    )

    private val _currentWallpaperIndex = MutableStateFlow(0)
    val currentWallpaperIndex: StateFlow<Int> = _currentWallpaperIndex.asStateFlow()

    private val _dynamicWallpaperEnabled = MutableStateFlow(true)
    val dynamicWallpaperEnabled: StateFlow<Boolean> = _dynamicWallpaperEnabled.asStateFlow()

    fun nextWallpaper() {
        if (_dynamicWallpaperEnabled.value && wallpapers.isNotEmpty()) {
            _currentWallpaperIndex.value = (_currentWallpaperIndex.value + 1) % wallpapers.size
        }
    }

    fun setDynamicWallpaperEnabled(enabled: Boolean) {
        _dynamicWallpaperEnabled.value = enabled
    }

    // Current page bookmarked state
    private val _isCurrentBookmarked = MutableStateFlow(false)
    val isCurrentBookmarked: StateFlow<Boolean> = _isCurrentBookmarked.asStateFlow()

    // Shared events to trigger webview operations
    private val _browserAction = MutableSharedFlow<BrowserAction>()
    val browserAction: SharedFlow<BrowserAction> = _browserAction.asSharedFlow()

    // Default quick shortcuts for New Tab Page
    val defaultShortcuts = listOf(
        WebShortcut("Google", "https://www.google.com", "G", 0xFF4285F4),
        WebShortcut("YouTube", "https://www.youtube.com", "Y", 0xFFFF0000),
        WebShortcut("Wikipedia", "https://fa.wikipedia.org", "W", 0xFF54595D),
        WebShortcut("GitHub", "https://github.com", "GH", 0xFF24292E),
        WebShortcut("BBC News", "https://www.bbc.com/persian", "B", 0xFFBB1919),
        WebShortcut("Reddit", "https://www.reddit.com", "R", 0xFFFF4500),
        WebShortcut("Digikala", "https://www.digikala.com", "D", 0xFFEF394E),
        WebShortcut("Varzesh3", "https://www.varzesh3.com", "V", 0xFF009688)
    )

    fun getActiveTab(): BrowserTab? {
        val currentId = _activeTabId.value
        return _tabs.value.firstOrNull { it.id == currentId } ?: _tabs.value.firstOrNull()
    }

    fun openNewTab(isIncognito: Boolean = false, url: String = "chrome://newtab") {
        nextWallpaper()
        val newTab = BrowserTab(
            title = if (url == "chrome://newtab") "برگه جدید" else "در حال بارگذاری...",
            url = url,
            displayUrl = if (url == "chrome://newtab") "" else url,
            isIncognito = isIncognito
        )
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newTab.id
        _isTabSwitcherOpen.value = false
        _tabSwitcherIncognitoSelected.value = isIncognito
        checkIfCurrentBookmarked(url)
    }

    fun selectTab(tabId: String) {
        nextWallpaper()
        val tab = _tabs.value.firstOrNull { it.id == tabId }
        if (tab != null) {
            _activeTabId.value = tabId
            _isTabSwitcherOpen.value = false
            _tabSwitcherIncognitoSelected.value = tab.isIncognito
            checkIfCurrentBookmarked(tab.url)
        }
    }

    fun closeTab(tabId: String) {
        val currentTabs = _tabs.value
        if (currentTabs.size <= 1) {
            // Keep at least one tab open
            val isIncognito = currentTabs.firstOrNull()?.isIncognito ?: false
            val freshTab = BrowserTab(isIncognito = isIncognito)
            _tabs.value = listOf(freshTab)
            _activeTabId.value = freshTab.id
            return
        }

        val closingIndex = currentTabs.indexOfFirst { it.id == tabId }
        val updatedTabs = currentTabs.filter { it.id != tabId }
        _tabs.value = updatedTabs

        if (_activeTabId.value == tabId) {
            val newActiveIndex = if (closingIndex >= updatedTabs.size) updatedTabs.size - 1 else closingIndex
            _activeTabId.value = updatedTabs[newActiveIndex].id
            checkIfCurrentBookmarked(updatedTabs[newActiveIndex].url)
        }
    }

    fun closeAllTabs(isIncognitoOnly: Boolean) {
        val remaining = _tabs.value.filter { it.isIncognito != isIncognitoOnly }
        if (remaining.isEmpty()) {
            val freshTab = BrowserTab(isIncognito = false)
            _tabs.value = listOf(freshTab)
            _activeTabId.value = freshTab.id
        } else {
            _tabs.value = remaining
            if (_tabs.value.none { it.id == _activeTabId.value }) {
                _activeTabId.value = remaining.first().id
            }
        }
        _isTabSwitcherOpen.value = false
    }

    fun toggleTabSwitcher() {
        val willOpen = !_isTabSwitcherOpen.value
        _isTabSwitcherOpen.value = willOpen
        if (willOpen) {
            // Set tab switcher page according to current active tab
            val current = getActiveTab()
            _tabSwitcherIncognitoSelected.value = current?.isIncognito ?: false
        }
    }

    fun setTabSwitcherIncognito(incognito: Boolean) {
        _tabSwitcherIncognitoSelected.value = incognito
    }

    fun navigateTo(input: String) {
        nextWallpaper()
        val normalized = normalizeUrl(input)
        val active = getActiveTab() ?: return

        updateActiveTab { it.copy(url = normalized, displayUrl = normalized, isLoading = true, progress = 10) }
        viewModelScope.launch {
            _browserAction.emit(BrowserAction.LoadUrl(normalized))
        }
        checkIfCurrentBookmarked(normalized)
    }

    private fun normalizeUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "chrome://newtab"
        if (trimmed == "chrome://newtab" || trimmed == "about:blank") return trimmed

        // Check if input is a valid URL or web domain
        val isUrl = URLUtil.isValidUrl(trimmed) ||
                (trimmed.contains(".") && !trimmed.contains(" ") && trimmed.indexOf(".") > 0 && trimmed.indexOf(".") < trimmed.length - 1)

        return if (isUrl) {
            if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                "https://$trimmed"
            } else {
                trimmed
            }
        } else {
            // Search query
            val encodedQuery = URLEncoder.encode(trimmed, StandardCharsets.UTF_8.toString())
            "${_searchEngine.value.searchUrl}$encodedQuery"
        }
    }

    fun updateActiveTab(transform: (BrowserTab) -> BrowserTab) {
        val currentId = _activeTabId.value
        _tabs.value = _tabs.value.map {
            if (it.id == currentId) transform(it) else it
        }
    }

    fun onPageStarted(url: String) {
        val isSecure = url.startsWith("https://")
        updateActiveTab {
            it.copy(
                url = url,
                displayUrl = url,
                isLoading = true,
                progress = 15,
                isSecure = isSecure
            )
        }
        checkIfCurrentBookmarked(url)
    }

    fun onPageFinished(url: String, title: String?) {
        val tab = getActiveTab()
        val finalTitle = if (!title.isNullOrBlank() && title != "about:blank") title else tab?.title ?: "وب"
        updateActiveTab {
            it.copy(
                url = url,
                displayUrl = url,
                title = finalTitle,
                isLoading = false,
                progress = 100
            )
        }

        // Record history only if NOT incognito and not newtab/blank
        if (tab != null && !tab.isIncognito && url != "about:blank" && url != "chrome://newtab") {
            viewModelScope.launch {
                dao.insertHistory(
                    HistoryEntity(
                        title = finalTitle,
                        url = url,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
        checkIfCurrentBookmarked(url)
    }

    fun onProgressChanged(progress: Int) {
        updateActiveTab {
            it.copy(
                progress = progress,
                isLoading = progress < 100
            )
        }
    }

    fun onNavigationStateChanged(canGoBack: Boolean, canGoForward: Boolean) {
        updateActiveTab {
            it.copy(canGoBack = canGoBack, canGoForward = canGoForward)
        }
    }

    fun toggleDesktopSite() {
        val tab = getActiveTab() ?: return
        val newMode = !tab.isDesktopSite
        updateActiveTab { it.copy(isDesktopSite = newMode) }
        viewModelScope.launch {
            _browserAction.emit(BrowserAction.Reload)
        }
    }

    fun reload() {
        viewModelScope.launch { _browserAction.emit(BrowserAction.Reload) }
    }

    fun goBack() {
        viewModelScope.launch { _browserAction.emit(BrowserAction.GoBack) }
    }

    fun goForward() {
        viewModelScope.launch { _browserAction.emit(BrowserAction.GoForward) }
    }

    fun stopLoading() {
        viewModelScope.launch { _browserAction.emit(BrowserAction.StopLoading) }
        updateActiveTab { it.copy(isLoading = false, progress = 0) }
    }

    // Bookmarks
    private fun checkIfCurrentBookmarked(url: String) {
        viewModelScope.launch {
            val exists = dao.getBookmarkByUrl(url)
            _isCurrentBookmarked.value = exists != null
        }
    }

    fun toggleBookmarkCurrentPage() {
        val tab = getActiveTab() ?: return
        if (tab.isNewTabPage) return

        viewModelScope.launch {
            val existing = dao.getBookmarkByUrl(tab.url)
            if (existing != null) {
                dao.deleteBookmark(existing)
                _isCurrentBookmarked.value = false
            } else {
                dao.insertBookmark(
                    BookmarkEntity(
                        title = tab.title.ifBlank { tab.url },
                        url = tab.url,
                        timestamp = System.currentTimeMillis()
                    )
                )
                _isCurrentBookmarked.value = true
            }
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            dao.deleteBookmark(bookmark)
            val current = getActiveTab()
            if (current?.url == bookmark.url) {
                _isCurrentBookmarked.value = false
            }
        }
    }

    // History
    fun deleteHistoryItem(historyItem: HistoryEntity) {
        viewModelScope.launch {
            dao.deleteHistory(historyItem)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAllHistory()
        }
    }

    // Downloads
    fun recordDownload(fileName: String, url: String, filePath: String, fileSize: String) {
        viewModelScope.launch {
            dao.insertDownload(
                DownloadEntity(
                    fileName = fileName,
                    url = url,
                    filePath = filePath,
                    fileSize = fileSize,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteDownload(download: DownloadEntity) {
        viewModelScope.launch {
            dao.deleteDownload(download)
        }
    }

    fun clearAllDownloads() {
        viewModelScope.launch {
            dao.clearAllDownloads()
        }
    }

    // Find In Page
    fun openFindInPage() {
        _isFindInPageVisible.value = true
    }

    fun closeFindInPage() {
        _isFindInPageVisible.value = false
        _findQuery.value = ""
        _findMatchCount.value = Pair(0, 0)
        viewModelScope.launch {
            _browserAction.emit(BrowserAction.ClearMatches)
        }
    }

    fun updateFindQuery(query: String) {
        _findQuery.value = query
        viewModelScope.launch {
            _browserAction.emit(BrowserAction.FindInPage(query))
        }
    }

    fun findNext(forward: Boolean) {
        viewModelScope.launch {
            _browserAction.emit(BrowserAction.FindNext(forward))
        }
    }

    fun setFindResult(current: Int, total: Int) {
        _findMatchCount.value = Pair(current, total)
    }

    // Dialog state controllers
    fun setHistoryOpen(open: Boolean) { _isHistoryOpen.value = open }
    fun setBookmarksOpen(open: Boolean) { _isBookmarksOpen.value = open }
    fun setDownloadsOpen(open: Boolean) { _isDownloadsOpen.value = open }
    fun setSettingsOpen(open: Boolean) { _isSettingsOpen.value = open }
    fun setClearDataDialogOpen(open: Boolean) { _isClearDataDialogOpen.value = open }

    fun setSearchEngine(engine: SearchEngine) {
        _searchEngine.value = engine
    }

    fun setJavascriptEnabled(enabled: Boolean) {
        _javascriptEnabled.value = enabled
    }

    fun clearBrowsingData(clearHistory: Boolean, clearCookiesCache: Boolean) {
        viewModelScope.launch {
            if (clearHistory) {
                dao.clearAllHistory()
            }
            if (clearCookiesCache) {
                _browserAction.emit(BrowserAction.ClearCacheAndCookies)
            }
        }
        _isClearDataDialogOpen.value = false
    }

    fun setColorTheme(theme: com.example.ui.theme.AppColorTheme) {
        _colorTheme.value = theme
    }

    fun setAppLanguage(language: com.example.ui.theme.AppLanguage) {
        _appLanguage.value = language
    }

    fun setDancingLightsEnabled(enabled: Boolean) {
        _dancingLightsEnabled.value = enabled
    }
}
