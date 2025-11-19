package com.yugentech.sessions.ui.config.components.aboutScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.appearanceScreen.PixelCard
import com.yugentech.sessions.ui.config.components.appearanceScreen.PixelSectionHeader
import com.yugentech.sessions.ui.config.components.appearanceScreen.PixelThemeModeOption

@Composable
fun AboutContactCard(
    onEmailClick: () -> Unit,
    onGitHubClick: () -> Unit
) {
    PixelCard {
        PixelSectionHeader(
            icon = Icons.Default.Info,
            title = "Connect"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
        ) {
            PixelThemeModeOption(
                icon = Icons.Default.Email,
                title = "Contact Developer",
                subtitle = "Get in touch for support or feedback",
                isSelected = false,
                isRadio = false,
                onClick = onEmailClick
            )

            PixelThemeModeOption(
                icon = Icons.Default.Info,
                title = "Visit GitHub",
                subtitle = "View source code and contribute",
                isSelected = false,
                isRadio = false,
                onClick = onGitHubClick
            )
        }
    }
}