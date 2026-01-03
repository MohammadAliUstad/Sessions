package com.yugentech.sessions.utils

import com.yugentech.sessions.timer.TimerMode

data class HomeUiState(
    // 1. SETTINGS: The rules user selected
    val config: SessionConfig = SessionConfig(),

    // 2. RUNTIME: The live status of the timer
    val status: SessionStatus = SessionStatus(),

    // 3. ALERTS: Any temporary messages
    val errorMessage: String? = null
)

data class SessionConfig(
    val sessionTask: String = "",

    // Durations (Changed Int -> Long)
    val focusDuration: Long = 25 * 60 * 1000L,      // 25 mins
    val shortBreakDuration: Long = 5 * 60 * 1000L,  // 5 mins
    val longBreakDuration: Long = 15 * 60 * 1000L,  // 15 mins

    // Goals
    val targetSets: Int = 4,

    // Behavior
    val autoStartNext: Boolean = false,
    val soundId: String? = null
)

data class SessionStatus(
    val isRunning: Boolean = false,
    val currentMode: TimerMode = TimerMode.Focus,

    // Runtime values (Changed Int -> Long)
    val currentTime: Long = 0L,     // Time remaining (ms)
    val totalTime: Long = 0L,       // Total duration (ms)

    val completedSets: Int = 0
)