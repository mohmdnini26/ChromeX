package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WebShortcut
import com.example.ui.theme.ChromeBlue
import com.example.ui.theme.ChromeGreen
import com.example.ui.theme.ChromeRed
import com.example.ui.theme.ChromeYellow
import com.example.ui.theme.LocalizedStrings
import com.example.ui.theme.StringsFa

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewTabPage(
    isIncognito: Boolean,
    shortcuts: List<WebShortcut>,
    dancingLights: Boolean = true,
    wallpaperResId: Int? = null,
    onNextWallpaper: () -> Unit = {},
    strings: LocalizedStrings = StringsFa,
    onNavigate: (String) -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Dynamic Wallpaper Background with smooth crossfade
        if (!isIncognito && wallpaperResId != null) {
            Crossfade(targetState = wallpaperResId, label = "wallpaper_crossfade") { resId ->
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "تصویر پس‌زمینه",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Scrim overlay for high contrast and readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.52f),
                                Color.Black.copy(alpha = 0.38f),
                                Color.Black.copy(alpha = 0.72f)
                            )
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isIncognito) {
                // Incognito Welcome Page
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF38383D)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = strings.incognito,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = strings.youHaveGoneIncognito,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = strings.incognitoDescription,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.incognitoDoesNotSave,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = strings.incognitoBullets1,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Search bar for incognito
                IncognitoSearchBar(
                    value = searchInput,
                    placeholderText = strings.searchOrTypeUrl,
                    dancingLights = dancingLights,
                    onValueChange = { searchInput = it },
                    onSearch = {
                        if (searchInput.isNotBlank()) {
                            onNavigate(searchInput)
                            focusManager.clearFocus()
                        }
                    }
                )
            } else {
                // Top bar in New Tab Page: Wallpaper change chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        onClick = onNextWallpaper,
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.45f),
                        contentColor = Color.White,
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wallpaper,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تغییر تصویر",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stylized Google logo
                GoogleLogoText()

                Spacer(modifier = Modifier.height(26.dp))

                // Central Search Input with Dancing Lights
                ChromeCenterSearchBar(
                    value = searchInput,
                    placeholderText = strings.searchOrTypeUrl,
                    dancingLights = dancingLights,
                    onValueChange = { searchInput = it },
                    onSearch = {
                        if (searchInput.isNotBlank()) {
                            onNavigate(searchInput)
                            focusManager.clearFocus()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Shortcuts grid
                Text(
                    text = strings.topSites,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (wallpaperResId != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    maxItemsInEachRow = 4
                ) {
                    shortcuts.forEach { shortcut ->
                        ShortcutItem(
                            shortcut = shortcut,
                            dancingLights = dancingLights,
                            hasWallpaper = wallpaperResId != null,
                            onClick = { onNavigate(shortcut.url) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Quick Navigation Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickChip(
                        icon = Icons.Default.Bookmark,
                        label = strings.bookmarks,
                        onClick = onOpenBookmarks,
                        modifier = Modifier.weight(1f)
                    )
                    QuickChip(
                        icon = Icons.Default.History,
                        label = strings.history,
                        onClick = onOpenHistory,
                        modifier = Modifier.weight(1f)
                    )
                    QuickChip(
                        icon = Icons.Default.Download,
                        label = strings.downloads,
                        onClick = onOpenDownloads,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun GoogleLogoText() {
    val styledText = buildAnnotatedString {
        withStyle(SpanStyle(color = ChromeBlue)) { append("G") }
        withStyle(SpanStyle(color = ChromeRed)) { append("o") }
        withStyle(SpanStyle(color = ChromeYellow)) { append("o") }
        withStyle(SpanStyle(color = ChromeBlue)) { append("g") }
        withStyle(SpanStyle(color = ChromeGreen)) { append("l") }
        withStyle(SpanStyle(color = ChromeRed)) { append("e") }
    }

    Text(
        text = styledText,
        fontSize = 44.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
    )
}

@Composable
fun ChromeCenterSearchBar(
    value: String,
    placeholderText: String,
    dancingLights: Boolean,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val searchShape = RoundedCornerShape(26.dp)
    val dancingBorder = if (dancingLights) {
        Modifier.rgbDancingBorder(shape = searchShape, borderWidth = 3.dp)
    } else {
        Modifier
    }

    ElevatedCard(
        shape = searchShape,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(dancingBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholderText,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("home_search_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() })
            )

            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Search",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun IncognitoSearchBar(
    value: String,
    placeholderText: String,
    dancingLights: Boolean,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val searchShape = RoundedCornerShape(26.dp)
    val dancingBorder = if (dancingLights) {
        Modifier.rgbDancingBorder(shape = searchShape, borderWidth = 3.dp)
    } else {
        Modifier
    }

    ElevatedCard(
        shape = searchShape,
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF38383D)),
        modifier = Modifier
            .fillMaxWidth()
            .then(dancingBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholderText,
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("incognito_search_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() })
            )
        }
    }
}

@Composable
fun ShortcutItem(
    shortcut: WebShortcut,
    dancingLights: Boolean,
    hasWallpaper: Boolean = false,
    onClick: () -> Unit
) {
    val circleShape = CircleShape
    val dancingBorder = if (dancingLights) {
        Modifier.rgbDancingBorder(shape = circleShape, borderWidth = 3.dp, durationMs = 2400)
    } else {
        Modifier
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .then(dancingBorder)
                .clip(circleShape)
                .background(Color(shortcut.colorHex)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = shortcut.iconLetter,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = shortcut.title,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = if (hasWallpaper) Color.White else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuickChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        modifier = modifier.height(44.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
