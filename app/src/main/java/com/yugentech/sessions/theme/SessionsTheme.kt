package com.yugentech.sessions.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalHapticFeedback
import com.yugentech.sessions.theme.builder.getColorScheme
import com.yugentech.sessions.theme.builder.getFontFamily
import com.yugentech.sessions.theme.builder.getTypography
import com.yugentech.sessions.theme.config.ThemeConfiguration
import com.yugentech.sessions.theme.config.ThemeMode
import com.yugentech.sessions.theme.tokens.LocalDesignTokens
import com.yugentech.sessions.theme.tokens.TokensCompact

@Composable
fun SessionsTheme(
    themeConfiguration: ThemeConfiguration,
    hapticsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    // Generate the color scheme based on user settings (Dark/Light, Dynamic, etc.)
    val colorScheme = getColorScheme(themeConfiguration = themeConfiguration)
    // AMOLED only applies in dark mode, so it's showing only when both hold (see SheetTheme)
    val isDark = when (themeConfiguration.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val amoledActive = isDark && themeConfiguration.isAmoledMode

    // Reconstruct typography only when the selected font changes
    val currentTypography = remember(themeConfiguration.appFont) {
        getTypography(getFontFamily(themeConfiguration.appFont))
    }

    // Load the standard design tokens (spacing, sizing, etc.)
    val tokens = TokensCompact

    // Provide the tokens, haptics and theme data to the entire UI tree
    CompositionLocalProvider(
        LocalDesignTokens provides tokens,
        LocalHapticFeedback provides rememberGatedHaptics(hapticsEnabled),
        LocalAmoledActive provides amoledActive
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = currentTypography,
            content = content,
            motionScheme = MotionScheme.expressive()
        )
    }
}