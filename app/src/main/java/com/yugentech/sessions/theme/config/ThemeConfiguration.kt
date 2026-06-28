package com.yugentech.sessions.theme.config

import com.yugentech.sessions.theme.AppFont

// Holds all user-customizable theme settings in a single state object
data class ThemeConfiguration(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorTheme: ColorTheme = ColorTheme.SESSIONS,
    val useDynamicColors: Boolean = true,
    val isAmoledMode: Boolean = false,
    val appFont: AppFont = AppFont.Google
)