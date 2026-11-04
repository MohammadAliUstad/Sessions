package com.yugentech.sessions.theme.builder

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
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

    var baseScheme = when (themeConfiguration.colorTheme) {
        ColorTheme.DYNAMIC -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (isDarkMode) dynamicDarkColorScheme(context).dimTertiaryContainer() else dynamicLightColorScheme(context)
            } else {
                if (isDarkMode) AppColorSchemes.SessionsDarkColorScheme else AppColorSchemes.SessionsLightColorScheme
            }
        }

        ColorTheme.TWILIGHT ->
            if (isDarkMode) AppColorSchemes.TwilightDarkColorScheme else AppColorSchemes.TwilightLightColorScheme

        ColorTheme.SAKURA ->
            if (isDarkMode) AppColorSchemes.SakuraDarkColorScheme else AppColorSchemes.SakuraLightColorScheme

        ColorTheme.SESSIONS ->
            if (isDarkMode) AppColorSchemes.SessionsDarkColorScheme else AppColorSchemes.SessionsLightColorScheme

        ColorTheme.HARVEST ->
            if (isDarkMode) AppColorSchemes.HarvestDarkColorScheme else AppColorSchemes.HarvestLightColorScheme

        ColorTheme.GROVE ->
            if (isDarkMode) AppColorSchemes.GroveDarkColorScheme else AppColorSchemes.GroveLightColorScheme

        ColorTheme.ALPINE ->
            if (isDarkMode) AppColorSchemes.AlpineDarkColorScheme else AppColorSchemes.AlpineLightColorScheme

        ColorTheme.LAGOON ->
            if (isDarkMode) AppColorSchemes.LagoonDarkColorScheme else AppColorSchemes.LagoonLightColorScheme
    }

    if (isDarkMode && themeConfiguration.colorTheme != ColorTheme.DYNAMIC) {
        baseScheme = baseScheme.deepenSurfaces(fraction = 0.12f)
    }

    return if (isDarkMode && themeConfiguration.isAmoledMode) {
        baseScheme.toAmoled()
    } else {
        baseScheme
    }
}

/**
 * Dynamic's dark tertiary container glares among the other containers, so it's toned down a
 * little. It stays a light pastel, so its text is a deep shade of the same colour, which reads
 * clearly on it (around 6:1).
 */
fun ColorScheme.dimTertiaryContainer(): ColorScheme {
    val container = lerp(tertiaryContainer, Color.Black, TERTIARY_CONTAINER_DIMMING)
    return copy(
        tertiaryContainer = container,
        onTertiaryContainer = lerp(container, Color.Black, ON_TERTIARY_CONTAINER_DARKENING)
    )
}

/** How far Dynamic's dark tertiary container is pulled toward black. */
private const val TERTIARY_CONTAINER_DIMMING = 0.2f

/** How far the container colour is pulled toward black to make the text on it. */
private const val ON_TERTIARY_CONTAINER_DARKENING = 0.6f

fun ColorScheme.deepenSurfaces(fraction: Float): ColorScheme {
    return this.copy(
        background = lerp(this.background, Color.Black, fraction),
        surface = lerp(this.surface, Color.Black, fraction),
        surfaceDim = lerp(this.surfaceDim, Color.Black, fraction),
        surfaceBright = lerp(this.surfaceBright, Color.Black, fraction),
        surfaceContainerLowest = lerp(this.surfaceContainerLowest, Color.Black, fraction),
        surfaceContainerLow = lerp(this.surfaceContainerLow, Color.Black, fraction),
        surfaceContainer = lerp(this.surfaceContainer, Color.Black, fraction),
        surfaceContainerHigh = lerp(this.surfaceContainerHigh, Color.Black, fraction),
        surfaceContainerHighest = lerp(this.surfaceContainerHighest, Color.Black, fraction),
        surfaceVariant = lerp(this.surfaceVariant, Color.Black, fraction * 0.5f),
        onBackground = lerp(this.onBackground, Color.Black, fraction * 0.15f),
        onSurface = lerp(this.onSurface, Color.Black, fraction * 0.15f),
        onSurfaceVariant = lerp(this.onSurfaceVariant, Color.Black, fraction * 0.15f),
    )
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