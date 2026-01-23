package com.yugentech.sessions.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.yugentech.sessions.theme.color.getColorScheme
import com.yugentech.sessions.theme.models.ThemeConfiguration
import com.yugentech.sessions.theme.tokens.LocalDesignTokens
import com.yugentech.sessions.theme.tokens.TokensCompact
import com.yugentech.sessions.theme.tokens.dimensions.TypographyCompact

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun SessionsTheme(
    themeConfiguration: ThemeConfiguration,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(themeConfiguration = themeConfiguration)
    val typography = TypographyCompact
    val tokens = TokensCompact

    CompositionLocalProvider(LocalDesignTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
            motionScheme = MotionScheme.expressive()
        )
    }
}