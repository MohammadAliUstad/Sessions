package com.yugentech.sessions.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val MonochromeDarkColorScheme = darkColorScheme(
    primary = Pure_White,
    onPrimary = Pure_Black,
    primaryContainer = Medium_Dark_Gray,
    onPrimaryContainer = Very_Light_Gray,

    secondary = Light_Gray,
    onSecondary = Pure_Black,
    secondaryContainer = Dark_Gray,
    onSecondaryContainer = Very_Light_Gray,

    tertiary = Gray,
    onTertiary = Pure_White,
    tertiaryContainer = Medium_Dark_Gray,
    onTertiaryContainer = Very_Light_Gray,

    background = Pure_Black,
    onBackground = Pure_White,

    surface = Dark_Gray,
    onSurface = Pure_White,
    surfaceVariant = Medium_Dark_Gray,
    onSurfaceVariant = Very_Light_Gray,

    error = Color(0xFFD3D3D3),
    onError = Pure_Black,
    errorContainer = Gray,
    onErrorContainer = Pure_White,

    outline = Light_Gray,
    outlineVariant = Gray,
    scrim = Pure_Black.copy(alpha = 0.4f),
    inverseSurface = Very_Light_Gray,
    inverseOnSurface = Pure_Black,
    inversePrimary = Dark_Gray
)

private val MonochromeLightColorScheme = lightColorScheme(
    primary = Pure_Black,
    onPrimary = Pure_White,
    primaryContainer = Light_Gray,
    onPrimaryContainer = Pure_Black,

    secondary = Gray,
    onSecondary = Pure_White,
    secondaryContainer = Very_Light_Gray,
    onSecondaryContainer = Pure_Black,

    tertiary = Medium_Dark_Gray,
    onTertiary = Pure_White,
    tertiaryContainer = Light_Gray,
    onTertiaryContainer = Pure_Black,

    background = Pure_White,
    onBackground = Pure_Black,

    surface = Very_Light_Gray,
    onSurface = Pure_Black,
    surfaceVariant = Light_Gray,
    onSurfaceVariant = Dark_Gray,

    error = Gray,
    onError = Pure_White,
    errorContainer = Light_Gray,
    onErrorContainer = Pure_Black,

    outline = Gray,
    outlineVariant = Light_Gray,
    scrim = Pure_Black.copy(alpha = 0.2f),
    inverseSurface = Dark_Gray,
    inverseOnSurface = Pure_White,
    inversePrimary = Very_Light_Gray
)

@Composable
fun SessionsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> MonochromeDarkColorScheme
        else -> MonochromeLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}