package com.yugentech.sessions.notifications.active

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ActiveForeground : Service() {

    companion object {
        const val ACTION_START_SESSION = "START_SESSION"
        const val ACTION_STOP_SESSION = "STOP_SESSION"
        const val ACTION_UPDATE_SESSION = "UPDATE_SESSION"
    }

    private val activeService: ActiveService by inject()
    private var isSessionActive = false
    private var updateJob: Job? = null
    private var remainingSeconds = 0
    private var sessionTitle = "Focus Session"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SESSION -> startSession(intent)
            ACTION_STOP_SESSION -> stopSession()
            ACTION_UPDATE_SESSION -> {
                val newTime = intent.getIntExtra("remainingMinutes", remainingSeconds)
                if (newTime != remainingSeconds) {
                    remainingSeconds = newTime
                }
            }

            else -> startSession(intent)
        }

        return if (isSessionActive) START_STICKY else START_NOT_STICKY
    }

    private fun startSession(intent: Intent?) {
        if (isSessionActive) return

        isSessionActive = true
        sessionTitle = intent?.getStringExtra("title") ?: "Focus Session"
        remainingSeconds = intent?.getIntExtra("remainingMinutes", 0) ?: 0

        val notification = createNotification(remainingSeconds)
        val androidNotification = activeService.buildNotification(notification)

        val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        } else {
            0
        }

        ServiceCompat.startForeground(
            this,
            ActiveService.ACTIVE_NOTIFICATION_ID,
            androidNotification,
            serviceType
        )

        startCountdown()
    }

    private fun startCountdown() {
        updateJob?.cancel()
        updateJob = CoroutineScope(Dispatchers.Default).launch {
            while (isSessionActive && remainingSeconds > 0) {
                delay(1000)
                remainingSeconds--

                val notification = createNotification(remainingSeconds)
                activeService.showNotification(notification)
            }

            if (remainingSeconds <= 0) {
                stopSession()
            }
        }
    }

    private fun createNotification(seconds: Int): Notification {
        return Notification(
            id = ActiveService.ACTIVE_NOTIFICATION_ID,
            type = NotificationType.ACTIVE,
            title = sessionTitle,
            message = "Session in progress",
            isOngoing = true,
            timeRemainingMinutes = seconds
        )
    }

    private fun stopSession() {
        isSessionActive = false
        updateJob?.cancel()
        updateJob = null
        activeService.hideNotification(ActiveService.ACTIVE_NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSession()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        isSessionActive = false
        updateJob?.cancel()
        activeService.hideNotification(ActiveService.ACTIVE_NOTIFICATION_ID)
        super.onDestroy()
    }
}