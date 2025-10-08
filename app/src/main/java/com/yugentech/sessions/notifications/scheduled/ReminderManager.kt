package com.yugentech.sessions.notifications.scheduled

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class ReminderManager(private val context: Context) {

    fun scheduleReminder(
        message: String,
        delayMinutes: Long,
        uniqueWorkName: String = "reminder_work"
    ) {
        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(workDataOf(ReminderWorker.KEY_MESSAGE to message))
            .addTag(TAG_REMINDER)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            work
        )
    }

    fun cancelReminder(uniqueWorkName: String = "reminder_work") {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
    }

    fun cancelAllReminders() {
        val operation = WorkManager.getInstance(context).cancelAllWorkByTag(TAG_REMINDER)

    }

    companion object {
        private const val TAG_REMINDER = "reminder_tag"
    }
}