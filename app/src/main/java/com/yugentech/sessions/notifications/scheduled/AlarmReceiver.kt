package com.yugentech.sessions.notifications.scheduled

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationDataStore
import com.yugentech.sessions.notifications.NotificationService
import com.yugentech.sessions.notifications.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

// BroadcastReceiver handling exact alarms and device boot events
class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationService: NotificationService by inject()
    private val reminderNotificationManager: ReminderNotificationManager by inject()
    private val notificationDataStore: NotificationDataStore by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    rescheduleAfterBoot()
                } finally {
                    pendingResult.finish()
                }
            }
            return
        }

        // Handle scheduled alarm execution
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Time to study!"
        val hour = intent.getIntExtra(EXTRA_HOUR, 8)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)

        Timber.i("Alarm fired: $message. Showing notification.")

        val notification = Notification(
            id = NotificationService.REMINDER_NOTIFICATION_ID,
            type = NotificationType.SCHEDULED,
            title = "Reminder",
            message = message,
            isOngoing = false
        )
        notificationService.showNotification(notification)

        // Reschedule the alarm for the next day
        if (reminderNotificationManager.canScheduleExactAlarms()) {
            try {
                reminderNotificationManager.scheduleReminder(message, hour, minute)
            } catch (e: SecurityException) {
                Timber.e(e, "Permission revoked, cannot reschedule next day alarm")
            }
        }
    }

    private suspend fun rescheduleAfterBoot() {
        if (reminderNotificationManager.canScheduleExactAlarms()) {
            try {
                val config = notificationDataStore.getInitialConfig()

                if (config.focusRemindersEnabled) {
                    Timber.d("Boot completed. Rescheduling for ${config.reminderTimeHour}:${config.reminderTimeMinute}")

                    reminderNotificationManager.scheduleReminder(
                        message = "Time to study!",
                        hour = config.reminderTimeHour,
                        minute = config.reminderTimeMinute
                    )
                } else {
                    Timber.d("Boot completed but reminders are disabled.")
                }

            } catch (e: SecurityException) {
                Timber.e(e, "Boot reschedule failed: Permission missing")
            } catch (e: Exception) {
                Timber.e(e, "Boot reschedule failed")
            }
        }
    }

    companion object {
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_HOUR = "hour"
        const val EXTRA_MINUTE = "minute"
    }
}