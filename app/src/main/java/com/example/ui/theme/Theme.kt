package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DeepNavy,
    onPrimary = Color.White,
    primaryContainer = NavyLight,
    onPrimaryContainer = Color.White,
    secondary = RoyalPurple,
    onSecondary = Color.White,
    secondaryContainer = PurpleLight,
    onSecondaryContainer = RoyalPurple,
    tertiary = GoldAccent,
    onTertiary = TextDarkNavy,
    tertiaryContainer = GoldLight,
    onTertiaryContainer = TextDarkNavy,
    background = BackgroundLight,
    onBackground = TextDarkNavy,
    surface = SurfaceWhite,
    onSurface = TextDarkNavy,
    surfaceVariant = CardLightGray,
    onSurfaceVariant = TextSecondarySlate,
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = DeepNavy,
    primaryContainer = NavyLight,
    onPrimaryContainer = Color.White,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    tertiary = GoldLight,
    background = DeepNavy,
    onBackground = Color.White,
    surface = NavyLight,
    onSurface = Color.White,
    surfaceVariant = NavySlate,
    onSurfaceVariant = Color.White.copy(alpha = 0.8f),
    error = ErrorRed,
    onError = Color.White
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
