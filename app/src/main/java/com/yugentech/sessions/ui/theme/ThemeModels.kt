package com.yugentech.sessions.ui.theme

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class ColorTheme {
    DYNAMIC,
    MONOCHROME,
    BLUE,
    GREEN,
    ORANGE,
    PURPLE,
    TEAL
}

data class ThemeConfig(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorTheme: ColorTheme = ColorTheme.DYNAMIC,
    val useDynamicColors: Boolean = true
)