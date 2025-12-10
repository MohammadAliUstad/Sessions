package com.yugentech.sessions.ui.config.components.appearanceScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.models.ThemeMode
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThemeModeSelector(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()

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
                icon = Icons.Default.Brightness6,
                title = stringResource(R.string.theme_mode)
            )

            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
            ) {
                ThemeMode.entries.forEach { themeMode ->
                    val (icon, title, subtitle) = when (themeMode) {
                        ThemeMode.LIGHT -> Triple(
                            Icons.Default.LightMode,
                            stringResource(R.string.light),
                            stringResource(R.string.always_use_light_appearance)
                        )

                        ThemeMode.DARK -> Triple(
                            Icons.Default.DarkMode,
                            stringResource(R.string.dark),
                            stringResource(R.string.always_use_dark_appearance)
                        )

                        ThemeMode.SYSTEM -> Triple(
                            Icons.Default.AutoMode,
                            stringResource(R.string.system),
                            stringResource(R.string.match_system_appearance)
                        )
                    }

                    ThemeModeOption(
                        icon = icon,
                        title = title,
                        subtitle = subtitle,
                        isSelected = themeConfig.themeMode == themeMode,
                        onClick = {
                            val newConfig = themeConfig.copy(themeMode = themeMode)
                            themeViewModel.updateTheme(newConfig)
                        }
                    )
                }
            }
        }
    }
}