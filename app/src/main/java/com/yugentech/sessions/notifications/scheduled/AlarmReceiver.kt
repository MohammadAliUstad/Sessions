package com.yugentech.sessions.notifications.scheduled

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yugentech.sessions.notifications.NotificationPrefsDataStore
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationService
import com.yugentech.sessions.notifications.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationService: NotificationService by inject()
    private val reminderManager: ReminderManager by inject()
    // 1. Inject your DataStore
    private val notificationPrefsDataStore: NotificationPrefsDataStore by inject()

    override fun onReceive(context: Context, intent: Intent) {

        // CASE 1: Device Restarted
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 2. Use goAsync() because reading DataStore takes time.
            // Without this, the system kills the Receiver before data loads.
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    rescheduleAfterBoot()
                } finally {
                    // Always finish the pending result so the system knows we are done
                    pendingResult.finish()
                }
            }
            return
        }

        // CASE 2: Normal Alarm Fired (Same as before)
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Time to study!"
        val hour = intent.getIntExtra(EXTRA_HOUR, 8)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)

        val notification = Notification(
            id = NotificationService.REMINDER_NOTIFICATION_ID,
            type = NotificationType.SCHEDULED,
            title = "Reminder",
            message = message,
            isOngoing = false
        )
        notificationService.showNotification(notification)

        if (reminderManager.canScheduleExactAlarms()) {
            try {
                reminderManager.scheduleReminder(message, hour, minute)
            } catch (e: SecurityException) {
                Log.e("AlarmReceiver", "Permission revoked, cannot reschedule", e)
            }
        }
    }

    // 3. Suspend function to read DataStore
    private suspend fun rescheduleAfterBoot() {
        if (reminderManager.canScheduleExactAlarms()) {
            try {
                // Fetch the config directly
                val config = notificationPrefsDataStore.getInitialConfig()

                // Only schedule if the user actually wants reminders
                if (config.focusRemindersEnabled) {
                    Log.d("AlarmReceiver", "Boot completed. Rescheduling for ${config.reminderTimeHour}:${config.reminderTimeMinute}")

                    reminderManager.scheduleReminder(
                        message = "Time to study!", // You can also save this message in DataStore if you want it dynamic
                        hour = config.reminderTimeHour,
                        minute = config.reminderTimeMinute
                    )
                } else {
                    Log.d("AlarmReceiver", "Boot completed but reminders are disabled.")
                }

            } catch (e: SecurityException) {
                Log.e("AlarmReceiver", "Boot reschedule failed: Permission missing")
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Boot reschedule failed: ${e.message}")
            }
        }
    }

    companion object {
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_HOUR = "hour"
        const val EXTRA_MINUTE = "minute"
    }
}