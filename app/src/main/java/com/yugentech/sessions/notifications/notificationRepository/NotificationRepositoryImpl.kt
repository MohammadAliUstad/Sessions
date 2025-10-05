package com.yugentech.sessions.notifications.notificationRepository

import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.active.ActiveServiceManager
import com.yugentech.sessions.notifications.scheduled.ReminderManager

class NotificationRepositoryImpl(
    private val activeServiceManager: ActiveServiceManager,
    private val reminderManager: ReminderManager
) : NotificationRepository {

    override suspend fun startActiveSession(notification: Notification) {
        activeServiceManager.startActiveSession(notification)
    }

    override suspend fun updateActiveSession(notification: Notification) {
        activeServiceManager.updateActiveSession(notification)
    }

    override suspend fun stopActiveSession() {
        activeServiceManager.stopActiveSession()
    }

    override suspend fun scheduleReminder(message: String, delayMinutes: Long) {
        reminderManager.scheduleReminder(message, delayMinutes)
    }

    override suspend fun cancelAllReminders() {
        reminderManager.cancelAllReminders()
    }
}