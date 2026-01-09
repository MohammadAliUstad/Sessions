package com.yugentech.sessions.ui.config.components.appearanceScreen

import androidx.compose.ui.graphics.Color
import com.yugentech.sessions.theme.models.ColorTheme
import com.yugentech.sessions.theme.color.AppColorSchemes

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
    // Dynamic (Material You)
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

    // Eclipse (Monochrome)
    ThemeOption(
        colorTheme = ColorTheme.MONOCHROME,
        displayName = "Eclipse",
        primaryColor = AppColorSchemes.EclipseLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.EclipseLightColorScheme.primary,
            AppColorSchemes.EclipseLightColorScheme.tertiary,
            AppColorSchemes.EclipseLightColorScheme.primaryContainer
        )
    ),

    // Twilight (Blue/Indigo)
    ThemeOption(
        colorTheme = ColorTheme.BLUE,
        displayName = "Twilight",
        primaryColor = AppColorSchemes.TwilightLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.TwilightLightColorScheme.primary,
            AppColorSchemes.TwilightLightColorScheme.tertiary,
            AppColorSchemes.TwilightLightColorScheme.primaryContainer
        )
    ),

    // Grove (Green)
    ThemeOption(
        colorTheme = ColorTheme.GREEN,
        displayName = "Grove",
        primaryColor = AppColorSchemes.GroveLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.GroveLightColorScheme.primary,
            AppColorSchemes.GroveLightColorScheme.tertiary,
            AppColorSchemes.GroveLightColorScheme.primaryContainer
        )
    ),

    // Canyon (Yellow/Brown)
    ThemeOption(
        colorTheme = ColorTheme.YELLOW,
        displayName = "Canyon",
        primaryColor = AppColorSchemes.CanyonLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.CanyonLightColorScheme.primary,
            AppColorSchemes.CanyonLightColorScheme.tertiary,
            AppColorSchemes.CanyonLightColorScheme.primaryContainer
        )
    ),

    // Sakura (Pink)
    ThemeOption(
        colorTheme = ColorTheme.PINK,
        displayName = "Sakura",
        primaryColor = AppColorSchemes.SakuraLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.SakuraLightColorScheme.primary,
            AppColorSchemes.SakuraLightColorScheme.tertiary,
            AppColorSchemes.SakuraLightColorScheme.primaryContainer
        )
    ),

    // Garnet (Red)
    ThemeOption(
        colorTheme = ColorTheme.RED,
        displayName = "Garnet",
        primaryColor = AppColorSchemes.GarnetLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.GarnetLightColorScheme.primary,
            AppColorSchemes.GarnetLightColorScheme.tertiary,
            AppColorSchemes.GarnetLightColorScheme.primaryContainer
        )
    ),

    // Lagoon (Cyan/Teal)
    ThemeOption(
        colorTheme = ColorTheme.CYAN,
        displayName = "Lagoon",
        primaryColor = AppColorSchemes.LagoonLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.LagoonLightColorScheme.primary,
            AppColorSchemes.LagoonLightColorScheme.tertiary,
            AppColorSchemes.LagoonLightColorScheme.primaryContainer
        )
    )
)