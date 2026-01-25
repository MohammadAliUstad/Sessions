package com.yugentech.sessions.notifications

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
import timber.log.Timber
import java.util.Locale

// Handles low-level interaction with the Android Notification Manager
class NotificationService(
    private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        const val ACTIVE_CHANNEL_ID = "active_session_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val ACTIVE_NOTIFICATION_ID = 1001
        const val REMINDER_NOTIFICATION_ID = 1002
        const val EXTRA_NAVIGATE_TO_HOME = "navigate_to_home"
    }

    // Creates separate channels for silent timer updates and high-priority reminders
    fun createNotificationChannels() {
        Timber.d("Creating notification channels")
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
            "Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminds you to start or resume a study session"
        }

        manager.createNotificationChannel(activeChannel)
        manager.createNotificationChannel(reminderChannel)
    }

    // Displays a notification if the required runtime permission is granted
    fun showNotification(notification: Notification) {
        if (!hasNotificationPermission()) {
            Timber.w("Cannot show notification: POST_NOTIFICATIONS permission missing")
            return
        }

        try {
            val androidNotification = buildNotification(notification)
            // Use specific IDs for specific types to avoid overwriting unrelated notifications
            notificationManager.notify(notification.id, androidNotification)
        } catch (e: SecurityException) {
            Timber.e(e, "SecurityException while showing notification")
        } catch (e: Exception) {
            Timber.e(e, "Failed to show notification")
        }
    }

    // Removes a specific notification from the status bar
    fun hideNotification(notificationId: Int) {
        try {
            notificationManager.cancel(notificationId)
        } catch (e: Exception) {
            Timber.e(e, "Failed to cancel notification ID: $notificationId")
        }
    }

    // Verifies if the app has the POST_NOTIFICATIONS permission on Android 13+
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

    // Constructs the notification object with appropriate flags, intents, and styles
    fun buildNotification(notification: Notification): android.app.Notification {
        val channelId = when (notification.type) {
            NotificationType.SCHEDULED -> REMINDER_CHANNEL_ID
            NotificationType.ACTIVE -> ACTIVE_CHANNEL_ID
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_NAVIGATE_TO_HOME, true)
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
            .setOngoing(notification.isOngoing)
            .setAutoCancel(!notification.isOngoing)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .apply {
                // Ensure notification shows immediately for FGS on Android 12+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setForegroundServiceBehavior(android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE)
                }

                when (notification.type) {
                    NotificationType.ACTIVE -> {
                        setSilent(true)
                        notification.remainingSeconds?.let { seconds ->
                            val formattedTime = String.format(
                                Locale.US,
                                "%02d:%02d",
                                seconds / 60,
                                seconds % 60
                            )
                            setContentText("$formattedTime remaining")
                        }
                    }
                    NotificationType.SCHEDULED -> {
                        setContentText("Time to focus and be productive")
                        setPriority(NotificationCompat.PRIORITY_HIGH)
                        setDefaults(NotificationCompat.DEFAULT_ALL)
                    }
                }
            }.build()
    }
}