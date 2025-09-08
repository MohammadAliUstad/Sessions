package com.yugentech.sessions.notifications.notificationRepository

import com.yugentech.sessions.notifications.Notification

interface NotificationRepository {
    fun startActiveSession(notification: Notification)
    fun updateActiveSession(notification: Notification)
    fun stopActiveSession()
    fun scheduleReminder(message: String, delayMinutes: Long)
    fun cancelAllReminders()
}