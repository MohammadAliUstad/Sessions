package com.yugentech.sessions.theme.models

data class ThemeConfiguration(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val colorTheme: ColorTheme = ColorTheme.DYNAMIC,
    val useDynamicColors: Boolean = true,
    val isAmoledMode: Boolean = false
)