package com.yugentech.sessions.notifications.notificationRepository

import android.util.Log
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.active.ActiveManager
import com.yugentech.sessions.notifications.scheduled.ReminderManager

class NotificationRepositoryImpl(
    private val activeManager: ActiveManager,
    private val reminderManager: ReminderManager
) : NotificationRepository {

    companion object {
        private const val TAG = "NotificationRepo"
        // Use a consistent request code for reminders so we can cancel them
        private const val REMINDER_REQUEST_CODE = 1000
    }

    override suspend fun startActiveSession(notification: Notification) {
        activeManager.startActiveSession(notification)
    }

    override suspend fun updateActiveSession(notification: Notification) {
        activeManager.updateActiveSession(notification)
    }

    override suspend fun stopActiveSession() {
        activeManager.stopActiveSession()
    }

    override suspend fun scheduleReminder(message: String, delayMillis: Long) {
        Log.d(TAG, "Scheduling reminder: '$message' in $delayMillis minutes")
        try {
            // Check if exact alarms are allowed (Android 12+)
            if (!reminderManager.canScheduleExactAlarms()) {
                Log.w(TAG, "Exact alarm permission not granted")
                // You might want to throw an exception or handle this differently
                // throw SecurityException("SCHEDULE_EXACT_ALARM permission not granted")
            }

            reminderManager.scheduleReminder(
                message = message,
                delayMillis = delayMillis,
                requestCode = REMINDER_REQUEST_CODE
            )
            Log.d(TAG, "Reminder scheduled successfully with requestCode: $REMINDER_REQUEST_CODE")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule reminder", e)
            throw e
        }
    }

    override suspend fun cancelAllReminders() {
        Log.d(TAG, "Cancelling all reminders")
        try {
            reminderManager.cancelReminder(REMINDER_REQUEST_CODE)
            Log.d(TAG, "All reminders cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel reminders", e)
            throw e
        }
    }
}