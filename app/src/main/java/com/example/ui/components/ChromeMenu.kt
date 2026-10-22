package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.model.BrowserTab
import com.example.ui.theme.ChromeYellow
import com.example.ui.theme.LocalizedStrings
import com.example.ui.theme.StringsFa

@Composable
fun ChromeMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    tab: BrowserTab,
    isBookmarked: Boolean,
    strings: LocalizedStrings = StringsFa,
    onNewTab: () -> Unit,
    onNewIncognitoTab: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onForward: () -> Unit,
    onReload: () -> Unit,
    onHistoryClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    onShareClick: () -> Unit,
    onFindInPageClick: () -> Unit,
    onDesktopSiteToggle: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .width(260.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
        properties = PopupProperties(focusable = true)
    ) {
        // Quick Action Row at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onForward()
                    onDismissRequest()
                },
                enabled = tab.canGoForward
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (tab.canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            IconButton(
                onClick = {
                    onBookmarkToggle()
                    onDismissRequest()
                }
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = strings.bookmarks,
                    tint = if (isBookmarked) ChromeYellow else MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = {
                    onDownloadsClick()
                    onDismissRequest()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = strings.downloads,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = {
                    onReload()
                    onDismissRequest()
                }
            ) {
                Icon(
                    imageVector = if (tab.isLoading) Icons.Default.Close else Icons.Default.Refresh,
                    contentDescription = "Reload",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        ChromeMenuItem(
            icon = Icons.Default.Add,
            text = strings.newTab,
            onClick = {
                onNewTab()
                onDismissRequest()
            }
        )

        ChromeMenuItem(
            icon = Icons.Outlined.VisibilityOff,
            text = strings.newIncognitoTab,
            onClick = {
                onNewIncognitoTab()
                onDismissRequest()
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        ChromeMenuItem(
            icon = Icons.Default.History,
            text = strings.history,
            onClick = {
                onHistoryClick()
                onDismissRequest()
            }
        )

        ChromeMenuItem(
            icon = Icons.Default.Bookmark,
            text = strings.bookmarks,
            onClick = {
                onBookmarksClick()
                onDismissRequest()
            }
        )

        ChromeMenuItem(
            icon = Icons.Default.Download,
            text = strings.downloads,
            onClick = {
                onDownloadsClick()
                onDismissRequest()
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        ChromeMenuItem(
            icon = Icons.Default.Share,
            text = strings.share,
            onClick = {
                onShareClick()
                onDismissRequest()
            }
        )

        ChromeMenuItem(
            icon = Icons.Default.FindInPage,
            text = strings.findInPage,
            onClick = {
                onFindInPageClick()
                onDismissRequest()
            }
        )

        // Desktop site toggle with checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onDesktopSiteToggle()
                    onDismissRequest()
                }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Computer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = strings.desktopSite,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = tab.isDesktopSite,
                onCheckedChange = {
                    onDesktopSiteToggle()
                    onDismissRequest()
                }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        ChromeMenuItem(
            icon = Icons.Default.Settings,
            text = strings.settings,
            onClick = {
                onSettingsClick()
                onDismissRequest()
            }
        )
    }
}

@Composable
private fun ChromeMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        onClick = onClick
    )
}
