package com.yugentech.sessions.notifications.active

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.yugentech.sessions.notifications.Notification
import org.koin.core.component.KoinComponent

class ActiveServiceManager(
    private val context: Context
) : KoinComponent {

    fun startActiveSession(notification: Notification) {
        val intent = Intent(context, ActiveForeground::class.java).apply {
            putExtra("title", notification.title)
            putExtra("message", notification.message)
            putExtra("totalMinutes", notification.totalMinutes ?: 0)
            putExtra("remainingMinutes", notification.timeRemainingMinutes ?: 0)
        }

        ContextCompat.startForegroundService(context, intent)
    }

    fun stopActiveSession() {
        val intent = Intent(context, ActiveForeground::class.java)
        context.stopService(intent)
    }
}