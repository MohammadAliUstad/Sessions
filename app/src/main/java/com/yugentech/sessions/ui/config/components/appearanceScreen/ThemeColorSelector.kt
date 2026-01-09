package com.yugentech.sessions.ui.config.components.appearanceScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsSectionHeader
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThemeColorSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    val currentPrimary = MaterialTheme.colorScheme.primary
    val currentTertiary = MaterialTheme.colorScheme.tertiary

    val themeOptions = remember(currentPrimary, currentTertiary) {
        themeOptions(currentPrimary, currentTertiary)
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SettingsSectionHeader(
            icon = Icons.Default.Palette,
            title = stringResource(R.string.color_theme)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            themeOptions.chunked(2).forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowOptions.forEach { themeOption ->
                        Box(modifier = Modifier.weight(1f)) {
                            ThemeCard(
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
}