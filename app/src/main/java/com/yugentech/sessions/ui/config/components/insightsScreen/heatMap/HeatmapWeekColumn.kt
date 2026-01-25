package com.yugentech.sessions.ui.config.components.insightsScreen.heatMap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.ui.config.models.insights.HeatmapDay


@Composable
fun HeatmapWeekColumn(
    days: List<HeatmapDay>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(7) { dayIndex ->
            val day = days.find { it.date.dayOfWeek.value == dayIndex + 1 }

            if (day != null) {
                HeatmapCell(
                    intensity = day.intensity,
                    dayOfMonth = day.date.dayOfMonth
                )
            } else {
                HeatmapCell(intensity = -1)
            }
        }
    }
}