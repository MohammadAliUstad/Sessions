package com.yugentech.sessions.ui.dash.states

import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import kotlin.math.ceil

data class SessionConfig(
    val sessionTask: String = AppConstants.EMPTY_STRING,
    val focusDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val targetSets: Int = 4,
    val autoStartNext: Boolean = false,
    val activeBackgroundSoundId: String? = null
) {
    val setsPerLongBreak: Int
        get() {
            if (focusDurationMinutes <= 0) return 1
            return ceil(100f / focusDurationMinutes).toInt()
        }
}