package com.yugentech.sessions.notifications.scheduled

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.notifications.active.ActiveService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    // Properly inject ActiveService using Koin
    private val activeService: ActiveService by inject()

    override suspend fun doWork(): Result {
        Log.d(TAG, "ReminderWorker started")

        val message = inputData.getString(KEY_MESSAGE) ?: "Time to study!"
        Log.d(TAG, "Input message: $message")

        val notification = Notification(
            id = ActiveService.REMINDER_NOTIFICATION_ID,
            type = NotificationType.SCHEDULED,
            title = "Reminder",
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