package com.yugentech.sessions.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun TimerDisplay(progress: Float, time: Int) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(280.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 16.dp
        )
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(280.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 16.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format(Locale.US, "%02d:%02d", time / 60, time % 60),
                style = MaterialTheme.typography.displayLarge
            )
            Text("minutes remaining", style = MaterialTheme.typography.bodyLarge)
        }
    }
}