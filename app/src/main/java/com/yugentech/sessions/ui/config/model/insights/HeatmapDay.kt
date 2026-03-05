package com.yugentech.sessions.ui.config.model.insights

import java.time.LocalDate

data class HeatmapDay(
    val date: LocalDate,
    val intensity: Int
)