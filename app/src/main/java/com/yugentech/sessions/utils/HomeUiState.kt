package com.yugentech.sessions.utils

import com.yugentech.sessions.timer.TimerMode
import kotlin.math.ceil

data class HomeUiState(
    val config: SessionConfig = SessionConfig(),
    val status: SessionStatus = SessionStatus(),
    val errorMessage: String? = null
)

data class SessionConfig(
    val sessionTask: String = "",

    // Durations stored simply as MINUTES (Int)
    val focusDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,

    val targetSets: Int = 4,
    val autoStartNext: Boolean = false,
    val soundId: String? = null
) {
    // Logic: Calculates how many sets to finish before a long break.
    // Target is roughly 100 minutes of focus time.
    val setsPerLongBreak: Int
        get() {
            if (focusDurationMinutes <= 0) return 1
            return ceil(100f / focusDurationMinutes).toInt()
        }
}

data class SessionStatus(
    val isRunning: Boolean = false,
    val currentMode: TimerMode = TimerMode.Focus,

    // Runtime values stored as SECONDS (Long)
    val currentTime: Long = 0L,
    val totalTime: Long = 0L,

    val completedSets: Int = 0
) {
    // Logic: Checks if the session is strictly "Paused"
    // (Started, not currently running, and not yet finished)
    val isPaused: Boolean
        get() = !isRunning && totalTime > 0 && currentTime > 0 && currentTime < totalTime

    // Optional: Useful helper to check if the timer is completely stopped/fresh
    val isIdle: Boolean
        get() = !isRunning && (totalTime == 0L || currentTime == totalTime)
}