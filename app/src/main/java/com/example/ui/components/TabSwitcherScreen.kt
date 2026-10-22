package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTab
import com.example.ui.theme.ChromeBlue
import com.example.ui.theme.LocalizedStrings
import com.example.ui.theme.StringsFa
import java.net.URI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabSwitcherScreen(
    tabs: List<BrowserTab>,
    activeTabId: String,
    isIncognitoSelected: Boolean,
    strings: LocalizedStrings = StringsFa,
    onTabSelected: (String) -> Unit,
    onTabClosed: (String) -> Unit,
    onNewTabClick: (Boolean) -> Unit,
    onCloseAllTabs: (Boolean) -> Unit,
    onIncognitoTabToggled: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayedTabs = tabs.filter { it.isIncognito == isIncognitoSelected }
    val normalCount = tabs.count { !it.isIncognito }
    val incognitoCount = tabs.count { it.isIncognito }
    var menuExpanded by remember { mutableStateOf(false) }

    val bgColor = if (isIncognitoSelected) Color(0xFF1F1F1F) else MaterialTheme.colorScheme.background

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgColor,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isIncognitoSelected) strings.incognitoTabs else strings.tabs,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isIncognitoSelected) Color(0xFF2A2A2E) else MaterialTheme.colorScheme.surface,
                        titleContentColor = if (isIncognitoSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        // Close all tabs action
                        IconButton(
                            onClick = { onCloseAllTabs(isIncognitoSelected) },
                            modifier = Modifier.testTag("close_all_tabs_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = strings.closeAllTabs,
                                tint = if (isIncognitoSelected) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Close switcher and return to active tab
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = if (isIncognitoSelected) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                // Tabs / Incognito mode switcher bar
                TabRow(
                    selectedTabIndex = if (isIncognitoSelected) 1 else 0,
                    containerColor = if (isIncognitoSelected) Color(0xFF2A2A2E) else MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = !isIncognitoSelected,
                        onClick = { onIncognitoTabToggled(false) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tab,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${strings.regular} ($normalCount)")
                            }
                        }
                    )
                    Tab(
                        selected = isIncognitoSelected,
                        onClick = { onIncognitoTabToggled(true) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.VisibilityOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${strings.incognito} ($incognitoCount)")
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewTabClick(isIncognitoSelected) },
                containerColor = if (isIncognitoSelected) Color(0xFF4A4A52) else ChromeBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("new_tab_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "برگه جدید")
            }
        }
    ) { innerPadding ->
        if (displayedTabs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (isIncognitoSelected) Icons.Outlined.VisibilityOff else Icons.Default.Tab,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isIncognitoSelected) "هیچ برگه ناشناسی باز نیست" else "هیچ برگه‌ای باز نیست",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = innerPadding.calculateTopPadding() + 12.dp,
                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayedTabs, key = { it.id }) { tab ->
                    TabCard(
                        tab = tab,
                        isSelected = tab.id == activeTabId,
                        isIncognito = isIncognitoSelected,
                        onSelect = { onTabSelected(tab.id) },
                        onClose = { onTabClosed(tab.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TabCard(
    tab: BrowserTab,
    isSelected: Boolean,
    isIncognito: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isIncognito) Color(0xFF2C2C30) else MaterialTheme.colorScheme.surface
    val borderModifier = if (isSelected) {
        Modifier.border(2.5.dp, ChromeBlue, RoundedCornerShape(14.dp))
    } else {
        Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .then(borderModifier)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onSelect)
            .testTag("tab_card_${tab.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Card Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isIncognito) Color(0xFF38383E) else MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (tab.isIncognito) Icons.Outlined.VisibilityOff else Icons.Default.Public,
                    contentDescription = null,
                    tint = if (tab.isIncognito) Color.LightGray else ChromeBlue,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = tab.title.ifBlank { "برگه جدید" },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    ),
                    color = if (isIncognito) Color.White else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("close_tab_${tab.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن برگه",
                        tint = if (isIncognito) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Tab Card Content / Preview Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val host = try {
                        val uri = URI(tab.url)
                        uri.host ?: tab.url
                    } catch (_: Exception) {
                        tab.url
                    }

                    if (tab.isNewTabPage) {
                        Text(
                            text = "صفحه برگه جدید",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isIncognito) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = host,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isIncognito) Color.White else MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tab.url,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isIncognito) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
