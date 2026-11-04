package com.yugentech.sessions.ui.config.appearanceScreen.components

import com.yugentech.sessions.theme.config.ColorTheme

// The colour themes offered in Appearance; their colours come from the real schemes
data class ThemeOption(
    val colorTheme: ColorTheme,
    val displayName: String
)

val themeOptions = listOf(
    ThemeOption(ColorTheme.DYNAMIC, "Dynamic"),
    ThemeOption(ColorTheme.SESSIONS, "Sessions"),
    ThemeOption(ColorTheme.LAGOON, "Lagoon"),
    ThemeOption(ColorTheme.GROVE, "Grove"),
    ThemeOption(ColorTheme.HARVEST, "Harvest"),
    ThemeOption(ColorTheme.SAKURA, "Sakura"),
    ThemeOption(ColorTheme.ALPINE, "Alpine"),
    ThemeOption(ColorTheme.TWILIGHT, "Twilight")
)
