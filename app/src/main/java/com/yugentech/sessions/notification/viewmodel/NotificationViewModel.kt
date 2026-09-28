package com.yugentech.sessions.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.notification.datastore.NotificationDataStore
import com.yugentech.sessions.notification.repository.NotificationRepository
import com.yugentech.sessions.notification.model.NotificationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Manages UI state for notification settings and handles user interactions.
// Active session notifications (start/stop) are owned by TimerViewModel — not here.
class NotificationsViewModel(
    private val notificationRepository: NotificationRepository,
    private val notificationDataStore: NotificationDataStore
) : ViewModel() {

    // Exposes current notification preferences as a hot state flow.
    val notificationConfiguration: StateFlow<NotificationConfig> =
        notificationDataStore.notificationConfigFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotificationConfig()
        )

    private val _showExactAlarmDialog = MutableStateFlow(false)
    val showExactAlarmDialog = _showExactAlarmDialog.asStateFlow()

    // Set when the user is sent to system settings for the exact-alarm permission, so that
    // coming back with it granted can finish what they were doing instead of making them
    // tap the reminder toggle again.
    private var awaitingExactAlarmPermission = false

    fun dismissDialog() {
        _showExactAlarmDialog.value = false
    }

    // User backed out of the permission dialog without going to settings.
    fun cancelPermissionRequest() {
        awaitingExactAlarmPermission = false
        _showExactAlarmDialog.value = false
    }

    // Verifies permissions before allowing the user to enable exact alarms.
    fun canEnableReminders(): Boolean {
        val hasPermission = notificationRepository.hasExactAlarmPermission()
        if (!hasPermission) {
            Timber.w("Exact alarm permission missing, showing dialog")
            requestExactAlarmPermission()
            return false
        }
        return true
    }

    // Called when the screen resumes. Returns true if the user just came back from granting
    // the exact-alarm permission while trying to turn reminders on, meaning the time picker
    // should open to finish that. If reminders were already on (the alarm just couldn't be
    // scheduled), they're rescheduled here directly instead.
    fun onReturnedFromSettings(): Boolean {
        if (!awaitingExactAlarmPermission || !notificationRepository.hasExactAlarmPermission()) return false
        awaitingExactAlarmPermission = false

        val config = notificationConfiguration.value
        if (config.notificationsEnabled && config.focusRemindersEnabled) {
            updateReminders(config.reminderTimeHour, config.reminderTimeMinute)
            return false
        }
        return true
    }

    private fun requestExactAlarmPermission() {
        awaitingExactAlarmPermission = true
        _showExactAlarmDialog.value = true
    }

    // Toggles global notifications and syncs scheduled alarms accordingly.
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            Timber.i("User toggled notifications enabled: $enabled")
            notificationDataStore.setNotificationsEnabled(enabled)
            if (!enabled) {
                cancelReminders()
                notificationRepository.cancelSmartReminders()
            } else {
                val config = notificationConfiguration.value
                if (config.focusRemindersEnabled) {
                    updateReminders(config.reminderTimeHour, config.reminderTimeMinute)
                }
                if (notificationConfiguration.value.smartRemindersEnabled) {
                    notificationRepository.scheduleSmartReminders()
                }
            }
        }
    }

    // Toggles focus reminders and ensures a valid time is set.
    fun setFocusRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            Timber.i("User toggled focus reminders enabled: $enabled")
            notificationDataStore.setFocusRemindersEnabled(enabled)
            if (enabled) {
                val config = notificationConfiguration.value
                var hour = config.reminderTimeHour
                var minute = config.reminderTimeMinute
                if (hour == 8 && minute == 0) {
                    hour = 9
                    minute = 0
                    notificationDataStore.setFocusReminderTime(hour, minute)
                }
                if (config.notificationsEnabled) {
                    updateReminders(hour, minute)
                }
            } else {
                cancelReminders()
            }
        }
    }

    // Updates the reminder time preference and reschedules the alarm.
    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            Timber.d("User updated reminder time: $hour:$minute")
            notificationDataStore.setFocusReminderTime(hour, minute)
            notificationDataStore.setFocusRemindersEnabled(true)
            // Schedule with the time just picked, not notificationConfiguration.value -- the
            // saved time only reaches that StateFlow after DataStore emits, so reading it back
            // here can still return the previous time.
            if (notificationConfiguration.value.notificationsEnabled) {
                updateReminders(hour, minute)
            }
        }
    }

    // Formats the selected reminder time for display in the UI.
    fun formatReminderTime(): String {
        val config = notificationConfiguration.value
        if (!config.focusRemindersEnabled) {
            return "Get notified to start your focus sessions"
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, config.reminderTimeHour)
            set(Calendar.MINUTE, config.reminderTimeMinute)
        }

        val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
        return "Reminder is set to $formattedTime"
    }

    // Toggles the smart random reminders feature
    fun setSmartRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            Timber.i("User toggled smart reminders enabled: $enabled")
            notificationDataStore.setSmartRemindersEnabled(enabled)
            if (enabled && notificationConfiguration.value.notificationsEnabled) {
                notificationRepository.scheduleSmartReminders()
            } else {
                notificationRepository.cancelSmartReminders()
            }
        }
    }

    // Schedules a new alarm via the repository, handling permission errors.
    fun scheduleReminder(message: String, hour: Int, minute: Int) {
        viewModelScope.launch {
            if (!notificationRepository.hasExactAlarmPermission()) {
                Timber.w("Cannot schedule: Permission revoked")
                requestExactAlarmPermission()
                return@launch
            }

            try {
                Timber.i("Scheduling reminder: $message at $hour:$minute")
                notificationRepository.scheduleReminder(message, hour, minute)
            } catch (e: SecurityException) {
                Timber.e(e, "Permission revoked during scheduling")
                requestExactAlarmPermission()
            } catch (e: Exception) {
                Timber.e(e, "Failed to schedule reminder")
                throw e
            }
        }
    }

    // Schedules the daily focus reminder at the given time. Callers pass the time and have
    // already checked the on/off switches themselves: re-reading them here from
    // notificationConfiguration.value could see values from before the caller's own write
    // (e.g. reminders still "off" right after turning them on) and cancel the alarm instead.
    private fun updateReminders(hour: Int, minute: Int) {
        scheduleReminder(
            message = "Focus Reminder",
            hour = hour,
            minute = minute
        )
    }

    fun cancelReminders() {
        viewModelScope.launch {
            try {
                Timber.i("Cancelling reminders")
                notificationRepository.cancelReminders()
            } catch (e: Exception) {
                Timber.e(e, "Failed to cancel reminders")
                throw e
            }
        }
    }
}