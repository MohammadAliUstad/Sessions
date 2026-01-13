package com.yugentech.sessions.ui.config.components.appearanceScreen

import androidx.compose.ui.graphics.Color
import com.yugentech.sessions.theme.color.AppColorSchemes
import com.yugentech.sessions.theme.models.ColorTheme

data class ThemeOption(
    val colorTheme: ColorTheme,
    val displayName: String,
    val primaryColor: Color,
    val gradientColors: List<Color>
)

fun themeOptions(
    currentPrimary: Color,
    currentTertiary: Color
): List<ThemeOption> = listOf(

    // Dynamic
    ThemeOption(
        colorTheme = ColorTheme.DYNAMIC,
        displayName = "Dynamic",
        primaryColor = currentPrimary,
        gradientColors = listOf(
            currentPrimary,
            currentTertiary,
            currentPrimary.copy(alpha = 0.7f)
        )
    ),

    // Sakura (Pink/Red)
    ThemeOption(
        colorTheme = ColorTheme.SAKURA,
        displayName = "Sakura",
        primaryColor = AppColorSchemes.SakuraLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.SakuraLightColorScheme.primary,
            AppColorSchemes.SakuraLightColorScheme.tertiary,
            AppColorSchemes.SakuraLightColorScheme.primaryContainer
        )
    ),

    // Canyon (Brown/Orange)
    ThemeOption(
        colorTheme = ColorTheme.CANYON,
        displayName = "Canyon",
        primaryColor = AppColorSchemes.CanyonLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.CanyonLightColorScheme.primary,
            AppColorSchemes.CanyonLightColorScheme.tertiary,
            AppColorSchemes.CanyonLightColorScheme.primaryContainer
        )
    ),

    // Harvest (Gold/Yellow)
    ThemeOption(
        colorTheme = ColorTheme.HARVEST,
        displayName = "Harvest",
        primaryColor = AppColorSchemes.HarvestLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.HarvestLightColorScheme.primary,
            AppColorSchemes.HarvestLightColorScheme.tertiary,
            AppColorSchemes.HarvestLightColorScheme.primaryContainer
        )
    ),

    // Grove (Olive/Yellow-Green)
    ThemeOption(
        colorTheme = ColorTheme.GROVE,
        displayName = "Grove",
        primaryColor = AppColorSchemes.GroveLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.GroveLightColorScheme.primary,
            AppColorSchemes.GroveLightColorScheme.tertiary,
            AppColorSchemes.GroveLightColorScheme.primaryContainer
        )
    ),

    // Alpine (Deep Green/Forest)
    ThemeOption(
        colorTheme = ColorTheme.ALPINE,
        displayName = "Alpine",
        primaryColor = AppColorSchemes.AlpineLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.AlpineLightColorScheme.primary,
            AppColorSchemes.AlpineLightColorScheme.tertiary,
            AppColorSchemes.AlpineLightColorScheme.primaryContainer
        )
    ),

    // Lagoon (Cyan/Teal)
    ThemeOption(
        colorTheme = ColorTheme.LAGOON,
        displayName = "Lagoon",
        primaryColor = AppColorSchemes.LagoonLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.LagoonLightColorScheme.primary,
            AppColorSchemes.LagoonLightColorScheme.tertiary,
            AppColorSchemes.LagoonLightColorScheme.primaryContainer
        )
    ),

    // Twilight (Indigo/Purple tones)
    ThemeOption(
        colorTheme = ColorTheme.TWILIGHT,
        displayName = "Twilight",
        primaryColor = AppColorSchemes.TwilightLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.TwilightLightColorScheme.primary,
            AppColorSchemes.TwilightLightColorScheme.tertiary,
            AppColorSchemes.TwilightLightColorScheme.primaryContainer
        )
    )
)