package com.yugentech.sessions.ui.config.components.appearanceScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.models.ThemeMode
import com.yugentech.sessions.theme.tokens.spacing
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AmoledThemeSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val isDarkThemeActive = themeConfig.themeMode == ThemeMode.DARK ||
            (themeConfig.themeMode == ThemeMode.SYSTEM && isSystemDark)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.s),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            if (isDarkThemeActive) {
                LinearWavyProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .alpha(0.15f)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Transparent
                )
            }

            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
                headlineContent = {
                    Text(
                        text = "AMOLED Mode",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                supportingContent = {
                    Text(
                        text = if (isDarkThemeActive) "Use pure black background" else "Available in dark mode",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Contrast,
                        contentDescription = null,
                        tint = if (isDarkThemeActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.5f
                        )
                    )
                },
                trailingContent = {
                    Switch(
                        checked = themeConfig.isAmoledMode,
                        enabled = isDarkThemeActive,
                        onCheckedChange = { isChecked ->
                            viewModel.updateTheme(themeConfig.copy(isAmoledMode = isChecked))
                        },
                        thumbContent = if (themeConfig.isAmoledMode) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Contrast,
                                    contentDescription = null,
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                        } else null,
                        colors = SwitchDefaults.colors(
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            uncheckedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            )
        }
    }
}