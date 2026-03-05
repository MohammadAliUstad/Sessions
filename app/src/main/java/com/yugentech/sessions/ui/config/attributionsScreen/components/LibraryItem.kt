package com.yugentech.sessions.ui.config.attributionsScreen.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.net.toUri
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.ui.config.model.license.Library

@Composable
fun LibraryItem(
    lib: Library,
    shape: Shape
) {
    val context = LocalContext.current

    ListItem(
        headlineContent = {
            Text(
                text = lib.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        supportingContent = {
            Text(
                text = lib.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.icons.smallMedium),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, lib.url.toUri())
                context.startActivity(intent)
            },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}