package com.yugentech.sessions.notifications.active

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import org.koin.android.ext.android.inject

class ActiveForeground : Service() {

    private val activeService: ActiveService by inject()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("title") ?: "Study Session"
        val message = intent?.getStringExtra("message") ?: "Session in progress"
        val totalMinutes = intent?.getIntExtra("totalMinutes", 0)
        val remainingMinutes = intent?.getIntExtra("remainingMinutes", 0)

        val notification = Notification(
            id = ActiveService.Companion.ACTIVE_NOTIFICATION_ID,
            type = NotificationType.ACTIVE,
            title = title,
            message = message,
            isOngoing = true,
            totalMinutes = totalMinutes,
            timeRemainingMinutes = remainingMinutes
        )

        val androidNotification = activeService.buildNotification(notification)

        ServiceCompat.startForeground(
            this,
            ActiveService.Companion.ACTIVE_NOTIFICATION_ID,
            androidNotification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
            } else {
                0
            }
        )

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        activeService.hideNotification(ActiveService.Companion.ACTIVE_NOTIFICATION_ID)
    }
}