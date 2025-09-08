package com.yugentech.sessions.notifications.scheduled

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.notifications.active.ActiveService

class ReminderWorker(
    context: Context,
    params: WorkerParameters,
    private val activeService: ActiveService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "ReminderWorker started")

        val message = inputData.getString(KEY_MESSAGE) ?: "Time to study!"
        Log.d(TAG, "Input message: $message")

        val notification = Notification(
            id = ActiveService.REMINDER_NOTIFICATION_ID,
            type = NotificationType.SCHEDULED,
            title = "Study Reminder",
            message = message,
            isOngoing = false
        )

        Log.d(TAG, "Showing notification with id: ${notification.id} and message: ${notification.message}")
        activeService.showNotification(notification)

        Log.d(TAG, "ReminderWorker finished successfully")
        return Result.success()
    }

    companion object {
        const val KEY_MESSAGE = "message"
        private const val TAG = "ReminderWorker"
    }
}
