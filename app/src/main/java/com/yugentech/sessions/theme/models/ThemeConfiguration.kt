package com.yugentech.sessions.theme.models

// Holds the user's selected visual preferences for the app
data class ThemeConfiguration(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val colorTheme: ColorTheme = ColorTheme.DYNAMIC,
    val useDynamicColors: Boolean = true
)