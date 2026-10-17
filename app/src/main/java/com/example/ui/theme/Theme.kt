package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlueLight,
    onPrimaryContainer = PrimaryBlueDark,
    secondary = SecondaryPurple,
    onSecondary = Color.White,
    tertiary = TertiaryAmber,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = TextDark,
    surface = SurfaceCard,
    onSurface = TextDark
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = Color.Black,
    secondary = SecondaryPurple,
    onSecondary = Color.White,
    tertiary = AmberGlow,
    onTertiary = Color.Black,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC)
)

@Composable
fun StarLearnerTheme(
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
