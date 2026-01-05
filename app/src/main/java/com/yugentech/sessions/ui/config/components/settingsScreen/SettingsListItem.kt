package com.yugentech.sessions.ui.config.components.settingsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.yugentech.sessions.ui.dash.components.common.itemShape

@Composable
fun SettingsListItem(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    index: Int,
    totalCount: Int,
    onClick: () -> Unit
) {
    val shape = itemShape(index, totalCount)

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = leadingIcon?.let {
            {
                Icon(imageVector = it, contentDescription = null)
            }
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.clip(MaterialTheme.shapes.small)
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