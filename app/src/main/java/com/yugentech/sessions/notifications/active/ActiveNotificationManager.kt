package com.yugentech.sessions.notifications.active

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.utils.AppConstants
import org.koin.core.component.KoinComponent
import timber.log.Timber

// Helper to launch and control the ActiveForeground service via intents
class ActiveNotificationManager(
    private val context: Context
) : KoinComponent {

    // Launches the service in foreground mode with initial session details
    fun startActiveNotification(notification: Notification) {
        Timber.d("Starting ActiveForeground service for: ${notification.title}")
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = AppConstants.ACTION_START_SESSION
            putExtra("title", notification.title)
            putExtra("message", notification.message)
            putExtra("totalMinutes", notification.totalSeconds ?: 0)
            putExtra("remainingMinutes", notification.remainingSeconds ?: 0)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    // Sends an update intent to the running service with new time data
    fun updateActiveNotification(notification: Notification) {
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = AppConstants.ACTION_UPDATE_SESSION
            putExtra("remainingMinutes", notification.remainingSeconds ?: 0)
        }
        context.startService(intent)
    }

    // Sends a stop intent to the service to end the foreground session
    fun stopActiveNotification() {
        Timber.d("Stopping ActiveForeground service")
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = AppConstants.ACTION_STOP_SESSION
        }
        context.startService(intent)
    }
}