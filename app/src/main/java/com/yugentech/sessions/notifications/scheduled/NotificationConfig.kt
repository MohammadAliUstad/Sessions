package com.yugentech.sessions.notifications.scheduled

// Data class modeling user preferences for notifications and reminder times
data class NotificationConfig(
    val notificationsEnabled: Boolean = true,
    val focusRemindersEnabled: Boolean = false,
    val reminderTimeHour: Int = 8,
    val reminderTimeMinute: Int = 0
)