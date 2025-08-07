package com.yugentech.sessions.ui.components.profileScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StudyTimeSection(
    formattedTime: String
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer, // M3: surface for highlighted stats
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer, // Sets correct on-color for text
        shape = MaterialTheme.shapes.large,
        tonalElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 28.dp, vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.headlineSmall, // Use headline for important stat
                textAlign = TextAlign.Center
            )
        }
    }
}