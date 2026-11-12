package com.yugentech.sessions.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

// The app's colour scheme as it would be with AMOLED black off. SessionsTheme provides it; null
// outside SessionsTheme, where sheets just use whatever scheme is current.
internal val LocalSheetColorScheme = staticCompositionLocalOf<ColorScheme?> { null }

/**
 * Wrap a bottom sheet in this so it keeps the colours it has without AMOLED black. AMOLED blacks
 * out the app's backgrounds, but a sheet is a layer on top of them: drawn in pure black too it
 * would melt into the screen behind it, so it keeps its usual container colours instead.
 */
@Composable
fun SheetTheme(content: @Composable () -> Unit) {
    val scheme = LocalSheetColorScheme.current ?: MaterialTheme.colorScheme
    MaterialTheme(colorScheme = scheme, content = content)
}
