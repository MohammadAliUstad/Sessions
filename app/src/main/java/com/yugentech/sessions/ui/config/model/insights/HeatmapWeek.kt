package com.yugentech.sessions.ui.config.model.insights

data class HeatmapWeek(
    val days: List<HeatmapDay>,
    val firstDayOfMonth: String?
)