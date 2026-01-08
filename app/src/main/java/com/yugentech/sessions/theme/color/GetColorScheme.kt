package com.yugentech.sessions.theme.color

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.yugentech.sessions.theme.models.ColorTheme
import com.yugentech.sessions.theme.models.ThemeConfiguration
import com.yugentech.sessions.theme.models.ThemeMode

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

    val baseScheme = when {
        themeConfiguration.colorTheme == ColorTheme.DYNAMIC &&
                themeConfiguration.useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            when (themeConfiguration.colorTheme) {
                ColorTheme.DYNAMIC, ColorTheme.MONOCHROME ->
                    if (isDarkMode) AppColorSchemes.EclipseDarkColorScheme else AppColorSchemes.EclipseLightColorScheme

                ColorTheme.BLUE ->
                    if (isDarkMode) AppColorSchemes.TwilightDarkColorScheme else AppColorSchemes.TwilightLightColorScheme

                ColorTheme.GREEN ->
                    if (isDarkMode) AppColorSchemes.GroveDarkColorScheme else AppColorSchemes.GroveLightColorScheme

                ColorTheme.YELLOW ->
                    if (isDarkMode) AppColorSchemes.CanyonDarkColorScheme else AppColorSchemes.CanyonLightColorScheme

                ColorTheme.PINK ->
                    if (isDarkMode) AppColorSchemes.SakuraDarkColorScheme else AppColorSchemes.SakuraLightColorScheme

                ColorTheme.RED ->
                    if (isDarkMode) AppColorSchemes.GarnetDarkColorScheme else AppColorSchemes.GarnetLightColorScheme

                ColorTheme.CYAN ->
                    if (isDarkMode) AppColorSchemes.LagoonDarkColorScheme else AppColorSchemes.LagoonLightColorScheme
            }
        }
    }

    return if (isDarkMode && themeConfiguration.isAmoledMode) {
        baseScheme.toAmoled()
    } else {
        baseScheme
    }
}

fun ColorScheme.toAmoled(): ColorScheme {
    return this.copy(
        background = Color.Black,
        surface = Color.Black,
        surfaceContainerLowest = Color.Black,
        surfaceContainerLow = Color(0xFF101010),
        scrim = Color.Black
    )
}