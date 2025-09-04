package com.yugentech.sessions.notifications

import android.Manifest
import android.app.AlarmManager
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
import java.util.Calendar

class NotificationService(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val ACTIVE_CHANNEL_ID = "active_session_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val ACTIVE_NOTIFICATION_ID = 1001
        const val REMINDER_NOTIFICATION_ID = 1002
        private const val TAG = "NotificationService"
    }

    fun createNotificationChannels() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val activeChannel = NotificationChannel(
            ACTIVE_CHANNEL_ID,
            "Active Session",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Shows when session is active"
            setShowBadge(true)
        }

        val reminderChannel = NotificationChannel(
            REMINDER_CHANNEL_ID,
            "Session Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Session reminders"
            setShowBadge(true)
        }

        manager.createNotificationChannel(activeChannel)
        manager.createNotificationChannel(reminderChannel)
    }

    fun showNotification(notification: Notification) {
        // Check if we have notification permission
        if (!hasNotificationPermission()) {
            Log.w(TAG, "Notification permission not granted")
            return
        }

        try {
            val androidNotification = buildNotification(notification)
            notificationManager.notify(notification.id, androidNotification)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to show notification: ${e.message}")
        }
    }

    fun hideNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    fun hideAllNotifications() {
        notificationManager.cancelAll()
    }

    fun scheduleReminderNotification(hour: Int, minute: Int, message: String): Boolean {
        // Check if we can schedule exact alarms
        if (!canScheduleExactAlarms()) {
            Log.w(TAG, "Cannot schedule exact alarms - permission not granted")
            return false
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hour * 100 + minute,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d(TAG, "Reminder scheduled for ${hour}:${minute}")
            return true
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to schedule reminder: ${e.message}")
            return false
        }
    }

    fun cancelScheduledReminder(hour: Int, minute: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hour * 100 + minute,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Reminder cancelled for ${hour}:${minute}")
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For older versions, notification permission is granted by default
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun buildNotification(notification: Notification): android.app.Notification {
        val channelId = when (notification.type) {
            NotificationType.REMINDER -> REMINDER_CHANNEL_ID
            NotificationType.ACTIVE_SESSION -> ACTIVE_CHANNEL_ID
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
            .setSmallIcon(R.drawable.sessions_timer_coral)
            .setContentIntent(pendingIntent)
            .setOngoing(notification.isOngoing)
            .setAutoCancel(!notification.isOngoing)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setShowWhen(false)
            .apply {
                when (notification.type) {
                    NotificationType.ACTIVE_SESSION -> {
                        if (notification.timeRemainingMinutes != null && notification.totalMinutes != null) {
                            val progressCurrent =
                                notification.totalMinutes - notification.timeRemainingMinutes
                            val progressMax = notification.totalMinutes

                            // Real progress bar
                            setProgress(progressMax, progressCurrent, false)

                            // Better text formatting
                            val timeText = when {
                                notification.timeRemainingMinutes > 1 ->
                                    "${notification.timeRemainingMinutes} minutes left"

                                notification.timeRemainingMinutes == 1 ->
                                    "1 minute left"

                                else ->
                                    "Less than 1 minute"
                            }
                            setContentText(timeText)

                            // 🔧 TRY THIS FOR STATUS BAR ICON
                            setSubText(timeText)

                        } else {
                            // Fallback
                            setProgress(0, 0, true)
                        }
                    }

                    NotificationType.REMINDER -> {
                        setPriority(NotificationCompat.PRIORITY_HIGH)
                        setDefaults(NotificationCompat.DEFAULT_ALL)
                    }
                }
            }
            .build()
    }
}