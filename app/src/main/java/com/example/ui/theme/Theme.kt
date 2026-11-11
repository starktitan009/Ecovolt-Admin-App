package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EcoVoltLightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = MintSurface,
    onPrimaryContainer = ForestGreen,
    secondary = ElectricGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F6ED),
    onSecondaryContainer = ForestGreenDark,
    background = OffWhiteBg,
    onBackground = DeepInk,
    surface = Color.White,
    onSurface = DeepInk,
    surfaceVariant = MintSurface,
    onSurfaceVariant = MutedText,
    outline = SubtleLine,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun EcoVoltTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EcoVoltLightColorScheme,
        typography = EcoVoltTypography,
        content = content
    )
}
