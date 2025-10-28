package com.yugentech.sessions.notifications.notificationRepository

import com.yugentech.sessions.notifications.Notification

interface NotificationRepository {
    suspend fun startActiveSession(notification: Notification)
    suspend fun updateActiveSession(notification: Notification)
    suspend fun stopActiveSession()
    suspend fun scheduleReminder(message: String, delayMillis: Long)
    suspend fun cancelAllReminders()
}