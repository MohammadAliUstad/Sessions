package com.yugentech.sessions.utils

import com.yugentech.sessions.timer.TimerMode

data class SessionStatus(
    val isRunning: Boolean = false,
    val currentMode: TimerMode = TimerMode.Focus,
    val currentTime: Long = 0L,
    val totalTime: Long = 0L,
    val completedSets: Int = 0
) {
    val isPaused: Boolean
        get() = !isRunning && totalTime > 0 && currentTime > 0 && currentTime < totalTime

    val isIdle: Boolean
        get() = !isRunning && (totalTime == 0L || currentTime == totalTime)
}