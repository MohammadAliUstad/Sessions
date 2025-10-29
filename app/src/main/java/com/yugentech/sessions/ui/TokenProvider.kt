package com.yugentech.sessions.ui

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*

val LocalDesignTokens = staticCompositionLocalOf { TokensCompact }

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TokenProvider(
    activity: Activity,
    content: @Composable () -> Unit
) {
    val windowSizeClass = calculateWindowSizeClass(activity)

    val tokens = remember(windowSizeClass) {
        selectTokensForWindow(windowSizeClass)
    }

    CompositionLocalProvider(
        LocalDesignTokens provides tokens,
        content = content
    )
}

fun selectTokensForWindow(windowSizeClass: WindowSizeClass): DesignTokens {
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> TokensCompact
        WindowWidthSizeClass.Medium -> TokensMedium
        WindowWidthSizeClass.Expanded -> TokensExpanded
        else -> TokensCompact
    }
}

object AppTokens {
    @Composable
    fun current(): DesignTokens = LocalDesignTokens.current
}