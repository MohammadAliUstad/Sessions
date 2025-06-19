package com.yugentech.sessions.templates.model

data class Template(
    val id: Long = 0,
    val name: String,
    val focusDuration: Int,
    val shortBreakDuration: Int,
    val longBreakDuration: Int,
    val targetSets: Int,
    val setsPerLongBreak: Int,
    val longBreakEnabled: Boolean,
    val activeBackgroundSoundId: String?,
    val isAmbientEnabled: Boolean
)
