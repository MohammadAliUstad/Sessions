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
                // 1. Sakura (Pink)
                ColorTheme.SAKURA ->
                    if (isDarkMode) AppColorSchemes.SakuraDarkColorScheme else AppColorSchemes.SakuraLightColorScheme

                // 2. Canyon (Brown)
                ColorTheme.CANYON ->
                    if (isDarkMode) AppColorSchemes.CanyonDarkColorScheme else AppColorSchemes.CanyonLightColorScheme

                // 3. Harvest (Gold)
                ColorTheme.HARVEST ->
                    if (isDarkMode) AppColorSchemes.HarvestDarkColorScheme else AppColorSchemes.HarvestLightColorScheme

                // 4. Grove (Olive)
                ColorTheme.GROVE ->
                    if (isDarkMode) AppColorSchemes.GroveDarkColorScheme else AppColorSchemes.GroveLightColorScheme

                // 5. Alpine (Green)
                ColorTheme.ALPINE ->
                    if (isDarkMode) AppColorSchemes.AlpineDarkColorScheme else AppColorSchemes.AlpineLightColorScheme

                // 6. Lagoon (Teal)
                ColorTheme.LAGOON ->
                    if (isDarkMode) AppColorSchemes.LagoonDarkColorScheme else AppColorSchemes.LagoonLightColorScheme

                // 7. Twilight (Indigo)
                ColorTheme.DYNAMIC,
                ColorTheme.TWILIGHT ->
                    if (isDarkMode) AppColorSchemes.TwilightDarkColorScheme else AppColorSchemes.TwilightLightColorScheme
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