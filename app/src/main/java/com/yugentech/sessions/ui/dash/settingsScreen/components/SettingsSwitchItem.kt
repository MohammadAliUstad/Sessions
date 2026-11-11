package com.yugentech.sessions.ui.dash.settingsScreen.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.itemShape

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    enabled: Boolean = true,
    checkedIcon: ImageVector? = null,
    index: Int,
    totalCount: Int,
    onCheckedChange: (Boolean) -> Unit,
    onClick: (() -> Unit)? = null
) {
    val shape = itemShape(index, totalCount)
    val contentAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.45f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "settingsSwitchContentAlpha"
    )

    // The switch sits beside the ListItem rather than in its trailing slot: ListItem pins trailing
    // content to the top once the subtitle wraps to three lines, and this keeps it centred
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .graphicsLayer { alpha = contentAlpha }
            .then(if ((onClick != null) && enabled) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.s,
                    top = MaterialTheme.spacing.s
                )
            )
        },
        supportingContent = subtitle?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        start = MaterialTheme.spacing.s,
                        bottom = MaterialTheme.spacing.s
                    )
                )
            }
        },
        modifier = Modifier.weight(1f),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )

    // ListItem's own end padding gives the same 16dp gap before the switch as before
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = Modifier.padding(end = 16.dp),
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            checkedIconColor = MaterialTheme.colorScheme.primaryContainer,
            uncheckedIconColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledCheckedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledCheckedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledUncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledCheckedIconColor = MaterialTheme.colorScheme.primaryContainer,
            disabledUncheckedIconColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        thumbContent = {
            Icon(
                imageVector = if (checked) checkedIcon ?: Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
            )
        }
    )
    }
}