package com.yugentech.sessions.ui.config.appearanceScreen.components

import androidx.compose.ui.graphics.Color
import com.yugentech.sessions.theme.AppColorSchemes
import com.yugentech.sessions.theme.config.ColorTheme

data class ThemeOption(
    val colorTheme: ColorTheme,
    val displayName: String,
    val primaryColor: Color,
    val gradientColors: List<Color>
)

fun themeOptions(
    currentPrimary: Color,
    currentPrimaryContainer: Color
): List<ThemeOption> = listOf(

    ThemeOption(
        colorTheme = ColorTheme.DYNAMIC,
        displayName = "Dynamic",
        primaryColor = currentPrimary,
        gradientColors = listOf(
            currentPrimary,
            currentPrimaryContainer
        )
    ),

    ThemeOption(
        colorTheme = ColorTheme.SESSIONS,
        displayName = "Sessions",
        primaryColor = AppColorSchemes.SessionsLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.SessionsLightColorScheme.primary,
            AppColorSchemes.SessionsLightColorScheme.primaryContainer
        )
    ),

    ThemeOption(
        colorTheme = ColorTheme.LAGOON,
        displayName = "Lagoon",
        primaryColor = AppColorSchemes.LagoonLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.LagoonLightColorScheme.primary,
            AppColorSchemes.LagoonLightColorScheme.primaryContainer
        )
    ),

    ThemeOption(
        colorTheme = ColorTheme.HARVEST,
        displayName = "Harvest",
        primaryColor = AppColorSchemes.HarvestLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.HarvestLightColorScheme.primary,
            AppColorSchemes.HarvestLightColorScheme.primaryContainer
        )
    ),

    ThemeOption(
        colorTheme = ColorTheme.GROVE,
        displayName = "Grove",
        primaryColor = AppColorSchemes.GroveLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.GroveLightColorScheme.primary,
            AppColorSchemes.GroveLightColorScheme.primaryContainer
        )
    ),

    ThemeOption(
        colorTheme = ColorTheme.SAKURA,
        displayName = "Sakura",
        primaryColor = AppColorSchemes.SakuraLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.SakuraLightColorScheme.primary,
            AppColorSchemes.SakuraLightColorScheme.primaryContainer
        )
    ),

    ThemeOption(
        colorTheme = ColorTheme.ALPINE,
        displayName = "Alpine",
        primaryColor = AppColorSchemes.AlpineLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.AlpineLightColorScheme.primary,
            AppColorSchemes.AlpineLightColorScheme.primaryContainer
        )
    ),

    ThemeOption(
        colorTheme = ColorTheme.TWILIGHT,
        displayName = "Twilight",
        primaryColor = AppColorSchemes.TwilightLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.TwilightLightColorScheme.primary,
            AppColorSchemes.TwilightLightColorScheme.primaryContainer
        )
    )
)