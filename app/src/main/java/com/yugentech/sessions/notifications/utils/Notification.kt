package com.yugentech.sessions.notifications.utils

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isOngoing: Boolean,
    val timeRemainingMinutes: Int? = null,  // For session notifications
    val totalMinutes: Int? = null          // For progress calculation
)

enum class NotificationType {
    ACTIVE_SESSION,
    REMINDER
}