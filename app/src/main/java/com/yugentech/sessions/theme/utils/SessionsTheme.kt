package com.yugentech.sessions.theme.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.yugentech.sessions.ui.ProvideDesignTokens

@Composable
fun SessionsTheme(
    themeConfiguration: ThemeConfiguration,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(themeConfiguration = themeConfiguration)

    ProvideDesignTokens {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}