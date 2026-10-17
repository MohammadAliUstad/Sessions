package com.yugentech.sessions.notification.reminders

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/** Runs every hour while the playful reminders are on, and sends a nudge when one is due. */
class NudgeWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val reminders: ReminderManager by inject()

    override suspend fun doWork(): Result {
        reminders.sendNudgeIfDue()
        return Result.success()
    }
}
