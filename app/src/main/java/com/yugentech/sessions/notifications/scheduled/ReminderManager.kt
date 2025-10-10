package com.yugentech.sessions.notifications.scheduled

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class ReminderManager(private val context: Context) {

    fun scheduleReminder(
        message: String,
        delayMinutes: Long,
        uniqueWorkName: String = "reminder_work_${System.currentTimeMillis()}"
    ) {
        Log.d(TAG, "Scheduling reminder with message: '$message' for $delayMinutes minutes from now")

        // Create work request
        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(workDataOf(ReminderWorker.KEY_MESSAGE to message))
            .addTag(TAG_REMINDER)
            .build()

        // Enqueue work with a unique name
        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            work
        )

        Log.d(TAG, "Reminder scheduled with work name: $uniqueWorkName")
    }

    fun cancelAllReminders() {
        Log.d(TAG, "Cancelling all reminders")
        WorkManager.getInstance(context).cancelAllWorkByTag(TAG_REMINDER)
    }

    companion object {
        private const val TAG_REMINDER = "reminder_tag"
        private const val TAG = "ReminderManager"
    }
}