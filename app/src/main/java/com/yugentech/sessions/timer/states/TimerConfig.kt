package com.yugentech.sessions.timer.states

import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import kotlin.math.ceil

data class TimerConfig(
    val focusDuration: Int = 25,
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15,
    val targetSets: Int = 1,
    val sessionTask: String = AppConstants.EMPTY_STRING,
    val activeBackgroundSoundId: String? = null
) {
    val setsPerLongBreak: Int
        get() = if (focusDuration <= 0) 1 else ceil(100f / focusDuration).toInt()
}