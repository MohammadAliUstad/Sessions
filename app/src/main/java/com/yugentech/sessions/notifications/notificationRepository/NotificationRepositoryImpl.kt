package com.yugentech.sessions.notifications.notificationRepository

import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.active.ActiveManager
import com.yugentech.sessions.notifications.scheduled.ReminderManager
import timber.log.Timber

class NotificationRepositoryImpl(
    private val activeManager: ActiveManager,
    private val reminderManager: ReminderManager
) : NotificationRepository {

    override suspend fun startActiveSession(notification: Notification) {
        Timber.d("Requesting start of active session: ${notification.title}")
        activeManager.startActiveSession(notification)
    }

    override suspend fun updateActiveSession(notification: Notification) {
        Timber.v("Requesting session update: ${notification.remainingSeconds}s remaining")
        activeManager.updateActiveSession(notification)
    }

    override suspend fun stopActiveSession() {
        Timber.d("Requesting stop of active session")
        activeManager.stopActiveSession()
    }

    override suspend fun scheduleReminder(message: String, hour: Int, minute: Int) {
        try {
            Timber.i("Scheduling reminder '$message' for $hour:$minute")
            reminderManager.scheduleReminder(
                message = message,
                hour = hour,
                minute = minute
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to schedule reminder")
            throw e
        }
    }

    override suspend fun cancelReminders() {
        try {
            Timber.i("Cancelling all scheduled reminders")
            reminderManager.cancelReminders()
        } catch (e: Exception) {
            Timber.e(e, "Failed to cancel reminders")
            throw e
        }
    }

    override fun hasExactAlarmPermission(): Boolean {
        return reminderManager.canScheduleExactAlarms()
    }
}