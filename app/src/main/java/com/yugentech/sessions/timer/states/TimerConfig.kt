package com.yugentech.sessions.timer.states

import com.yugentech.sessions.utils.AppConstants
import kotlin.math.ceil

// Holds the user's settings for timer durations, goals, and active tasks
data class TimerConfig(
    val focusDuration: Int = 25,
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15,
    val targetSets: Int = 1,
    val sessionTask: String = AppConstants.EMPTY_STRING,
    val activeBackgroundSoundId: String? = null
) {
    // Dynamically calculates how many short breaks occur before a long break
    val setsPerLongBreak: Int
        get() = if (focusDuration <= 0) 1 else ceil(100f / focusDuration).toInt()
}