package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Navy800,
    onPrimary = Color.White,
    primaryContainer = Navy900,
    onPrimaryContainer = Blue300,
    background = DarkCanvas,
    onBackground = Color(0xFFEEF4FF),
    surface = DarkSurface,
    onSurface = Color(0xFFEEF4FF),
    surfaceVariant = Color(0xFF182840),
    onSurfaceVariant = Color(0xFFA9B7CC)
)

private val LightColorScheme = lightColorScheme(
    primary = Navy800,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2EFFF),
    onPrimaryContainer = Navy900,
    background = LightCanvas,
    onBackground = Color(0xFF111827),
    surface = LightSurface,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun MyApplicationTheme(
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
