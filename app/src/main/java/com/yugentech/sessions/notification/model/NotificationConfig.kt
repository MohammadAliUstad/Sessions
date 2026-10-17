package com.yugentech.sessions.notification.model

/** The reminder settings, the same in Quill and Sessions. */
data class NotificationConfig(
    val notificationsEnabled: Boolean = true,
    val dailyReminderEnabled: Boolean = false,
    val playfulRemindersEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0
)
