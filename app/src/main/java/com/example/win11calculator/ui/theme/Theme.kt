package com.example.win11calculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val Win11LightColorScheme = lightColorScheme(
    primary = Win11Accent,
    onPrimary = Win11TextOnAccent,
    primaryContainer = Win11Accent,
    onPrimaryContainer = Win11TextOnAccent,
    background = Win11WindowBg,
    onBackground = Win11TextPrimary,
    surface = Win11Surface,
    onSurface = Win11TextPrimary,
    surfaceVariant = Win11SurfaceVariant,
    onSurfaceVariant = Win11TextSecondary,
    outline = Win11Border
)

@Composable
fun Win11CalculatorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = Win11LightColorScheme,
        typography = Typography,
        content = content
    )
}
