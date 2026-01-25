package com.yugentech.sessions.notifications.notificationRepository

import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.active.ActiveNotificationManager
import com.yugentech.sessions.notifications.scheduled.ReminderNotificationManager
import timber.log.Timber

// Implementation that delegates notification tasks to specific managers for active vs scheduled types
class NotificationRepositoryImpl(
    private val activeNotificationManager: ActiveNotificationManager,
    private val reminderNotificationManager: ReminderNotificationManager
) : NotificationRepository {

    // Delegates start request to the active manager
    override suspend fun startActiveNotification(notification: Notification) {
        Timber.d("Requesting start of active session: ${notification.title}")
        activeNotificationManager.startActiveNotification(notification)
    }

    // Delegates update request to the active manager
    override suspend fun updateActiveNotification(notification: Notification) {
        Timber.v("Requesting session update: ${notification.remainingSeconds}s remaining")
        activeNotificationManager.updateActiveNotification(notification)
    }

    // Delegates stop request to the active manager
    override suspend fun stopActiveNotification() {
        Timber.d("Requesting stop of active session")
        activeNotificationManager.stopActiveNotification()
    }

    // Delegates scheduling to the reminder manager, with error logging
    override suspend fun scheduleReminder(message: String, hour: Int, minute: Int) {
        try {
            Timber.i("Scheduling reminder '$message' for $hour:$minute")
            reminderNotificationManager.scheduleReminder(
                message = message,
                hour = hour,
                minute = minute
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to schedule reminder")
            throw e
        }
    }

    // Delegates cancellation to the reminder manager
    override suspend fun cancelReminders() {
        try {
            Timber.i("Cancelling all scheduled reminders")
            reminderNotificationManager.cancelReminders()
        } catch (e: Exception) {
            Timber.e(e, "Failed to cancel reminders")
            throw e
        }
    }

    // Checks permission status via the reminder manager
    override fun hasExactAlarmPermission(): Boolean {
        return reminderNotificationManager.canScheduleExactAlarms()
    }
}