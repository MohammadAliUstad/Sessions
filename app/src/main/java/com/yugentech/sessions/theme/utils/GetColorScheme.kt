package com.yugentech.sessions.theme.utils

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
                    if (isDarkMode) AppColorSchemes.MonochromeDark else AppColorSchemes.MonochromeLight
                }

                ColorTheme.MONOCHROME -> {
                    if (isDarkMode) AppColorSchemes.MonochromeDark else AppColorSchemes.MonochromeLight
                }

                ColorTheme.BLUE -> {
                    if (isDarkMode) AppColorSchemes.BlueDark else AppColorSchemes.BlueLight
                }

                ColorTheme.GREEN -> {
                    if (isDarkMode) AppColorSchemes.GreenDark else AppColorSchemes.GreenLight
                }

                ColorTheme.ORANGE -> {
                    if (isDarkMode) AppColorSchemes.OrangeDark else AppColorSchemes.OrangeLight
                }

                ColorTheme.PURPLE -> {
                    if (isDarkMode) AppColorSchemes.PurpleDark else AppColorSchemes.PurpleLight
                }

                ColorTheme.TEAL -> {
                    if (isDarkMode) AppColorSchemes.TealDark else AppColorSchemes.TealLight
                }
            }
        }
    }
}