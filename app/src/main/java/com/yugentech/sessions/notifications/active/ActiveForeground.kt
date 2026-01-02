package com.yugentech.sessions.notifications.active

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationService
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.viewModels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.Locale

// Foreground service to keep the session active and show a persistent notification
class ActiveForeground : Service() {

    private val notificationService: NotificationService by inject()
    private val homeViewModel: HomeViewModel by inject()
    private var isSessionActive = false
    private var updateJob: Job? = null
    private var remainingSeconds = 0
    private var sessionTitle = "Focus Session"
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.d("ActiveForeground Service Created")
        // Initialize channels immediately to prevent invisible service crashes
        notificationService.createNotificationChannels()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            AppConstants.ACTION_START_SESSION -> startSession(intent)
            AppConstants.ACTION_STOP_SESSION -> stopSession()
            AppConstants.ACTION_UPDATE_SESSION -> {
                val newTime = intent.getIntExtra("remainingMinutes", remainingSeconds)
                if (newTime != remainingSeconds) {
                    remainingSeconds = newTime
                    if (isSessionActive) {
                        updateNotification(currentSeconds = remainingSeconds.toLong())
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

        Timber.i("Starting session in foreground: $sessionTitle")

        val placeholderNotification = Notification(
            id = NotificationService.ACTIVE_NOTIFICATION_ID,
            type = NotificationType.ACTIVE,
            title = sessionTitle,
            message = "Starting session...",
            isOngoing = true,
            remainingSeconds = remainingSeconds.toLong()
        )

        val androidNotification = notificationService.buildNotification(placeholderNotification)

        try {
            val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else 0

            // Promote service to foreground to prevent system kill
            ServiceCompat.startForeground(
                this,
                NotificationService.ACTIVE_NOTIFICATION_ID,
                androidNotification,
                serviceType
            )

            startCountdown()

        } catch (e: Exception) {
            Timber.e(e, "Failed to start foreground service")
            isSessionActive = false
            stopSelf()
        }
    }

    private fun startCountdown() {
        updateJob?.cancel()
        updateJob = serviceScope.launch {
            while (isSessionActive) {
                // Fetch latest state directly from ViewModel
                val syncedSeconds = homeViewModel.uiState.value.status.currentTime
                updateNotification(syncedSeconds)
                // Delay at end of loop ensures UI updates immediately on start
                delay(500)
            }
        }
    }

    private fun updateNotification(currentSeconds: Long) {
        val notification = createNotification(currentSeconds)
        notificationService.showNotification(notification)
    }

    private fun createNotification(seconds: Long): Notification {
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

        Timber.i("Stopping session in foreground")
        isSessionActive = false
        remainingSeconds = 0
        updateJob?.cancel()
        updateJob = null

        notificationService.hideNotification(NotificationService.ACTIVE_NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.d("Task removed, performing cleanup")
        stopSession()
         super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Timber.d("ActiveForeground Service Destroyed")
        isSessionActive = false
        updateJob?.cancel()
        notificationService.hideNotification(NotificationService.ACTIVE_NOTIFICATION_ID)
        super.onDestroy()
    }
}