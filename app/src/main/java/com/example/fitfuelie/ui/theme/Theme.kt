package com.example.fitfuelie.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Theme.kt
 * 
 * This file defines the app's color scheme and theme.
 * Material Design 3 uses a color system with semantic names like "primary" and "secondary".
 * 
 * Color roles:
 * - primary: Main brand color (I use green for fitness/health)
 * - secondary: Accent color (I use blue)
 * - tertiary: Another accent color (I use orange)
 * - error: For error messages and warnings (red)
 * - background: Main background color
 * - surface: Color for cards and surfaces
 * - onPrimary/onSecondary/etc.: Text color that goes ON TOP of primary/secondary/etc.
 * 
 * I define two color schemes:
 * 1. Light theme - for when the user's phone is in light mode
 * 2. Dark theme - for when the user's phone is in dark mode
 * 
 * The app automatically switches between them based on system settings!
 */

/**
 * Light color scheme - used when the phone is in light mode
 * Colors are brighter and have more contrast
 */
private val FitFuelLightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF2196F3),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFBBDEFB),
    onSecondaryContainer = Color(0xFF0D47A1),
    tertiary = Color(0xFFFF9800),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFFE65100),
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

/**
 * Dark color scheme - used when the phone is in dark mode
 * Colors are darker and easier on the eyes in low light
 */
private val FitFuelDarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF1B5E20),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFF64B5F6),
    onSecondary = Color(0xFF0D47A1),
    secondaryContainer = Color(0xFF1976D2),
    onSecondaryContainer = Color(0xFFBBDEFB),
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFFE65100),
    tertiaryContainer = Color(0xFFEF6C00),
    onTertiaryContainer = Color(0xFFFFE0B2),
    error = Color(0xFFEF5350),
    onError = Color(0xFFB71C1C),
    errorContainer = Color(0xFFD32F2F),
    onErrorContainer = Color(0xFFFFCDD2),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99)
)

/**
 * FitFuelIETheme
 * 
 * This is the main theme composable that wraps my entire app.
 * It provides colors and typography to all child composables.
 * 
 * @param darkTheme Whether to use dark mode (defaults to system setting)
 * @param dynamicColor Whether to use Android 12+ dynamic colors (matches wallpaper)
 * @param content The UI content that will use this theme
 * 
 *
 */
@Composable
fun FitFuelIETheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // Automatically detects if phone is in dark mode
    dynamicColor: Boolean = false,  // Set to true to enable dynamic colors (matches wallpaper)
    content: @Composable () -> Unit  // All the UI content that uses this theme
) {
    // Pick which color scheme to use
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // Use  colors that match the wallpaper
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Otherwise use  custom color schemes
        darkTheme -> FitFuelDarkColorScheme
        else -> FitFuelLightColorScheme
    }

    // Apply the theme to all child composables
    MaterialTheme(
        colorScheme = colorScheme,  //  colors
        typography = Typography,     //  text styles
        content = content            // The UI that will use this theme
    )
}