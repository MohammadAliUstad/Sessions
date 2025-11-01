package com.yugentech.sessions.theme.color

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.yugentech.sessions.theme.utils.ColorTheme
import com.yugentech.sessions.theme.utils.ThemeConfiguration
import com.yugentech.sessions.theme.utils.ThemeMode

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

    return when {
        themeConfiguration.colorTheme == ColorTheme.DYNAMIC &&
                themeConfiguration.useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkMode) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        else -> {
            when (themeConfiguration.colorTheme) {
                ColorTheme.DYNAMIC -> {
                    if (isDarkMode) AppColorSchemes.GrayDarkColorScheme else AppColorSchemes.GrayLightColorScheme
                }

                ColorTheme.MONOCHROME -> {
                    if (isDarkMode) AppColorSchemes.GrayDarkColorScheme else AppColorSchemes.GrayLightColorScheme
                }

                ColorTheme.BLUE -> {
                    if (isDarkMode) AppColorSchemes.DarkBlueScheme else AppColorSchemes.LightBlueScheme
                }

                ColorTheme.GREEN -> {
                    if (isDarkMode) AppColorSchemes.YellowDarkColorScheme else AppColorSchemes.YellowLightColorScheme
                }
            }
        }
    }
}