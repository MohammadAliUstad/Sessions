package com.yugentech.sessions.notifications

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

// Distinguishes between active timer updates and scheduled future reminders
enum class NotificationType {
    ACTIVE,
    SCHEDULED
}