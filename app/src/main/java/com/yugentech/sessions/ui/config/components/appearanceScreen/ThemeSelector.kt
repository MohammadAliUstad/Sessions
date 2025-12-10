package com.yugentech.sessions.ui.config.components.appearanceScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.theme.tokens.spacing
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

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(MaterialTheme.corners.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.l)
        ) {
            SectionHeader(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.color_theme)
            )

            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    themeOptions.take(2).forEach { themeOption ->
                        Box(modifier = Modifier.weight(AppConstants.ONEF)) {
                            ThemeCard(
                                themeOption = themeOption,
                                isSelected = themeConfig.colorTheme == themeOption.colorTheme,
                                onClick = {
                                    val newConfig =
                                        themeConfig.copy(colorTheme = themeOption.colorTheme)
                                    viewModel.updateTheme(newConfig)
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    themeOptions.drop(AppConstants.TWO).forEach { themeOption ->
                        Box(modifier = Modifier.weight(1f)) {
                            ThemeCard(
                                themeOption = themeOption,
                                isSelected = themeConfig.colorTheme == themeOption.colorTheme,
                                onClick = {
                                    val newConfig =
                                        themeConfig.copy(colorTheme = themeOption.colorTheme)
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