package com.yugentech.sessions.timer

data class TimerConfig(
    val focusDuration: Long = 25 * 60 * 1000L,
    val shortBreakDuration: Long = 5 * 60 * 1000L,
    val longBreakDuration: Long = 15 * 60 * 1000L,
    val targetSets: Int = 4,
    val setsInterval: Int = 4
)