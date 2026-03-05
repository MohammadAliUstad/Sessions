package com.yugentech.sessions.ui.config.appearanceScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.theme.config.ThemeMode
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.itemShape
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThemeModeSelector(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            icon = Icons.Default.Brightness6,
            title = stringResource(R.string.theme_mode)
        )

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
        ) {
            val modes = ThemeMode.entries
            modes.forEachIndexed { index, themeMode ->
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

                ThemeRadioItem(
                    title = title,
                    subtitle = subtitle,
                    icon = icon,
                    selected = themeConfig.themeMode == themeMode,
                    index = index,
                    totalCount = modes.size,
                    onClick = {
                        val newConfig = themeConfig.copy(themeMode = themeMode)
                        themeViewModel.updateTheme(newConfig)
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeRadioItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    index: Int,
    totalCount: Int,
    onClick: () -> Unit
) {
    val shape = itemShape(index, totalCount)

    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.xs)
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = null
            )
        },
        modifier = Modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}