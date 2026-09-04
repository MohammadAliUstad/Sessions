package com.yugentech.sessions.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.notification.datastore.NotificationDataStore
import com.yugentech.sessions.notification.model.NotificationConfig
import com.yugentech.sessions.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
            initialValue = runCatching {
                runBlocking { notificationDataStore.getInitialConfig() }
            }.getOrDefault(NotificationConfig())
        )

    private val _showExactAlarmDialog = MutableStateFlow(false)
    val showExactAlarmDialog = _showExactAlarmDialog.asStateFlow()

    fun dismissDialog() {
        _showExactAlarmDialog.value = false
    }

    // Verifies permissions before allowing the user to enable exact alarms.
    fun canEnableReminders(): Boolean {
        val hasPermission = notificationRepository.hasExactAlarmPermission()
        if (!hasPermission) {
            Timber.w("Exact alarm permission missing, showing dialog")
            _showExactAlarmDialog.value = true
            return false
        }
        return true
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
                if (notificationConfiguration.value.focusRemindersEnabled) {
                    updateReminders()
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
                if (config.reminderTimeHour == 8 && config.reminderTimeMinute == 0) {
                    notificationDataStore.setFocusReminderTime(9, 0)
                }
                if (config.notificationsEnabled) {
                    updateReminders()
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
            val config = notificationConfiguration.value
            if (config.notificationsEnabled) {
                updateReminders()
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
                _showExactAlarmDialog.value = true
                return@launch
            }

            try {
                Timber.i("Scheduling reminder: $message at $hour:$minute")
                notificationRepository.scheduleReminder(message, hour, minute)
            } catch (e: SecurityException) {
                Timber.e(e, "Permission revoked during scheduling")
                _showExactAlarmDialog.value = true
            } catch (e: Exception) {
                Timber.e(e, "Failed to schedule reminder")
                throw e
            }
        }
    }

    // Syncs the system alarm with the current configuration state.
    private fun updateReminders() {
        val config = notificationConfiguration.value
        if (config.notificationsEnabled && config.focusRemindersEnabled) {
            scheduleReminder(
                message = "Focus Reminder",
                hour = config.reminderTimeHour,
                minute = config.reminderTimeMinute
            )
        } else {
            cancelReminders()
        }
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