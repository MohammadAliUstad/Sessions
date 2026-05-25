package com.yugentech.sessions.notification.model

import com.yugentech.sessions.timer.state.TimerMode

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isOngoing: Boolean,
    val remainingSeconds: Long? = null,
    val totalSeconds: Long? = null,
    val completedSets: Int? = null,
    val totalSets: Int? = null,
    val mode: TimerMode? = null,
    val setsPerLongBreak: Int? = null
)