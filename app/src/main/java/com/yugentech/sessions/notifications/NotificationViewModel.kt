package com.yugentech.sessions.notifications

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

    val notificationConfig: StateFlow<NotificationConfig> =
        notificationPrefsDataStore.notificationConfigFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = run {
                runCatching {
                    kotlinx.coroutines.runBlocking {
                        notificationPrefsDataStore.getInitialConfig()
                    }
                }.getOrDefault(NotificationConfig())
            }
        )

    fun startActiveSession(notification: Notification) {
        viewModelScope.launch {
            notificationRepository.startActiveSession(notification)
        }
    }

    fun stopActiveSession() {
        viewModelScope.launch {
            notificationRepository.stopActiveSession()
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            notificationPrefsDataStore.setNotificationsEnabled(enabled)
            if (!enabled) {
                cancelReminders()
            } else if (notificationConfig.value.focusRemindersEnabled) {
                updateReminders()
            }
        }
    }

    fun setFocusRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            notificationPrefsDataStore.setFocusRemindersEnabled(enabled)
            if (enabled) {
                val config = notificationConfig.value
                if (config.reminderTimeHour == 8 && config.reminderTimeMinute == 0) {
                    notificationPrefsDataStore.setFocusReminderTime(9, 0)
                }
                if (config.notificationsEnabled) {
                    updateReminders()
                }
            } else {
                cancelReminders()
            }
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            notificationPrefsDataStore.setFocusReminderTime(hour, minute)
            notificationPrefsDataStore.setFocusRemindersEnabled(true)
            val config = notificationConfig.value
            if (config.notificationsEnabled) {
                updateReminders()
            }
        }
    }

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

    fun scheduleReminder(message: String, hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                notificationRepository.scheduleReminder(message, hour, minute)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun cancelReminders() {
        viewModelScope.launch {
            try {
                notificationRepository.cancelReminders()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun updateReminders() {
        val config = notificationConfig.value

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

    private fun calculateDelayMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        val targetCalendar = Calendar.getInstance()
        targetCalendar.set(Calendar.HOUR_OF_DAY, hour)
        targetCalendar.set(Calendar.MINUTE, minute)
        targetCalendar.set(Calendar.SECOND, 0)
        targetCalendar.set(Calendar.MILLISECOND, 0)

        if (targetCalendar.timeInMillis <= currentTime) {
            targetCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val delayMillis = targetCalendar.timeInMillis - currentTime
        return delayMillis
    }
}