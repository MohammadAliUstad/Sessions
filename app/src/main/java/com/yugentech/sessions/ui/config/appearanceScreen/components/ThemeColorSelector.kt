package com.yugentech.sessions.ui.config.appearanceScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.LocalThemeReveal
import com.yugentech.sessions.theme.SLOW_FADE_MS
import com.yugentech.sessions.theme.builder.getColorScheme
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThemeColorSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    val reveal = LocalThemeReveal.current

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SectionHeader(
            icon = Icons.Default.Palette,
            title = stringResource(R.string.color_theme)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            themeOptions.chunked(2).forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    rowOptions.forEach { option ->
                        // Each tile is drawn in the scheme it would give, in the current mode
                        ThemeCard(
                            name = option.displayName,
                            scheme = getColorScheme(themeConfig.copy(colorTheme = option.colorTheme)),
                            isSelected = themeConfig.colorTheme == option.colorTheme,
                            onClick = {
                                // A simple fade from the old colours to the new, across the whole screen
                                val apply = { viewModel.updateTheme(themeConfig.copy(colorTheme = option.colorTheme)) }
                                reveal?.fade(apply, SLOW_FADE_MS) ?: apply()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // An odd last theme keeps its half width
                    if (rowOptions.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
