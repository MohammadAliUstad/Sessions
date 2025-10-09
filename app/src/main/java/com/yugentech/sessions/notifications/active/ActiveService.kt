package com.yugentech.sessions.notifications.active

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.yugentech.sessions.MainActivity
import com.yugentech.sessions.R
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import java.util.Locale

private const val TAG = "active service"

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
        Log.d(TAG, "Creating notification channels...")

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
        Log.d(TAG, "Active session channel created: id=$ACTIVE_CHANNEL_ID")

        manager.createNotificationChannel(reminderChannel)
        Log.d(TAG, "Reminder session channel created: id=$REMINDER_CHANNEL_ID")
    }

    fun showNotification(notification: Notification) {
        Log.d(TAG, "Attempting to show notification: id=${notification.id}, title='${notification.title}'")
        if (!hasNotificationPermission()) {
            Log.w(TAG, "Cannot show notification: permission denied")
            return
        }

        try {
            val androidNotification = buildNotification(notification)
            notificationManager.notify(notification.id, androidNotification)
            Log.d(TAG, "Notification shown: id=${notification.id}")
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to show notification due to SecurityException", e)
        }
    }

    fun hideNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    private fun hasNotificationPermission(): Boolean {
        val allowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            notificationManager.areNotificationsEnabled()
        }
        return allowed
    }

    fun buildNotification(notification: Notification): android.app.Notification {

        val channelId = when (notification.type) {
            NotificationType.SCHEDULED -> REMINDER_CHANNEL_ID
            NotificationType.ACTIVE -> ACTIVE_CHANNEL_ID
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
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
                        notification.remainingSeconds?.let { seconds ->
                            val formattedTime = String.format(
                                Locale.US,
                                "%02d:%02d",
                                seconds / 60,
                                seconds % 60
                            )
                            setContentText("$formattedTime time remaining")
                        }
                    }
                    NotificationType.SCHEDULED -> {
                        setContentText("Time to focus and be productive")
                        setPriority(NotificationCompat.PRIORITY_HIGH)
                        setDefaults(NotificationCompat.DEFAULT_ALL)
                    }
                }
            }

        return builder.build()
    }
}