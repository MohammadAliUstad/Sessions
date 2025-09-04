package com.yugentech.sessions.notifications

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isOngoing: Boolean,
    val timeRemainingMinutes: Int? = null,
    val totalMinutes: Int? = null
)

enum class NotificationType {
    ACTIVE_SESSION,
    REMINDER
}