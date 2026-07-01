package com.yugentech.sessions.notification.active

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.yugentech.sessions.alerts.repository.AlertsRepository
import com.yugentech.sessions.notification.model.Notification
import com.yugentech.sessions.notification.model.NotificationType
import com.yugentech.sessions.notification.service.NotificationService
import com.yugentech.sessions.timer.effect.TimerEffect
import com.yugentech.sessions.timer.repository.TimerRepository
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.utils.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

class ActiveForeground : Service() {

    private val notificationService: NotificationService by inject()
    private val timerRepository: TimerRepository by inject()
    private val alertsRepository: AlertsRepository by inject()

    private var isSessionActive = false
    private var updateJob: Job? = null
    private var effectsJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.d("ActiveForeground Service Created")
        notificationService.createNotificationChannels()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            AppConstants.ACTION_START_SESSION -> startSession()
            AppConstants.ACTION_STOP_SESSION -> stopSession()
            AppConstants.ACTION_SKIP_SESSION -> skipSession()
            AppConstants.ACTION_PAUSE_SESSION -> pauseSession()
            AppConstants.ACTION_RESUME_SESSION -> resumeSession()
            AppConstants.ACTION_FINISH_SESSION -> finishSession()
        }
        return if (isSessionActive) START_STICKY else START_NOT_STICKY
    }

    private fun pauseSession() {
        Timber.i("Pausing session from notification")
        timerRepository.pause()
        alertsRepository.onFocusPause(null)
        val state = timerRepository.timerState.value
        updateNotification(state.currentTime, state.currentMode)
    }

    private fun resumeSession() {
        Timber.i("Resuming session from notification")
        timerRepository.start()
        alertsRepository.onFocusStart(null)
        val state = timerRepository.timerState.value
        updateNotification(state.currentTime, state.currentMode)
    }

    private fun skipSession() {
        Timber.i("Skipping session from notification")
        timerRepository.skipToNext()
        val state = timerRepository.timerState.value
        updateNotification(state.currentTime, state.currentMode)
    }

    private fun finishSession() {
        Timber.i("Finishing session from notification (Save and Stop)")
        timerRepository.saveCurrentSession()
        alertsRepository.onGoalReached(null)
        stopSession(playStopAlert = false)
    }

    private fun startSession() {
        if (isSessionActive) return
        isSessionActive = true

        // Read the current snapshot directly from the repository.
        val timerState = timerRepository.timerState.value

        Timber.i(
            "Starting session in foreground — mode: ${timerState.currentMode}, " +
                    "total: ${timerState.totalTime}s, remaining: ${timerState.currentTime}s"
        )

        val placeholderNotification = buildNotificationFromState(
            seconds = timerState.currentTime,
            mode = timerState.currentMode,
            message = "Starting session..."
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

            // Start countdown and effects observation AFTER successfully starting foreground
            startCountdown()
            observeTimerEffects()
        } catch (e: Exception) {
            Timber.e(e, "Failed to start foreground service")
            isSessionActive = false
            stopSelf()
        }
    }

    private fun observeTimerEffects() {
        effectsJob?.cancel()
        effectsJob = serviceScope.launch {
            timerRepository.timerEffects.collect { effect ->
                Timber.d("ActiveForeground Received Timer Effect: $effect")
                when (effect) {
                    is TimerEffect.FocusCompleted -> handleFocusCompleted()
                    is TimerEffect.BreakCompleted -> handleBreakCompleted()
                    is TimerEffect.EndGoalReached -> handleEndGoalReached()
                }
            }
        }
    }

    private fun handleFocusCompleted() {
        alertsRepository.onBreakStart(null)
    }

    private fun handleEndGoalReached() {
        alertsRepository.onGoalReached(null)
        stopSession(playStopAlert = false)
    }

    private fun handleBreakCompleted() {
        alertsRepository.onFocusStart(null)
    }

    private fun startCountdown() {
        updateJob?.cancel()
        updateJob = serviceScope.launch {
            while (isSessionActive) {
                // Update every second instead of 100ms to reduce system load and flicker
                delay(1000.milliseconds)
                val timerState = timerRepository.timerState.value
                updateNotification(timerState.currentTime, timerState.currentMode)
            }
        }
    }

    private fun updateNotification(currentSeconds: Long, mode: TimerMode) {
        notificationService.showNotification(buildNotificationFromState(currentSeconds, mode))
    }

    // All fields come from the live timerState snapshot on every tick.
    // totalTime is always current — no stale cached fields.
    private fun buildNotificationFromState(
        seconds: Long,
        mode: TimerMode,
        message: String = ""
    ): Notification {
        val state = timerRepository.timerState.value
        return Notification(
            id = NotificationService.ACTIVE_NOTIFICATION_ID,
            type = NotificationType.ACTIVE,
            title = getModeTitle(mode),
            message = message,
            isOngoing = true,
            remainingSeconds = seconds,
            totalSeconds = state.totalTime,
            completedSets = state.completedSets,
            totalSets = state.timerConfig.targetSets,
            mode = mode,
            setsPerLongBreak = state.timerConfig.setsPerLongBreak
        )
    }

    private fun getModeTitle(mode: TimerMode): String {
        return when (mode) {
            TimerMode.Focus -> "Focus"
            TimerMode.ShortBreak -> "Short Break"
            TimerMode.LongBreak -> "Long Break"
        }
    }

    private fun stopSession(playStopAlert: Boolean = true) {
        if (!isSessionActive && updateJob == null) return

        Timber.i("Stopping session in foreground")
        isSessionActive = false
        updateJob?.cancel()
        updateJob = null
        effectsJob?.cancel()
        effectsJob = null

        // Reorder to ensure alerts play before service destruction
        if (playStopAlert) {
            alertsRepository.onFocusStop(null)
        }
        timerRepository.reset()

        notificationService.hideNotification(NotificationService.ACTIVE_NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.d("Task removed, performing cleanup")
        // stopSession handles reset and onFocusStop
        stopSession()
        alertsRepository.onLeave()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Timber.d("ActiveForeground Service Destroyed")
        isSessionActive = false
        updateJob?.cancel()
        effectsJob?.cancel()
        notificationService.hideNotification(NotificationService.ACTIVE_NOTIFICATION_ID)
        super.onDestroy()
    }
}