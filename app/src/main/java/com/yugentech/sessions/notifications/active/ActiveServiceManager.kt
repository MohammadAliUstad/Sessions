package com.yugentech.sessions.notifications.active

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.yugentech.sessions.notifications.Notification
import org.koin.core.component.KoinComponent

class ActiveServiceManager(
    private val context: Context
) : KoinComponent {

    companion object {
        const val ACTION_START_SESSION = "START_SESSION"
        const val ACTION_STOP_SESSION = "STOP_SESSION"
        const val ACTION_UPDATE_SESSION = "UPDATE_SESSION"
    }

    fun startActiveSession(notification: Notification) {
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = ACTION_START_SESSION
            putExtra("title", notification.title)
            putExtra("message", notification.message)
            putExtra("totalMinutes", notification.totalMinutes ?: 0)
            putExtra("remainingMinutes", notification.timeRemainingMinutes ?: 0)
        }

        ContextCompat.startForegroundService(context, intent)
    }

    fun updateActiveSession(notification: Notification) {
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = ACTION_UPDATE_SESSION
            putExtra("remainingMinutes", notification.timeRemainingMinutes ?: 0)
        }

        context.startService(intent)
    }

    fun stopActiveSession() {
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = ACTION_STOP_SESSION
        }
        context.startService(intent)
    }
}