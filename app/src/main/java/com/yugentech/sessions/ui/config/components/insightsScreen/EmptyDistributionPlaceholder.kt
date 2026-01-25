package com.yugentech.sessions.ui.config.components.insightsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners

@Composable
fun EmptyDistributionPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.components.controlBarItemWidthWide)
            .background(
                MaterialTheme.colorScheme.surfaceContainerLow,
                RoundedCornerShape(MaterialTheme.corners.medium)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Complete a session to see distribution",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}