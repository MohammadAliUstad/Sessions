package com.yugentech.sessions.theme.models

import com.yugentech.sessions.theme.getters.AppFont

// Holds all user-customizable theme settings in a single state object
data class ThemeConfiguration(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorTheme: ColorTheme = ColorTheme.CANYON,
    val useDynamicColors: Boolean = true,
    val isAmoledMode: Boolean = false,
    val appFont: AppFont = AppFont.Google
)