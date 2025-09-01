package com.yugentech.sessions.notification.scheduled

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yugentech.sessions.notification.model.Notification
import com.yugentech.sessions.notification.datastore.NotificationDataStore
import com.yugentech.sessions.notification.service.NotificationService
import com.yugentech.sessions.notification.model.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

// System broadcast receiver that handles alarm triggers and device reboots
class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationService: NotificationService by inject()
    private val reminderNotificationManager: ReminderNotificationManager by inject()
    private val notificationDataStore: NotificationDataStore by inject()

    // Entry point for broadcast events
    override fun onReceive(context: Context, intent: Intent) {
        // Handle device reboot by checking preferences and restoring alarms
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

        // Extract alarm details from the intent
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Time to study!"
        val hour = intent.getIntExtra(EXTRA_HOUR, 8)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)

        Timber.i("Alarm fired: $message. Showing notification.")

        // Build and display the scheduled notification
        val notification = Notification(
            id = NotificationService.REMINDER_NOTIFICATION_ID,
            type = NotificationType.SCHEDULED,
            title = "Reminder",
            message = message,
            isOngoing = false
        )
        notificationService.showNotification(notification)

        // Automatically schedule the next alarm for the same time tomorrow
        if (reminderNotificationManager.canScheduleExactAlarms()) {
            try {
                reminderNotificationManager.scheduleReminder(message, hour, minute)
            } catch (e: SecurityException) {
                Timber.e(e, "Permission revoked, cannot reschedule next day alarm")
            }
        }
    }

    // Logic to re-register the alarm after a system reboot if enabled
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