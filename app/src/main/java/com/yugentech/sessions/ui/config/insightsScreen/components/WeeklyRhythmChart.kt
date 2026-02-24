package com.yugentech.sessions.ui.config.insightsScreen.components

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
fun WeeklyRhythmChart(
    dailyVolume: Map<Int, Int>
) {
    val days = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val totalVolume = dailyVolume.values.sum().toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.corners.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.m),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
        ) {
            days.forEachIndexed { index, dayName ->
                val durationSeconds = dailyVolume[index + 1] ?: 0
                val percentage = if (totalVolume > 0) durationSeconds / totalVolume else 0f
                val timeText = formatSeconds(durationSeconds)
                val percentageText = "(${(percentage * 100).toInt()}%)"
                val displayText = "$timeText $percentageText"

                TaskProgressItem(
                    label = dayName,
                    progress = percentage,
                    valueText = displayText
                )
            }
        }
    }
}

private fun formatSeconds(seconds: Int): String {
    if (seconds <= 0) return "0m"
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}