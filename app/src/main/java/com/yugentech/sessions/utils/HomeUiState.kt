package com.yugentech.sessions.utils

import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.timer.TimerMode

data class HomeUiState(
    val config: SessionConfig = SessionConfig(),
    val status: SessionStatus = SessionStatus(),
    val errorMessage: String? = null
)

// The Setup: Settings the user configures before starting
data class SessionConfig(
    val sessionTask: String = AppConstants.EMPTY_STRING,           // The task name (e.g. "Math")
    val focusDuration: Int = 25 * 60,       // In seconds
    val shortBreakDuration: Int = 5 * 60,   // In seconds
    val targetSets: Int = 4,                // Goal number of sets
    val soundId: Int = AppConstants.ZERO                    // Background sound selection
)

// The Runtime: Live data tracking the active timer
data class SessionStatus(
    val isRunning: Boolean = false,
    val currentMode: TimerMode = TimerMode.Focus,
    val currentTime: Int = AppConstants.ZERO,   // Current countdown
    val totalTime: Int = AppConstants.ZERO,     // Total time (for progress calculation)
    val completedSets: Int = AppConstants.ZERO                  // Current progress towards goal
)