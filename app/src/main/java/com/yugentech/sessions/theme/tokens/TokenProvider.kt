package com.yugentech.sessions.theme.tokens

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// Provides the active token set for the current screen width
val LocalDesignTokens = staticCompositionLocalOf { TokensCompact }

/**
 * Automatically selects the appropriate DesignTokens set based on window size.
 * Follows Material Design 3 breakpoints:
 * - Compact: 0-599dp (All phones)
 * - Medium: 600-839dp (Tablets in portrait, unfolded foldables)
 * - Expanded: 840dp+ (Tablets in landscape, desktop)
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

/**
 * Simple shorthand for accessing tokens anywhere in the composition
 * Usage: Tokens.spacing.m, Tokens.icons.medium, etc.
 */
val Tokens @Composable get() = LocalDesignTokens.current

/**
 * Extension properties for MaterialTheme-style access
 * Usage: MaterialTheme.spacing.m, MaterialTheme.icons.medium
 */
val MaterialTheme.spacing: SpacingTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalDesignTokens.current.spacing

val MaterialTheme.corners: CornerTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalDesignTokens.current.corners

val MaterialTheme.icons: IconSizeTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalDesignTokens.current.icons

val MaterialTheme.components: ComponentTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalDesignTokens.current.components

val MaterialTheme.elevation: ElevationTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalDesignTokens.current.elevation

val MaterialTheme.strokes: StrokeTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalDesignTokens.current.strokeWidths