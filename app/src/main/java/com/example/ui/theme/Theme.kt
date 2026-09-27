package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IronDarkColorScheme = darkColorScheme(
    primary = NeonLime,
    onPrimary = NeonLimeDark,
    primaryContainer = IronSurfaceElevated,
    onPrimaryContainer = NeonLime,
    secondary = NeonLime,
    onSecondary = NeonLimeDark,
    secondaryContainer = IronSurfaceSubtle,
    onSecondaryContainer = IronTextPrimary,
    tertiary = NeonLime,
    onTertiary = NeonLimeDark,
    error = FormBreakdownOrange,
    onError = Color.White,
    errorContainer = LaserCrimsonBg,
    onErrorContainer = FormBreakdownOrange,
    background = IronDarkBackground,
    onBackground = IronTextPrimary,
    surface = IronSurfaceDark,
    onSurface = IronTextPrimary,
    surfaceVariant = IronSurfaceElevated,
    onSurfaceVariant = IronTextSecondary,
    outline = IronBorder
)

@Composable
fun IronVisionTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IronDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    IronVisionTheme(content = content)
}
