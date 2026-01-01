package com.example.kelompok_1.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = TextOnPrimary,
    secondary = SecondaryGreen,
    onSecondary = TextOnPrimary,
    secondaryContainer = SecondaryGreen,
    onSecondaryContainer = TextOnPrimary,
    tertiary = SecondaryOrange,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    error = SecondaryRed,
    onError = TextOnPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = TextOnPrimary,
    secondary = SecondaryGreen,
    onSecondary = TextOnPrimary,
    secondaryContainer = SecondaryGreen.copy(alpha = 0.1f),
    onSecondaryContainer = SecondaryGreen,
    tertiary = SecondaryOrange,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondary,
    error = SecondaryRed,
    onError = TextOnPrimary
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PrimaryBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun Kelompok_1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ExpenseTrackerTheme(darkTheme = darkTheme, content = content)
}