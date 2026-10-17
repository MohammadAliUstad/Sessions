package com.yugentech.sessions.notification.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * A restart clears every alarm (and an app update can too); this books the daily reminders again
 * from [ReminderStore]. NudgeWorker needs nothing here: WorkManager keeps its own jobs.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        val scheduler = NotificationScheduler(context)
        ReminderStore.allDaily(context).forEach { daily ->
            scheduler.scheduleDailyReminder(daily.id, daily.hour, daily.minute, daily.title, daily.message)
        }
    }
}
