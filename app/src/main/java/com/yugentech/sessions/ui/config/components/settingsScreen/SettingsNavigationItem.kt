package com.yugentech.sessions.ui.config.components.settingsScreen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsNavigationItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.corners.small),
        color = MaterialTheme.colorScheme.surfaceContainer,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    // Standard M3 Role for List Item Primary Text
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                Text(
                    text = subtitle,
                    // Standard M3 Role for List Item Supporting Text
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.m))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(MaterialTheme.icons.small)
            )
        }
    }
}