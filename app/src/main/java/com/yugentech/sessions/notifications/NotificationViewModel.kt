package com.yugentech.sessions.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    fun startActiveSession(notification: Notification) {
        viewModelScope.launch {
            notificationRepository.startActiveSession(notification)
        }
    }

    fun updateActiveSession(notification: Notification) {
        viewModelScope.launch {
            notificationRepository.updateActiveSession(notification)
        }
    }

    fun stopActiveSession() {
        viewModelScope.launch {
            notificationRepository.stopActiveSession()
        }
    }

    fun scheduleReminder(message: String, delayMinutes: Long) {
        viewModelScope.launch {
            notificationRepository.scheduleReminder(message, delayMinutes)
        }
    }

    fun cancelAllReminders() {
        viewModelScope.launch {
            notificationRepository.cancelAllReminders()
        }
    }
}