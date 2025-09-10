package com.yugentech.sessions.ui.components.settingsScreen

import androidx.compose.ui.graphics.Color
import com.yugentech.sessions.theme.utils.ColorTheme

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
    ThemeOption(
        colorTheme = ColorTheme.MONOCHROME,
        displayName = "Monochrome",
        primaryColor = Color(0xFF1C1B1F),
        gradientColors = listOf(
            Color(0xFF1C1B1F),
            Color(0xFF49454F),
            Color(0xFF79747E)
        )
    ),
    ThemeOption(
        colorTheme = ColorTheme.BLUE,
        displayName = "Ocean",
        primaryColor = Color(0xFF0061A4),
        gradientColors = listOf(
            Color(0xFF0061A4),
            Color(0xFF6B5778),
            Color(0xFF9ECAFF)
        )
    ),
    ThemeOption(
        colorTheme = ColorTheme.GREEN,
        displayName = "Sunset",
        primaryColor = Color(0xFF8C4A00),
        gradientColors = listOf(
            Color(0xFF8C4A00),
            Color(0xFF5D5E2F),
            Color(0xFFFFB86E)
        )
    )
)