package com.infrabwx.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = White,
    primaryContainer = LightGreen,
    secondary = PrimaryGreen,
    onSecondary = White,
    background = BackgroundWhite,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    error = Color(0xFFD32F2F),
    onError = White
)

@Composable
fun InfraBWXTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
