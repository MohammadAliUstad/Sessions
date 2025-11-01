package com.yugentech.sessions.theme.utils

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.yugentech.sessions.theme.color.getColorScheme
import com.yugentech.sessions.theme.tokens.ProvideDesignTokens
import com.yugentech.sessions.theme.tokens.TypographyCompact
import com.yugentech.sessions.theme.tokens.TypographyExpanded
import com.yugentech.sessions.theme.tokens.TypographyMedium

/**
 * Main theme for Sessions app
 *
 * Automatically adapts:
 * - Typography (responsive text sizes)
 * - Design tokens (spacing, corners, icons, etc.)
 * - Color scheme (based on theme configuration)
 *
 * All based on screen size!
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SessionsTheme(
    themeConfiguration: ThemeConfiguration,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Get color scheme based on theme settings
    val colorScheme = getColorScheme(themeConfiguration = themeConfiguration)

    // Get responsive typography based on screen size
    val typography = if (activity != null) {
        val windowSizeClass = calculateWindowSizeClass(activity)
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> TypographyCompact
            WindowWidthSizeClass.Medium -> TypographyMedium
            WindowWidthSizeClass.Expanded -> TypographyExpanded
            else -> TypographyCompact
        }
    } else {
        TypographyCompact  // Fallback
    }

    // Provide design tokens (spacing, corners, icons, etc.)
    ProvideDesignTokens {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,  // ✅ Responsive typography!
            content = content
        )
    }
}

/**
 * How it works:
 *
 * Phone (< 600dp):
 * - MaterialTheme.typography.headlineMedium = 24sp
 * - MaterialTheme.spacing.m = 16dp
 * - MaterialTheme.icons.medium = 24dp
 *
 * Tablet Portrait (600-839dp):
 * - MaterialTheme.typography.headlineMedium = 30sp
 * - MaterialTheme.spacing.m = 20dp
 * - MaterialTheme.icons.medium = 28dp
 *
 * Tablet Landscape (840dp+):
 * - MaterialTheme.typography.headlineMedium = 34sp
 * - MaterialTheme.spacing.m = 24dp
 * - MaterialTheme.icons.medium = 32dp
 *
 * Everything scales together! 🎯
 */