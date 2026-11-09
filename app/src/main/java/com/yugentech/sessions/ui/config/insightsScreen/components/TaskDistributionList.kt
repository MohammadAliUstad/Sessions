package com.yugentech.sessions.ui.config.insightsScreen.components

import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.remember
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
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
                val displayText = stringResource(R.string.insights_time_percent, timeText, (percentage * 100).toInt())

                TaskProgressItem(
                    label = task,
                    progress = percentage,
                    valueText = displayText
                )
            }
        }
    }
}

@Composable
private fun formatSeconds(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) {
        stringResource(R.string.duration_hours_minutes_short, hours, minutes)
    } else {
        stringResource(R.string.duration_minutes_short, minutes)
    }
}