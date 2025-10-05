package com.yugentech.sessions.notifications.active

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.yugentech.sessions.MainActivity
import com.yugentech.sessions.R
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import java.util.Locale


class ActiveService(
    private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        const val ACTIVE_CHANNEL_ID = "active_session_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val ACTIVE_NOTIFICATION_ID = 1001
        const val REMINDER_NOTIFICATION_ID = 1002
    }

    fun createNotificationChannels() {

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // --- CORRECTION APPLIED HERE ---
        // Changed IMPORTANCE_LOW to IMPORTANCE_DEFAULT to ensure the icon
        // appears in the status bar on Android 8.0+
        val activeChannel = NotificationChannel(
            ACTIVE_CHANNEL_ID,
            "Active Session",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Shows when a study session is active"
            setSound(null, null)
            enableVibration(false)
        }

        val reminderChannel = NotificationChannel(
            REMINDER_CHANNEL_ID,
            "Session Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminds you to start or resume a study session"
        }

        manager.createNotificationChannel(activeChannel)
        manager.createNotificationChannel(reminderChannel)
    }

    fun showNotification(notification: Notification) {
        if (!hasNotificationPermission()) return

        try {
            val androidNotification = buildNotification(notification)
            notificationManager.notify(notification.id, androidNotification)
        } catch (_: SecurityException) {}
    }

    fun hideNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            notificationManager.areNotificationsEnabled()
        }
    }

    fun buildNotification(notification: Notification): android.app.Notification {
        val channelId = when (notification.type) {
            NotificationType.SCHEDULED -> REMINDER_CHANNEL_ID
            NotificationType.ACTIVE -> ACTIVE_CHANNEL_ID
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setSilent(true)
            .setOngoing(notification.isOngoing)
            .setAutoCancel(!notification.isOngoing)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .apply {
                when (notification.type) {
                    NotificationType.ACTIVE -> {
                        notification.timeRemainingMinutes?.let { seconds ->
                            val formattedTime = String.format(
                                Locale.US,
                                "%02d:%02d",
                                seconds / 60,  // minutes
                                seconds % 60   // seconds
                            )
                            setContentText("$formattedTime time remaining")
                        }
                    }

                    NotificationType.SCHEDULED -> {
                        setPriority(NotificationCompat.PRIORITY_HIGH)
                        setDefaults(NotificationCompat.DEFAULT_ALL)
                    }
                }
            }
            .build()
    }
}