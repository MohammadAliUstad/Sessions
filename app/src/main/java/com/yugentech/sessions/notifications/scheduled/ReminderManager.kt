package com.yugentech.sessions.notifications.scheduled

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class ReminderManager(private val context: Context) {

    fun scheduleReminder(message: String, delayMinutes: Long) {
        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(workDataOf(ReminderWorker.KEY_MESSAGE to message))
            .build()

        WorkManager.getInstance(context).enqueue(work)
    }

    fun cancelAllReminders() {
        WorkManager.getInstance(context).cancelAllWork()
    }
}