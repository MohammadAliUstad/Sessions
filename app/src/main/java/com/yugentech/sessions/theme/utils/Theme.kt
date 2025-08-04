package com.yugentech.sessions.theme.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun SessionsTheme(
    themeConfiguration: ThemeConfiguration,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(themeConfiguration = themeConfiguration)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}