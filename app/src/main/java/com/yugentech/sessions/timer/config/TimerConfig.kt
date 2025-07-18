package com.yugentech.sessions.timer.config

import com.yugentech.sessions.utils.AppConstants.EMPTY

// Holds the user's settings for timer durations, goals, and active tasks
data class TimerConfig(
    val focusDuration: Int = 25,
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15,
    val targetSets: Int = 1,
    val setsPerLongBreak: Int = 4,
    val longBreakEnabled: Boolean = true,
    val sessionTask: String = EMPTY,
    val activeBackgroundSoundId: String? = null,
    val isAmbientEnabled: Boolean = true
)