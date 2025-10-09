package com.yugentech.sessions.notifications.scheduled

data class NotificationConfig(
    val notificationsEnabled: Boolean = true,
    val focusRemindersEnabled: Boolean = false,
    val reminderTimeHour: Int = 8,
    val reminderTimeMinute: Int = 0
)