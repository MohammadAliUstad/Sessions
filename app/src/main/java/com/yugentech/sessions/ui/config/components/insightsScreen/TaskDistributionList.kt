package com.yugentech.sessions.ui.config.components.insightsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun TaskDistributionList(taskDistribution: Map<String, Int>) {
    val totalSeconds = taskDistribution.values.sum().toFloat()

    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
    ) {
        taskDistribution.toList().sortedByDescending { it.second }.take(5)
            .forEach { (task, duration) ->
                val percentage = (duration / totalSeconds)
                TaskProgressItem(label = task, percentage = percentage)
            }
    }
}