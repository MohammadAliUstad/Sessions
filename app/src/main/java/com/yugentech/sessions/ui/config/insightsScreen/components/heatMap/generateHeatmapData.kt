package com.yugentech.sessions.ui.config.insightsScreen.components.heatMap

import com.yugentech.sessions.ui.config.model.insights.HeatmapDay
import com.yugentech.sessions.ui.config.model.insights.HeatmapWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun generateHeatmapData(
    activeDates: Map<LocalDate, Int>
): List<HeatmapWeek> {
    val endDate = LocalDate.now()
    val startDate = endDate.minusWeeks(52).minusDays(endDate.dayOfWeek.value.toLong() - 1)

    val weeks = mutableListOf<HeatmapWeek>()
    val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)

    var currentWeekIndex = 0
    var currentWeekDays = mutableListOf<HeatmapDay>()

    for (i in 0..daysBetween) {
        val date = startDate.plusDays(i)
        val intensity = activeDates[date] ?: 0
        currentWeekDays.add(HeatmapDay(date, intensity))

        if (currentWeekDays.size == 7 || i == daysBetween) {
            val firstDayOfMonthLabel = currentWeekDays.firstOrNull { it.date.dayOfMonth == 1 }
                ?.date?.format(DateTimeFormatter.ofPattern("MMM"))

            weeks.add(
                HeatmapWeek(
                    currentWeekIndex,
                    ArrayList(currentWeekDays),
                    firstDayOfMonthLabel
                )
            )

            currentWeekIndex++
            currentWeekDays = mutableListOf()
        }
    }
    return weeks
}