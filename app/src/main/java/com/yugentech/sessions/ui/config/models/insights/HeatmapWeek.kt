package com.yugentech.sessions.ui.config.models.insights

data class HeatmapWeek(
    val weekIndex: Int,
    val days: List<HeatmapDay>,
    val firstDayOfMonth: String?
)