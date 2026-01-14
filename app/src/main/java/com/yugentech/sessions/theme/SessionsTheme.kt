package com.yugentech.sessions.theme

import android.app.Activity
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.yugentech.sessions.theme.color.getColorScheme
import com.yugentech.sessions.theme.models.ThemeConfiguration
import com.yugentech.sessions.theme.tokens.LocalDesignTokens
import com.yugentech.sessions.theme.tokens.TokensCompact
import com.yugentech.sessions.theme.tokens.TokensExpanded
import com.yugentech.sessions.theme.tokens.TokensMedium
import com.yugentech.sessions.theme.tokens.dimensions.TypographyCompact
import com.yugentech.sessions.theme.tokens.dimensions.TypographyExpanded
import com.yugentech.sessions.theme.tokens.dimensions.TypographyMedium

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun SessionsTheme(
    themeConfiguration: ThemeConfiguration,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val colorScheme = getColorScheme(themeConfiguration = themeConfiguration)
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }
    val widthSizeClass = windowSizeClass?.widthSizeClass ?: WindowWidthSizeClass.Compact

    val typography = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> TypographyCompact
        WindowWidthSizeClass.Medium -> TypographyMedium
        WindowWidthSizeClass.Expanded -> TypographyExpanded
        else -> TypographyCompact
    }

    val tokens = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> TokensCompact
        WindowWidthSizeClass.Medium -> TokensMedium
        WindowWidthSizeClass.Expanded -> TokensExpanded
        else -> TokensCompact
    }

    CompositionLocalProvider(LocalDesignTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
            motionScheme = MotionScheme.expressive()
        )
    }
}