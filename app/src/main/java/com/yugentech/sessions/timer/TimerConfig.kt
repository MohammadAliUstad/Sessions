package com.yugentech.sessions.timer

data class TimerConfig(
    val focusDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val targetSets: Int = 4,
    val setsInterval: Int = 4
)