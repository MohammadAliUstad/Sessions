package com.yugentech.sessions.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.yugentech.sessions.theme.getters.getColorScheme
import com.yugentech.sessions.theme.getters.getTypography
import com.yugentech.sessions.theme.models.ThemeConfiguration
import com.yugentech.sessions.theme.tokens.LocalDesignTokens
import com.yugentech.sessions.theme.tokens.TokensCompact

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SessionsTheme(
    themeConfiguration: ThemeConfiguration,
    content: @Composable () -> Unit
) {
    // Generate the color scheme based on user settings (Dark/Light, Dynamic, etc.)
    val colorScheme = getColorScheme(themeConfiguration = themeConfiguration)

    // Reconstruct typography only when the selected font changes
    val currentTypography = remember(themeConfiguration.appFont) {
        getTypography(themeConfiguration.appFont.toFontFamily())
    }

    // Load the standard design tokens (spacing, sizing, etc.)
    val tokens = TokensCompact

    // Provide the tokens and theme data to the entire UI tree
    CompositionLocalProvider(LocalDesignTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = currentTypography,
            content = content,
            motionScheme = MotionScheme.expressive()
        )
    }
}