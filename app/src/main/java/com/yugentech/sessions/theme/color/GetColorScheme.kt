package com.yugentech.sessions.theme.color

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.yugentech.sessions.theme.models.ColorTheme
import com.yugentech.sessions.theme.models.ThemeConfiguration
import com.yugentech.sessions.theme.models.ThemeMode
import timber.log.Timber

@Composable
fun getColorScheme(
    themeConfiguration: ThemeConfiguration,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme()
): ColorScheme {

    // Determine effective dark mode based on user override or system default
    val isDarkMode = when (themeConfiguration.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme
    }

    Timber.v("Resolving color scheme. DarkMode: $isDarkMode, Theme: ${themeConfiguration.colorTheme}")

    return when {
        // Use Android 12+ Dynamic Colors (Material You) if enabled and supported
        themeConfiguration.colorTheme == ColorTheme.DYNAMIC &&
                themeConfiguration.useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkMode) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        // Fallback to internal static color schemes
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