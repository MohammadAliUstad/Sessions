package com.yugentech.sessions.notification.model

// Data model representing a notification to be displayed
data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isOngoing: Boolean,
    val remainingSeconds: Long? = null,
    val totalSeconds: Int? = null
)