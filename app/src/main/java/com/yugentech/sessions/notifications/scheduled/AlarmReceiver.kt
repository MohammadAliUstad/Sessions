package com.yugentech.sessions.notifications.scheduled

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.notifications.active.ActiveService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val activeService: ActiveService by inject()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "AlarmReceiver triggered")

        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Time to study!"
        Log.d(TAG, "Received message: $message")

        val notification = Notification(
            id = ActiveService.REMINDER_NOTIFICATION_ID,
            type = NotificationType.SCHEDULED,
            title = "Reminder",
            message = message,
            isOngoing = false
        )

        activeService.showNotification(notification)
        Log.d(TAG, "Notification shown from AlarmReceiver")
    }

    companion object {
        const val EXTRA_MESSAGE = "message"
        private const val TAG = "AlarmReceiver"
    }
}