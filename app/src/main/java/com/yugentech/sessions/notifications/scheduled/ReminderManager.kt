package com.yugentech.sessions.notifications.scheduled

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class ReminderManager(private val context: Context) {

    fun scheduleReminder(message: String, delayMinutes: Long, uniqueWorkName: String = "reminder_work") {
        Log.d(TAG, "Scheduling reminder - WorkName: $uniqueWorkName, Message: '$message', Delay: ${delayMinutes}m")

        try {
            val work = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .setInputData(workDataOf(ReminderWorker.KEY_MESSAGE to message))
                .addTag(TAG_REMINDER)
                .build()

            Log.d(TAG, "Work request created with ID: ${work.id}")

            WorkManager.getInstance(context).enqueueUniqueWork(
                uniqueWorkName,
                ExistingWorkPolicy.REPLACE,
                work
            )

            Log.i(TAG, "Reminder scheduled successfully - WorkName: $uniqueWorkName, Work ID: ${work.id}")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule reminder - WorkName: $uniqueWorkName", e)
        }
    }

    fun cancelReminder(uniqueWorkName: String = "reminder_work") {
        Log.d(TAG, "Cancelling reminder with WorkName: $uniqueWorkName")

        try {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
            Log.i(TAG, "Reminder cancelled successfully - WorkName: $uniqueWorkName")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel reminder - WorkName: $uniqueWorkName", e)
        }
    }

    fun cancelAllReminders() {
        Log.d(TAG, "Cancelling all reminders with tag: $TAG_REMINDER")

        try {
            val operation = WorkManager.getInstance(context).cancelAllWorkByTag(TAG_REMINDER)
            Log.i(TAG, "All reminders cancelled successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel all reminders", e)
        }
    }

    companion object {
        private const val TAG_REMINDER = "reminder_tag"
        private const val TAG = "ReminderManager"
    }
}