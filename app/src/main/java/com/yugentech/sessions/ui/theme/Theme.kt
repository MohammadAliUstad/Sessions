package com.yugentech.sessions.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun SessionsTheme(
    themeConfig: ThemeConfig,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(themeConfig = themeConfig)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}