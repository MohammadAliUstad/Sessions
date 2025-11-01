package com.yugentech.sessions.ui.config.components.appearanceScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.ui.config.components.settingsScreen.themeOptions
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThemeSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    val currentPrimary = MaterialTheme.colorScheme.primary
    val currentTertiary = MaterialTheme.colorScheme.tertiary
    val themeOptions =
        remember(currentPrimary, currentTertiary) { themeOptions(currentPrimary, currentTertiary) }

    PixelCard(modifier = modifier) {
        PixelSectionHeader(icon = Icons.Default.Palette, title = "Color Theme")

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                themeOptions.take(2).forEach { themeOption ->
                    Box(modifier = Modifier.weight(1f)) {
                        PixelThemeCard(
                            themeOption = themeOption,
                            isSelected = themeConfig.colorTheme == themeOption.colorTheme,
                            onClick = {
                                val newConfig = themeConfig.copy(colorTheme = themeOption.colorTheme)
                                viewModel.updateTheme(newConfig)
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                themeOptions.drop(2).forEach { themeOption ->
                    Box(modifier = Modifier.weight(1f)) {
                        PixelThemeCard(
                            themeOption = themeOption,
                            isSelected = themeConfig.colorTheme == themeOption.colorTheme,
                            onClick = {
                                val newConfig = themeConfig.copy(colorTheme = themeOption.colorTheme)
                                viewModel.updateTheme(newConfig)
                            }
                        )
                    }
                }
            }
        }
    }
}