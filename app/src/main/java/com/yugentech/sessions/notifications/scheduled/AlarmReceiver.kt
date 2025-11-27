package com.yugentech.sessions.notifications.scheduled

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.notifications.active.NotificationService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationService: NotificationService by inject()

    override fun onReceive(context: Context, intent: Intent) {

        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Time to study!"

        val notification = Notification(
            id = NotificationService.REMINDER_NOTIFICATION_ID,
            type = NotificationType.SCHEDULED,
            title = "Reminder",
            message = message,
            isOngoing = false
        )

        notificationService.showNotification(notification)
    }

    companion object {
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_REQUEST_CODE = "request_code"
    }
}