package com.yugentech.sessions.theme.utils

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class ColorTheme {
    DYNAMIC,
    MONOCHROME,
    BLUE,
    ORANGE
}

data class ThemeConfiguration(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorTheme: ColorTheme = ColorTheme.DYNAMIC,
    val useDynamicColors: Boolean = true
)