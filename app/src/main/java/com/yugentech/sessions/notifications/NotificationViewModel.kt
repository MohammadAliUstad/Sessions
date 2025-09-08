package com.yugentech.sessions.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    companion object {
        private const val TAG = "NotificationsViewModel"
    }

    fun startActiveSession(notification: Notification) {
        Log.d(TAG, "startActiveSession called with notification: $notification")
        viewModelScope.launch {
            notificationRepository.startActiveSession(notification)
            Log.d(TAG, "startActiveSession completed for notification: ${notification.id}")
        }
    }

    fun updateActiveSession(notification: Notification) {
        Log.d(TAG, "updateActiveSession called with notification: $notification")
        viewModelScope.launch {
            notificationRepository.updateActiveSession(notification)
            Log.d(TAG, "updateActiveSession completed for notification: ${notification.id}")
        }
    }

    fun stopActiveSession() {
        Log.d(TAG, "stopActiveSession called")
        viewModelScope.launch {
            notificationRepository.stopActiveSession()
            Log.d(TAG, "stopActiveSession completed")
        }
    }

    fun scheduleReminder(message: String, delayMinutes: Long) {
        Log.d(TAG, "scheduleReminder called with message: \"$message\", delayMinutes: $delayMinutes")
        viewModelScope.launch {
            notificationRepository.scheduleReminder(message, delayMinutes)
            Log.d(TAG, "scheduleReminder completed for message: \"$message\"")
        }
    }

    fun cancelAllReminders() {
        Log.d(TAG, "cancelAllReminders called")
        viewModelScope.launch {
            notificationRepository.cancelAllReminders()
            Log.d(TAG, "cancelAllReminders completed")
        }
    }
}
