package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTab
import com.example.ui.theme.ChromeBlue
import com.example.ui.theme.LocalizedStrings
import com.example.ui.theme.StringsFa
import java.net.URI

@Composable
fun ChromeOmnibox(
    tab: BrowserTab,
    tabCount: Int,
    dancingLights: Boolean = true,
    strings: LocalizedStrings = StringsFa,
    onNavigate: (String) -> Unit,
    onTabSwitcherClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    var textInput by remember(tab.url) {
        val initial = if (tab.isNewTabPage) "" else tab.url
        mutableStateOf(initial)
    }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val animatedProgress by animateFloatAsState(
        targetValue = if (tab.isLoading) (tab.progress / 100f).coerceIn(0.05f, 1f) else 0f,
        label = "progress"
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                // Incognito icon if active
                if (tab.isIncognito) {
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = strings.incognito,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(24.dp)
                    )
                }

                val omniboxShape = RoundedCornerShape(23.dp)
                val borderModifier = if (dancingLights) {
                    Modifier.rgbDancingBorder(shape = omniboxShape, borderWidth = 2.5.dp)
                } else {
                    Modifier
                }

                // Omnibox pill container with RGB dancing light
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .then(borderModifier)
                        .clip(omniboxShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(enabled = !isFocused) {
                            isFocused = true
                        }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // SSL or Search icon
                        val icon = when {
                            tab.isNewTabPage -> Icons.Default.Search
                            tab.isSecure -> Icons.Default.Lock
                            else -> Icons.Outlined.LockOpen
                        }
                        val iconTint = when {
                            tab.isSecure -> Color(0xFF1E8E3E)
                            tab.isNewTabPage -> MaterialTheme.colorScheme.primary
                            else -> Color(0xFFD93025)
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = "SSL Status",
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // URL / Search input
                        Box(modifier = Modifier.weight(1f)) {
                            if (!isFocused && !tab.isNewTabPage) {
                                val host = try {
                                    val uri = URI(tab.url)
                                    val domain = uri.host ?: tab.url
                                    if (domain.startsWith("www.")) domain.substring(4) else domain
                                } catch (_: Exception) {
                                    tab.url
                                }

                                Text(
                                    text = host,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                BasicTextField(
                                    value = textInput,
                                    onValueChange = { textInput = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester)
                                        .onFocusChanged { focusState ->
                                            isFocused = focusState.isFocused
                                            if (focusState.isFocused && tab.isNewTabPage) {
                                                textInput = ""
                                            } else if (focusState.isFocused) {
                                                textInput = tab.url
                                            }
                                        }
                                        .testTag("omnibox_input"),
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 15.sp
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Uri,
                                        imeAction = ImeAction.Go
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onGo = {
                                            if (textInput.isNotBlank()) {
                                                onNavigate(textInput)
                                            }
                                            focusManager.clearFocus()
                                            isFocused = false
                                        }
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (textInput.isEmpty() && isFocused) {
                                            Text(
                                                text = strings.searchOrTypeUrl,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }

                            if (!isFocused && tab.isNewTabPage) {
                                Text(
                                    text = strings.searchOrTypeUrl,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Clear 'X' button
                        if (isFocused && textInput.isNotEmpty()) {
                            IconButton(
                                onClick = { textInput = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = strings.clear,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Tab Switcher Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = onTabSwitcherClick)
                        .testTag("tab_switcher_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$tabCount",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // 3-dots Menu Button
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("chrome_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = strings.settings,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Progress bar
            AnimatedVisibility(visible = tab.isLoading && animatedProgress > 0f) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}
