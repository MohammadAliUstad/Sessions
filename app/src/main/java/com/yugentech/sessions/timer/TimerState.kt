package com.yugentech.sessions.timer

data class TimerState(
    val currentTime: Long = 0L,
    val totalTime: Long = 0L,
    val isTimerRunning: Boolean = false,
    val currentMode: TimerMode = TimerMode.Focus,
    val completedSets: Int = 0,
    val config: TimerConfig = TimerConfig()
)