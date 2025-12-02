package com.yugentech.sessions.notifications.notificationRepository

import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.active.ActiveManager
import com.yugentech.sessions.notifications.scheduled.ReminderManager

class NotificationRepositoryImpl(
    private val activeManager: ActiveManager,
    private val reminderManager: ReminderManager
) : NotificationRepository {

    override suspend fun startActiveSession(notification: Notification) {
        activeManager.startActiveSession(notification)
    }

    override suspend fun updateActiveSession(notification: Notification) {
        activeManager.updateActiveSession(notification)
    }

    override suspend fun stopActiveSession() {
        activeManager.stopActiveSession()
    }

    override suspend fun scheduleReminder(message: String, hour: Int, minute: Int) {
        try {
            reminderManager.scheduleReminder(
                message = message,
                hour = hour,
                minute = minute
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun cancelReminders() {
        try {
            reminderManager.cancelReminders()
        } catch (e: Exception) {
            throw e
        }
    }
}