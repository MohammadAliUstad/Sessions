package com.yugentech.sessions.ui

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// Provides the active token set for the current screen width
val LocalDesignTokens = staticCompositionLocalOf { TokensCompact }

/**
 * Automatically selects the appropriate DesignTokens set based on window size.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ProvideDesignTokens(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return

    val windowSizeClass = calculateWindowSizeClass(activity)

    val tokens = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> TokensCompact
        WindowWidthSizeClass.Medium -> TokensMedium
        WindowWidthSizeClass.Expanded -> TokensExpanded
        else -> TokensCompact
    }

    CompositionLocalProvider(LocalDesignTokens provides tokens) {
        content()
    }
}

/** Simple shorthand for accessing tokens anywhere in the composition */
val Tokens @Composable get() = LocalDesignTokens.current
