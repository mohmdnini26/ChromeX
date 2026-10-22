package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
    appColorTheme: AppColorTheme = AppColorTheme.CHROME_BLUE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    isIncognito: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isIncognito -> darkColorScheme(
            primary = IncognitoPrimary,
            background = IncognitoBackground,
            surface = IncognitoSurface,
            surfaceVariant = Color(0xFF38383D),
            onBackground = Color(0xFFEDEDED),
            onSurface = Color(0xFFEDEDED),
            onSurfaceVariant = Color(0xFFB0B0B0)
        )
        else -> createColorPalette(appColorTheme, darkTheme || appColorTheme == AppColorTheme.BLACK)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
