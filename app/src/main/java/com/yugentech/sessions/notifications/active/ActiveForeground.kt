package com.yugentech.sessions.notifications.active

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.utils.Constants.ACTION_START_SESSION
import com.yugentech.sessions.utils.Constants.ACTION_STOP_SESSION
import com.yugentech.sessions.utils.Constants.ACTION_UPDATE_SESSION
import com.yugentech.sessions.viewModels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Locale


class ActiveForeground : Service() {

    private val notificationService: NotificationService by inject()
    private val homeViewModel: HomeViewModel by inject()
    private var isSessionActive = false
    private var updateJob: Job? = null
    private var remainingSeconds = 0
    private var sessionTitle = "Focus Session"
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SESSION -> startSession(intent)
            ACTION_STOP_SESSION -> stopSession()
            ACTION_UPDATE_SESSION -> {
                val newTime = intent.getIntExtra("remainingMinutes", remainingSeconds)
                if (newTime != remainingSeconds) {
                    remainingSeconds = newTime
                    if (isSessionActive) {
                        updateNotification(currentSeconds = remainingSeconds)
                    }
                }
            }
        }

        return if (isSessionActive) START_STICKY else START_NOT_STICKY
    }

    private fun startSession(intent: Intent?) {
        if (isSessionActive) return

        isSessionActive = true
        sessionTitle = intent?.getStringExtra("title") ?: "Focus Session"
        remainingSeconds = intent?.getIntExtra("remainingMinutes", 0) ?: 0

        val placeholderNotification = Notification(
            id = NotificationService.ACTIVE_NOTIFICATION_ID,
            type = NotificationType.ACTIVE,
            title = sessionTitle,
            message = "Starting session...",
            isOngoing = true,
            remainingSeconds = remainingSeconds
        )

        val androidNotification = notificationService.buildNotification(placeholderNotification)

        try {
            val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else 0

            ServiceCompat.startForeground(
                this,
                NotificationService.ACTIVE_NOTIFICATION_ID,
                androidNotification,
                serviceType
            )

            startCountdown()

        } catch (_: Exception) {
            isSessionActive = false
            stopSelf()
        }
    }

    private fun startCountdown() {
        updateJob?.cancel()
        updateJob = serviceScope.launch {
            while (isSessionActive) {
                delay(500)
                val syncedSeconds = homeViewModel.uiState.value.currentTime
                updateNotification(syncedSeconds)
            }
        }
    }


    private fun updateNotification(currentSeconds: Int) {
        val notification = createNotification(currentSeconds)
        notificationService.showNotification(notification)
    }


    private fun createNotification(seconds: Int): Notification {
        val formattedTime = String.format(
            Locale.US,
            "%02d:%02d",
            seconds / 60,
            seconds % 60
        )
        return Notification(
            id = NotificationService.ACTIVE_NOTIFICATION_ID,
            type = NotificationType.ACTIVE,
            title = sessionTitle,
            message = "$formattedTime remaining",
            isOngoing = true,
            remainingSeconds = seconds
        )
    }

    private fun stopSession() {
        if (!isSessionActive && updateJob == null) return
        isSessionActive = false
        remainingSeconds = 0
        updateJob?.cancel()
        updateJob = null

        notificationService.hideNotification(NotificationService.ACTIVE_NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSession()
        homeViewModel.resetSessionState()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        isSessionActive = false
        updateJob?.cancel()
        notificationService.hideNotification(NotificationService.ACTIVE_NOTIFICATION_ID)
        super.onDestroy()
    }
}