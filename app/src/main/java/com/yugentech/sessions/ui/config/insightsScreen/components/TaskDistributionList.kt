package com.yugentech.sessions.ui.config.components.insightsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun TaskDistributionList(
    taskDistribution: Map<String, Int>
) {
    val sortedList = taskDistribution.toList()
        .sortedByDescending { it.second }
        .take(5)

    val totalSeconds = taskDistribution.values.sum().toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        shape = RoundedCornerShape(MaterialTheme.corners.large)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.m),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
        ) {
            sortedList.forEach { (task, duration) ->
                val percentage = if (totalSeconds > 0) duration / totalSeconds else 0f

                val timeText = formatSeconds(duration)
                val displayText = "$timeText (${(percentage * 100).toInt()}%)"

                TaskProgressItem(
                    label = task,
                    progress = percentage,
                    valueText = displayText
                )
            }
        }
    }
}

private fun formatSeconds(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}