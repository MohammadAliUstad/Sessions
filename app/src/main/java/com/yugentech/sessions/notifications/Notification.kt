package com.yugentech.sessions.notifications

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isOngoing: Boolean,
    val remainingSeconds: Long? = null,
    val totalSeconds: Int? = null
)

enum class NotificationType {
    ACTIVE,
    SCHEDULED
}