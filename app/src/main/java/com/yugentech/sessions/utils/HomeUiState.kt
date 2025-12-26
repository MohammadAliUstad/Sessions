package com.yugentech.sessions.utils

import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.timer.TimerConfig
import com.yugentech.sessions.timer.TimerMode

data class HomeUiState(
    val isRunning: Boolean = false,
    val selectedDuration: Int = AppConstants.ZERO,
    val currentTime: Int = AppConstants.ZERO,
    val errorMessage: String? = null,
    val timerMode: TimerMode = TimerMode.Focus, // You need to import TimerMode
    val timerConfig: TimerConfig = TimerConfig(),
    val completedRounds: Int = 0,
    val sessionTask: String = ""
)