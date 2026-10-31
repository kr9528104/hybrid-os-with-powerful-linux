package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AuraPrimary,
    onPrimary = AuraTextPrimary,
    primaryContainer = AuraSurfaceLight,
    onPrimaryContainer = AuraTextPrimary,
    secondary = AuraSecondary,
    onSecondary = AuraTextPrimary,
    secondaryContainer = AuraSurfaceElevated,
    onSecondaryContainer = AuraTextPrimary,
    tertiary = AuraAccent,
    onTertiary = AuraBgDark,
    background = AuraBgDark,
    onBackground = AuraTextPrimary,
    surface = AuraSurfaceDark,
    onSurface = AuraTextPrimary,
    surfaceVariant = AuraSurfaceLight,
    onSurfaceVariant = AuraTextSecondary,
    outline = AuraBorderDark,
    outlineVariant = AuraBorderSubtle,
    error = AuraRose,
    onError = AuraTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark-modern as requested
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = AuraBgDark.toArgb()
                window.navigationBarColor = AuraBgDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
