package com.yugentech.sessions.notifications.notificationRepository

import com.yugentech.sessions.notifications.active.ActiveServiceManager
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.active.ActiveService
import com.yugentech.sessions.notifications.scheduled.ReminderManager

class NotificationRepositoryImpl(
    private val activeService: ActiveService,
    private val activeServiceManager: ActiveServiceManager,
    private val reminderManager: ReminderManager
) : NotificationRepository {

    override fun startActiveSession(notification: Notification) {
        activeServiceManager.startActiveSession(notification)
    }

    override fun updateActiveSession(notification: Notification) {
        activeService.showNotification(notification)
    }

    override fun stopActiveSession() {
        activeServiceManager.stopActiveSession()
    }

    override fun scheduleReminder(message: String, delayMinutes: Long) {
        reminderManager.scheduleReminder(message, delayMinutes)
    }

    override fun cancelAllReminders() {
        reminderManager.cancelAllReminders()
    }
}