package com.yugentech.sessions.notifications.scheduled

// Persisted configuration for user notification preferences and reminder times
data class NotificationConfig(
    val notificationsEnabled: Boolean = true,
    val focusRemindersEnabled: Boolean = false,
    val reminderTimeHour: Int = 8,
    val reminderTimeMinute: Int = 0
)