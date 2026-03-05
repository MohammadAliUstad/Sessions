package com.yugentech.sessions.theme.tokens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.yugentech.sessions.theme.tokens.dimensions.ComponentTokens
import com.yugentech.sessions.theme.tokens.dimensions.CornerTokens
import com.yugentech.sessions.theme.tokens.dimensions.ElevationTokens
import com.yugentech.sessions.theme.tokens.dimensions.IconSizeTokens
import com.yugentech.sessions.theme.tokens.dimensions.SpacingTokens
import com.yugentech.sessions.theme.tokens.dimensions.StrokeTokens

val LocalDesignTokens = staticCompositionLocalOf { TokensCompact }

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