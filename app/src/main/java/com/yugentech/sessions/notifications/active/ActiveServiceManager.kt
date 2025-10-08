package com.yugentech.sessions.notifications.active

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.utils.Constants.ACTION_START_SESSION
import com.yugentech.sessions.utils.Constants.ACTION_STOP_SESSION
import com.yugentech.sessions.utils.Constants.ACTION_UPDATE_SESSION
import org.koin.core.component.KoinComponent

class ActiveServiceManager(
    private val context: Context
) : KoinComponent {

    fun startActiveSession(notification: Notification) {
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = ACTION_START_SESSION
            putExtra("title", notification.title)
            putExtra("message", notification.message)
            putExtra("totalMinutes", notification.totalSeconds ?: 0)
            putExtra("remainingMinutes", notification.remainingSeconds ?: 0)
        }

        ContextCompat.startForegroundService(context, intent)
    }

    fun updateActiveSession(notification: Notification) {
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = ACTION_UPDATE_SESSION
            putExtra("remainingMinutes", notification.remainingSeconds ?: 0)
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