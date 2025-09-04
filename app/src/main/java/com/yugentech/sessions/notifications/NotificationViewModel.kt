package com.yugentech.sessions.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _notificationPermissionGranted = MutableStateFlow(false)
    val notificationPermissionGranted: StateFlow<Boolean> = _notificationPermissionGranted.asStateFlow()

    private val _exactAlarmPermissionGranted = MutableStateFlow(false)
    val exactAlarmPermissionGranted: StateFlow<Boolean> = _exactAlarmPermissionGranted.asStateFlow()

    init {
        setupNotifications()
        checkPermissions()
    }

    private fun setupNotifications() {
        viewModelScope.launch {
            try {
                notificationRepository.setupNotifications()
                _uiState.value = _uiState.value.copy(isInitialized = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to setup notifications: ${e.message}"
                )
            }
        }
    }

    private fun checkPermissions() {
        _notificationPermissionGranted.value = notificationRepository.hasNotificationPermission()
        _exactAlarmPermissionGranted.value = notificationRepository.canScheduleExactAlarms()
    }

    // Active Session Notifications
    fun showActiveSessionNotification(message: String, timeRemainingMinutes: Int, totalMinutes: Int) {
        if (!_notificationPermissionGranted.value) {
            _uiState.value = _uiState.value.copy(
                error = "Notification permission not granted"
            )
            return
        }

        viewModelScope.launch {
            try {
                notificationRepository.showActiveSessionNotification(message, timeRemainingMinutes, totalMinutes)
                _uiState.value = _uiState.value.copy(
                    isActiveSessionVisible = true,
                    activeSessionMessage = message,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to show active session notification: ${e.message}"
                )
            }
        }
    }

    fun hideActiveSessionNotification() {
        viewModelScope.launch {
            try {
                notificationRepository.hideActiveNotification()
                _uiState.value = _uiState.value.copy(
                    isActiveSessionVisible = false,
                    activeSessionMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to hide active session notification: ${e.message}"
                )
            }
        }
    }

    // Reminder Notifications
    fun showReminderNotification(message: String) {
        if (!_notificationPermissionGranted.value) {
            _uiState.value = _uiState.value.copy(
                error = "Notification permission not granted"
            )
            return
        }

        viewModelScope.launch {
            try {
                notificationRepository.showReminderNotification(message)
                _uiState.value = _uiState.value.copy(error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to show reminder notification: ${e.message}"
                )
            }
        }
    }

    // Add this new method to your NotificationViewModel.kt

    fun updateActiveSessionNotification(timeRemainingMinutes: Int, totalMinutes: Int) {
        if (!_notificationPermissionGranted.value) return
        if (!_uiState.value.isActiveSessionVisible) return

        viewModelScope.launch {
            try {
                notificationRepository.showActiveSessionNotification(
                    message = "Study session active",
                    timeRemainingMinutes = timeRemainingMinutes,
                    totalMinutes = totalMinutes
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update notification: ${e.message}"
                )
            }
        }
    }

    fun scheduleReminder(hour: Int, minute: Int, message: String) {
        if (!_exactAlarmPermissionGranted.value) {
            _uiState.value = _uiState.value.copy(
                error = "Exact alarm permission not granted. Please enable in settings."
            )
            return
        }

        viewModelScope.launch {
            try {
                val success = notificationRepository.scheduleReminder(hour, minute, message)
                if (success) {
                    val scheduledReminder = ScheduledReminder(
                        hour = hour,
                        minute = minute,
                        message = message,
                        isActive = true
                    )
                    val updatedReminders = _uiState.value.scheduledReminders.toMutableList()
                    updatedReminders.removeAll { it.hour == hour && it.minute == minute }
                    updatedReminders.add(scheduledReminder)

                    _uiState.value = _uiState.value.copy(
                        scheduledReminders = updatedReminders,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to schedule reminder. Check permissions."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to schedule reminder: ${e.message}"
                )
            }
        }
    }

    fun cancelReminder(hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                notificationRepository.cancelReminder(hour, minute)
                val updatedReminders = _uiState.value.scheduledReminders.toMutableList()
                updatedReminders.removeAll { it.hour == hour && it.minute == minute }

                _uiState.value = _uiState.value.copy(
                    scheduledReminders = updatedReminders,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to cancel reminder: ${e.message}"
                )
            }
        }
    }

    // General Controls
    fun hideAllNotifications() {
        viewModelScope.launch {
            try {
                notificationRepository.hideAllNotifications()
                _uiState.value = _uiState.value.copy(
                    isActiveSessionVisible = false,
                    activeSessionMessage = null,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to hide all notifications: ${e.message}"
                )
            }
        }
    }

    // Permission Management
    fun updateNotificationPermission(granted: Boolean) {
        _notificationPermissionGranted.value = granted
        if (!granted) {
            _uiState.value = _uiState.value.copy(
                error = "Notification permission is required for notifications to work"
            )
        }
    }

    fun updateExactAlarmPermission(granted: Boolean) {
        _exactAlarmPermissionGranted.value = granted
        if (!granted) {
            _uiState.value = _uiState.value.copy(
                error = "Exact alarm permission is required for scheduled reminders"
            )
        }
    }

    fun refreshPermissions() {
        checkPermissions()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // Convenience methods for common use cases
    fun startSessionNotification(sessionType: String, timeRemainingMinutes: Int, totalMinutes: Int) {
        val message = "$sessionType session active"
        showActiveSessionNotification(message, timeRemainingMinutes, totalMinutes)
    }

    fun endSessionNotification(sessionType: String) {
        hideActiveSessionNotification()
        showReminderNotification("$sessionType session completed!")
    }

    fun setDailyStudyReminder(hour: Int, minute: Int) {
        scheduleReminder(hour, minute, "Time to start your daily study session!")
    }

    fun setBreakReminder(hour: Int, minute: Int) {
        scheduleReminder(hour, minute, "Time for a break!")
    }
}

data class NotificationUiState(
    val isInitialized: Boolean = false,
    val isActiveSessionVisible: Boolean = false,
    val activeSessionMessage: String? = null,
    val scheduledReminders: List<ScheduledReminder> = emptyList(),
    val error: String? = null
)

data class ScheduledReminder(
    val hour: Int,
    val minute: Int,
    val message: String,
    val isActive: Boolean = true
)