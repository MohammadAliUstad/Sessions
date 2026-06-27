package com.yugentech.sessions.notification.repository

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yugentech.sessions.notification.active.ActiveNotificationManager
import com.yugentech.sessions.notification.scheduled.ScheduledNotificationManager
import com.yugentech.sessions.notification.worker.SmartReminderWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

// Implementation that delegates notification tasks to specific managers for active vs scheduled types
class NotificationRepositoryImpl(
    private val context: Context,
    private val activeNotificationManager: ActiveNotificationManager,
    private val scheduledNotificationManager: ScheduledNotificationManager
) : NotificationRepository {

    companion object {
        private const val SMART_REMINDER_WORK_NAME = "smart_reminder_work"
    }

    // Fires the intent to start ActiveForeground. The service reads all timer state
    // directly from TimerRepository on its own — no data needs to be passed here.
    override fun startActiveNotification() {
        Timber.d("Requesting start of active session notification")
        activeNotificationManager.startActiveNotification()
    }

    // Fires the intent to stop ActiveForeground and remove the notification.
    override fun stopActiveNotification() {
        Timber.d("Requesting stop of active session notification")
        activeNotificationManager.stopActiveNotification()
    }

    override fun finishActiveNotification() {
        Timber.d("Requesting finish of active session notification")
        activeNotificationManager.finishActiveNotification()
    }

    // Delegates scheduling to the reminder manager, with error logging.
    override suspend fun scheduleReminder(message: String, hour: Int, minute: Int) {
        try {
            Timber.i("Scheduling reminder '$message' for $hour:$minute")
            scheduledNotificationManager.scheduleReminder(
                message = message,
                hour = hour,
                minute = minute
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to schedule reminder")
            throw e
        }
    }

    // Delegates cancellation to the reminder manager.
    override suspend fun cancelReminders() {
        try {
            Timber.i("Cancelling all scheduled reminders")
            scheduledNotificationManager.cancelReminders()
        } catch (e: Exception) {
            Timber.e(e, "Failed to cancel reminders")
            throw e
        }
    }

    // Checks permission status via the reminder manager.
    override fun hasExactAlarmPermission(): Boolean {
        return scheduledNotificationManager.canScheduleExactAlarms()
    }

    override fun scheduleSmartReminders() {
        Timber.i("Scheduling smart reminders via WorkManager")
        val workRequest = PeriodicWorkRequestBuilder<SmartReminderWorker>(
            12, TimeUnit.HOURS // Run twice a day to check conditions
        ).setInitialDelay(6, TimeUnit.HOURS)
            .addTag(SMART_REMINDER_WORK_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SMART_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing to avoid resetting interval
            workRequest
        )
    }

    override fun cancelSmartReminders() {
        Timber.i("Cancelling smart reminders")
        WorkManager.getInstance(context).cancelUniqueWork(SMART_REMINDER_WORK_NAME)
    }
}