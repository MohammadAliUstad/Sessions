package com.yugentech.sessions.theme.models

data class ThemeConfiguration(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorTheme: ColorTheme = ColorTheme.CANYON,
    val useDynamicColors: Boolean = true,
    val isAmoledMode: Boolean = false
)