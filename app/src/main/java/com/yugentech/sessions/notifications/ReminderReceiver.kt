package com.yugentech.sessions.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yugentech.sessions.notifications.utils.NotificationType
import com.yugentech.sessions.notifications.utils.Notification
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReminderReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationService: NotificationService by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("message") ?: "Time to study!"

        val notification = Notification(
            id = NotificationService.REMINDER_NOTIFICATION_ID,
            type = NotificationType.REMINDER,
            title = "Study Reminder",
            message = message,
            isOngoing = false
        )

        notificationService.showNotification(notification)
    }
}