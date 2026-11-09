package com.yugentech.sessions.ui.config.appearanceScreen.components

import androidx.annotation.StringRes
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.config.ColorTheme

// The colour themes offered in Appearance; their colours come from the real schemes
data class ThemeOption(
    val colorTheme: ColorTheme,
    @param:StringRes val displayName: Int
)

val themeOptions = listOf(
    ThemeOption(ColorTheme.DYNAMIC, R.string.theme_name_dynamic),
    ThemeOption(ColorTheme.SESSIONS, R.string.theme_name_sessions),
    ThemeOption(ColorTheme.LAGOON, R.string.theme_name_lagoon),
    ThemeOption(ColorTheme.GROVE, R.string.theme_name_grove),
    ThemeOption(ColorTheme.HARVEST, R.string.theme_name_harvest),
    ThemeOption(ColorTheme.SAKURA, R.string.theme_name_sakura),
    ThemeOption(ColorTheme.ALPINE, R.string.theme_name_alpine),
    ThemeOption(ColorTheme.TWILIGHT, R.string.theme_name_twilight)
)
