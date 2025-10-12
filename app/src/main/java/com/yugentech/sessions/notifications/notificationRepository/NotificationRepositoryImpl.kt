package com.yugentech.sessions.notifications.notificationRepository

import android.util.Log
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.active.ActiveServiceManager
import com.yugentech.sessions.notifications.scheduled.ReminderManager

class NotificationRepositoryImpl(
    private val activeServiceManager: ActiveServiceManager,
    private val reminderManager: ReminderManager
) : NotificationRepository {

    companion object {
        private const val TAG = "NotificationRepo"
    }

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
        Log.d(TAG, "Scheduling reminder: '$message' in $delayMinutes minutes")
        try {
            // Create a unique work name using a consistent pattern
            // This helps with cancellation later
            val uniqueWorkName = "focus_reminder"
            reminderManager.scheduleReminder(message, delayMinutes, uniqueWorkName)
            Log.d(TAG, "Reminder scheduled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule reminder", e)
            throw e
        }
    }

    override suspend fun cancelAllReminders() {
        Log.d(TAG, "Cancelling all reminders")
        try {
            reminderManager.cancelAllReminders()
            Log.d(TAG, "All reminders cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel reminders", e)
            throw e
        }
    }
}