package com.yugentech.sessions.theme.builder

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.yugentech.sessions.theme.AppColorSchemes
import com.yugentech.sessions.theme.config.ColorTheme
import com.yugentech.sessions.theme.config.ThemeConfiguration
import com.yugentech.sessions.theme.config.ThemeMode

@Composable
fun getColorScheme(
    themeConfiguration: ThemeConfiguration,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme()
): ColorScheme {

    val isDarkMode = when (themeConfiguration.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme
    }

    val baseScheme = when (themeConfiguration.colorTheme) {
        ColorTheme.DYNAMIC -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (isDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (isDarkMode) AppColorSchemes.CanyonDarkColorScheme else AppColorSchemes.CanyonLightColorScheme
            }
        }

        ColorTheme.TWILIGHT ->
            if (isDarkMode) AppColorSchemes.TwilightDarkColorScheme else AppColorSchemes.TwilightLightColorScheme

        ColorTheme.SAKURA ->
            if (isDarkMode) AppColorSchemes.SakuraDarkColorScheme else AppColorSchemes.SakuraLightColorScheme

        ColorTheme.CANYON ->
            if (isDarkMode) AppColorSchemes.CanyonDarkColorScheme else AppColorSchemes.CanyonLightColorScheme

        ColorTheme.HARVEST ->
            if (isDarkMode) AppColorSchemes.HarvestDarkColorScheme else AppColorSchemes.HarvestLightColorScheme

        ColorTheme.GROVE ->
            if (isDarkMode) AppColorSchemes.GroveDarkColorScheme else AppColorSchemes.GroveLightColorScheme

        ColorTheme.ALPINE ->
            if (isDarkMode) AppColorSchemes.AlpineDarkColorScheme else AppColorSchemes.AlpineLightColorScheme

        ColorTheme.LAGOON ->
            if (isDarkMode) AppColorSchemes.LagoonDarkColorScheme else AppColorSchemes.LagoonLightColorScheme
    }

    return if (isDarkMode && themeConfiguration.isAmoledMode) {
        baseScheme.toAmoled()
    } else {
        baseScheme
    }
}

// Extension to force background colors to pure black for AMOLED displays
fun ColorScheme.toAmoled(): ColorScheme {
    return this.copy(
        background = Color.Black,
        surface = Color.Black,
        surfaceContainerLowest = Color.Black,
        surfaceContainerLow = Color(0xFF101010),
        scrim = Color.Black
    )
}