package com.yugentech.sessions.notifications.active

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.yugentech.sessions.alerts.repository.AlertsRepository
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationService
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.timer.states.TimerMode
import com.yugentech.sessions.utils.AppConstants
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.Locale

// Foreground service that keeps the timer running and visible in the notification tray
class ActiveForeground : Service() {

    private val notificationService: NotificationService by inject()
    private val timerRepository: TimerRepository by inject()
    private val alertsRepository: AlertsRepository by inject()

    private var isSessionActive = false
    private var updateJob: Job? = null
    private var remainingSeconds = 0
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    // Initializes notification channels when service is created
    override fun onCreate() {
        super.onCreate()
        Timber.d("ActiveForeground Service Created")
        notificationService.createNotificationChannels()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Handles start, stop, and update commands via Intents
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            AppConstants.ACTION_START_SESSION -> startSession(intent)
            AppConstants.ACTION_STOP_SESSION -> stopSession()
            AppConstants.ACTION_UPDATE_SESSION -> {
                val newTime = intent.getIntExtra("remainingMinutes", remainingSeconds)
                if (newTime != remainingSeconds) {
                    remainingSeconds = newTime
                    if (isSessionActive) {
                        val timerState = timerRepository.timerState.value
                        updateNotification(remainingSeconds.toLong(), timerState.currentMode)
                    }
                }
            }
        }
        return if (isSessionActive) START_STICKY else START_NOT_STICKY
    }

    // Promotes the service to the foreground and begins the timer update loop
    private fun startSession(intent: Intent?) {
        if (isSessionActive) return

        isSessionActive = true
        remainingSeconds = intent?.getIntExtra("remainingMinutes", 0) ?: 0

        Timber.i("Starting session in foreground")

        val timerState = timerRepository.timerState.value
        val placeholderNotification = Notification(
            id = NotificationService.ACTIVE_NOTIFICATION_ID,
            type = NotificationType.ACTIVE,
            title = getModeTitle(timerState.currentMode),
            message = "Starting session...",
            isOngoing = true,
            remainingSeconds = remainingSeconds.toLong()
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

        } catch (e: Exception) {
            Timber.e(e, "Failed to start foreground service")
            isSessionActive = false
            stopSelf()
        }
    }

    // Continuously updates the notification with the latest timer state
    private fun startCountdown() {
        updateJob?.cancel()
        updateJob = serviceScope.launch {
            while (isSessionActive) {
                val timerState = timerRepository.timerState.value
                val syncedSeconds = timerState.currentTime
                val currentMode = timerState.currentMode
                updateNotification(syncedSeconds, currentMode)
                delay(500)
            }
        }
    }

    // Updates the existing notification with the current remaining time
    private fun updateNotification(currentSeconds: Long, mode: TimerMode) {
        val notification = createNotification(currentSeconds, mode)
        notificationService.showNotification(notification)
    }

    // Builds a notification object with formatted time string
    private fun createNotification(seconds: Long, mode: TimerMode): Notification {
        val formattedTime = String.format(
            Locale.US,
            "%02d:%02d",
            seconds / 60,
            seconds % 60
        )
        return Notification(
            id = NotificationService.ACTIVE_NOTIFICATION_ID,
            type = NotificationType.ACTIVE,
            title = getModeTitle(mode),
            message = "$formattedTime remaining",
            isOngoing = true,
            remainingSeconds = seconds
        )
    }

    // Returns a displayable title based on the current timer mode
    private fun getModeTitle(mode: TimerMode): String {
        return when (mode) {
            TimerMode.Focus -> "Focus"
            TimerMode.ShortBreak -> "Short Break"
            TimerMode.LongBreak -> "Long Break"
        }
    }

    // Stops the service, cancels the update loop, and removes the notification
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

    // Cleans up resources if the app task is swiped away
    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.d("Task removed, performing cleanup")
        stopSession()
        timerRepository.reset()
        alertsRepository.onLeave()
        super.onTaskRemoved(rootIntent)
    }

    // Final cleanup when service is destroyed
    override fun onDestroy() {
        Timber.d("ActiveForeground Service Destroyed")
        isSessionActive = false
        updateJob?.cancel()
        notificationService.hideNotification(NotificationService.ACTIVE_NOTIFICATION_ID)
        super.onDestroy()
    }
}