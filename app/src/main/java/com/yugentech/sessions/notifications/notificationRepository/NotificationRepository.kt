package com.yugentech.sessions.notifications.notificationRepository

interface NotificationRepository {
    fun setupNotifications()
    fun showActiveSessionNotification(message: String, timeRemainingMinutes: Int, totalMinutes: Int)
    fun showReminderNotification(message: String)
    fun scheduleReminder(hour: Int, minute: Int, message: String): Boolean
    fun cancelReminder(hour: Int, minute: Int)
    fun hideActiveNotification()
    fun hideReminderNotification()
    fun hideAllNotifications()
    fun hasNotificationPermission(): Boolean
    fun canScheduleExactAlarms(): Boolean
}