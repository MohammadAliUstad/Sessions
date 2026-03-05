package com.yugentech.sessions.notification.model

// Data class modeling user preferences for notifications and reminder times
data class NotificationConfig(
    val notificationsEnabled: Boolean = true,
    val focusRemindersEnabled: Boolean = false,
    val reminderTimeHour: Int = 8,
    val reminderTimeMinute: Int = 0
)