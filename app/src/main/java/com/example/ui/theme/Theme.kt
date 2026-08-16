package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3B1E12),
    onPrimaryContainer = Color(0xFFFFEDD5),
    secondary = OrangeSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF331A0C),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = SapphireBlue,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF263248),
    error = CrimsonAccent,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEDD5),
    onPrimaryContainer = Color(0xFF7C2D12),
    secondary = OrangeSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF7ED),
    onSecondaryContainer = Color(0xFFC2410C),
    tertiary = SapphireBlue,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = Color(0xFFE2E8F0),
    error = CrimsonAccent,
    onError = Color.White
)

@Composable
fun GsmServiceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

