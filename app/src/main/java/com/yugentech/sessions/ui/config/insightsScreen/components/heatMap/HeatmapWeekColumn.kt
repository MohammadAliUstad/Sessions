package com.yugentech.sessions.ui.config.insightsScreen.components.heatMap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.ui.config.model.insights.HeatmapDay
import java.time.LocalDate

@Composable
fun HeatmapWeekColumn(
    days: List<HeatmapDay>,
    selectedDate: LocalDate? = null,
    onDayClick: (HeatmapDay) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(7) { dayIndex ->
            val day = days.find { it.date.dayOfWeek.value == dayIndex + 1 }

            if (day != null) {
                val isSelected = day.date == selectedDate

                Box {
                    HeatmapCell(
                        intensity = day.intensity,
                        dayOfMonth = day.date.dayOfMonth,
                        isSelected = isSelected,
                        onClick = { onDayClick(day) }
                    )

                    if (isSelected) {
                        HeatmapDayTooltip(day)
                    }
                }
            } else {
                HeatmapCell(intensity = -1)
            }
        }
    }
}
