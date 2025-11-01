package com.yugentech.sessions.ui.config.components.appearanceScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.utils.ThemeMode
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThemeModeSelector(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()

    PixelCard(
        modifier = modifier
    ) {
        PixelSectionHeader(
            icon = Icons.Default.Brightness6,
            title = "Theme Mode"
        )

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ThemeMode.entries.forEach { themeMode ->
                val (icon, title, subtitle) = when (themeMode) {
                    ThemeMode.LIGHT -> Triple(
                        Icons.Default.LightMode,
                        "Light",
                        "Always use light appearance"
                    )

                    ThemeMode.DARK -> Triple(
                        Icons.Default.DarkMode,
                        "Dark",
                        "Always use dark appearance"
                    )

                    ThemeMode.SYSTEM -> Triple(
                        Icons.Default.AutoMode,
                        "System",
                        "Match system appearance"
                    )
                }

                PixelThemeModeOption(
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