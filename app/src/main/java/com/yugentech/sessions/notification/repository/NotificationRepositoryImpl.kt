package com.yugentech.sessions.notification.repository

import com.yugentech.sessions.notification.active.ActiveNotificationManager
import timber.log.Timber

// Implementation that delegates the active session notification to its manager
class NotificationRepositoryImpl(
    private val activeNotificationManager: ActiveNotificationManager
) : NotificationRepository {

    // Fires the intent to start ActiveForeground. The service reads all timer state
    // directly from TimerRepository on its own — no data needs to be passed here.
    override fun startActiveNotification() {
        Timber.d("Requesting start of active session notification")
        activeNotificationManager.startActiveNotification()
    }

    // Fires the intent to stop ActiveForeground and remove the notification.
    override fun stopActiveNotification() {
        Timber.d("Requesting stop of active session notification")
        activeNotificationManager.stopActiveNotification()
    }

    override fun finishActiveNotification() {
        Timber.d("Requesting finish of active session notification")
        activeNotificationManager.finishActiveNotification()
    }
}
