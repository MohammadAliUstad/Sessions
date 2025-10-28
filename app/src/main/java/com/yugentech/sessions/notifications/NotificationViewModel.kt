package com.yugentech.sessions.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.dependencyInjection.NotificationPrefsDataStore
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import com.yugentech.sessions.notifications.scheduled.NotificationConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository,
    private val notificationPrefsDataStore: NotificationPrefsDataStore
) : ViewModel() {

    companion object {
        private const val TAG = "NotificationsViewModel"
    }

    // Initialize with actual preferences from DataStore immediately
    // Get notification config from DataStore with proper initial value
    val notificationConfig: StateFlow<NotificationConfig> =
        notificationPrefsDataStore.notificationConfigFlow.stateIn(
            scope = viewModelScope,
            // This ensures the state is kept for 5 seconds after the last subscriber leaves
            started = SharingStarted.WhileSubscribed(5000),
            // Load the initial config synchronously to avoid default values flashing
            initialValue = run {
                runCatching {
                    kotlinx.coroutines.runBlocking {
                        notificationPrefsDataStore.getInitialConfig()
                    }
                }.getOrDefault(NotificationConfig())
            }
        )

    init {
        // Log initial state for debugging
        Log.d(TAG, "Initial notification config: ${notificationConfig.value}")
    }

    fun startActiveSession(notification: Notification) {
        viewModelScope.launch {
            Log.d(TAG, "Starting active session with notification: ${notification.title}")
            // Always show active session notification - it's required for foreground service
            notificationRepository.startActiveSession(notification)
        }
    }

    fun stopActiveSession() {
        viewModelScope.launch {
            Log.d(TAG, "Stopping active session")
            notificationRepository.stopActiveSession()
        }
    }

    // Functions to update notification settings for focus reminders only
    fun setNotificationsEnabled(enabled: Boolean) {
        Log.d(TAG, "Setting focus notifications enabled: $enabled")

        viewModelScope.launch {
            // Save to DataStore
            notificationPrefsDataStore.setNotificationsEnabled(enabled)

            // If disabling notifications, cancel any scheduled reminders
            if (!enabled) {
                cancelAllReminders()
                Log.d(TAG, "Focus notifications disabled, canceling all reminders")
            } else if (notificationConfig.value.focusRemindersEnabled) {
                // If enabling and reminders were enabled, reschedule
                updateReminders()
                Log.d(TAG, "Focus notifications enabled, updating reminders")
            }
        }
    }

    fun setFocusRemindersEnabled(enabled: Boolean) {
        Log.d(TAG, "Setting focus reminders enabled: $enabled")

        viewModelScope.launch {
            // Set focus reminders enabled state
            notificationPrefsDataStore.setFocusRemindersEnabled(enabled)

            if (enabled) {
                // If there's no time set or we're using defaults, set a sensible default time
                val config = notificationConfig.value
                if (config.reminderTimeHour == 8 && config.reminderTimeMinute == 0) {
                    // This is our default time, set a better time like 9 AM
                    notificationPrefsDataStore.setFocusReminderTime(9, 0)
                }

                // Schedule if notifications are enabled
                if (config.notificationsEnabled) {
                    updateReminders()
                }
            } else {
                // Cancel reminders when disabling
                cancelAllReminders()
            }

            Log.d(TAG, "Focus reminders ${if (enabled) "enabled" else "disabled"}")
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        Log.d(TAG, "Setting reminder time to $hour:$minute")

        viewModelScope.launch {
            // Save time to DataStore
            notificationPrefsDataStore.setFocusReminderTime(hour, minute)

            // Also explicitly enable reminders when setting a time
            notificationPrefsDataStore.setFocusRemindersEnabled(true)

            // Update reminders if notifications are enabled
            val config = notificationConfig.value
            if (config.notificationsEnabled) {
                updateReminders()
            }

            Log.d(TAG, "Reminder time set and reminders updated")
        }
    }

    // Format time for display
    fun formatReminderTime(): String {
        val config = notificationConfig.value
        if (!config.focusRemindersEnabled) {
            return "Get notified to start your focus sessions"
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, config.reminderTimeHour)
        calendar.set(Calendar.MINUTE, config.reminderTimeMinute)

        val formattedTime = java.text.SimpleDateFormat("h:mm a", Locale.getDefault())
            .format(calendar.time)

        return "Reminder is set to $formattedTime"
    }

    // Schedule reminder with a specific message and delay
    fun scheduleReminder(message: String, delayMillis: Long) {
        Log.d(TAG, "Scheduling reminder with message: '$message', delay: $delayMillis minutes")
        viewModelScope.launch {
            try {
                notificationRepository.scheduleReminder(message, delayMillis)
                Log.d(TAG, "Reminder scheduled successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to schedule reminder", e)
            }
        }
    }

    // Cancel all scheduled reminders
    fun cancelAllReminders() {
        Log.d(TAG, "Cancelling all reminders")
        viewModelScope.launch {
            try {
                notificationRepository.cancelAllReminders()
                Log.d(TAG, "All reminders cancelled successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel reminders", e)
            }
        }
    }

    // Schedule or cancel reminders based on current notification config
    private fun updateReminders() {
        val config = notificationConfig.value
        Log.d(TAG, "Updating reminders with config: $config")

        if (config.notificationsEnabled && config.focusRemindersEnabled) {
            val delayMillis = calculateDelayMillis(
                config.reminderTimeHour,
                config.reminderTimeMinute
            )

            Log.d(
                TAG,
                "Notifications and focus reminders are enabled. Calculated delay: $delayMillis millis"
            )

            scheduleReminder(
                message = "Focus Reminder",
                delayMillis = delayMillis
            )
        } else {
            Log.d(TAG, "Notifications or focus reminders are disabled. Cancelling all reminders.")
            Log.d(
                TAG,
                "Notifications enabled: ${config.notificationsEnabled}, Focus reminders enabled: ${config.focusRemindersEnabled}"
            )
            cancelAllReminders()
        }
    }

    // Calculate minutes until next reminder time
    // Rename the function to reflect what it actually returns now
    private fun calculateDelayMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        val currentTimeFormatted =
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                .format(calendar.time)

        val targetCalendar = Calendar.getInstance()
        targetCalendar.set(Calendar.HOUR_OF_DAY, hour)
        targetCalendar.set(Calendar.MINUTE, minute)
        targetCalendar.set(Calendar.SECOND, 0)
        targetCalendar.set(Calendar.MILLISECOND, 0)

        val targetTimeFormatted =
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                .format(targetCalendar.time)

        // If the time has already passed today, schedule for tomorrow
        if (targetCalendar.timeInMillis <= currentTime) {
            targetCalendar.add(Calendar.DAY_OF_MONTH, 1)
            Log.d(TAG, "Target time has already passed today, scheduling for tomorrow instead")
        }

        val updatedTargetTimeFormatted =
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                .format(targetCalendar.time)

        val delayMillis = targetCalendar.timeInMillis - currentTime

        Log.d(TAG, "Calculate delay: Current time: $currentTimeFormatted")
        Log.d(TAG, "Calculate delay: Initial target time: $targetTimeFormatted")
        Log.d(TAG, "Calculate delay: Final target time: $updatedTargetTimeFormatted")
        Log.d(TAG, "Calculate delay: Delay in milliseconds: $delayMillis")

        return delayMillis
    }
}