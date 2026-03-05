package com.yugentech.sessions.timer.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yugentech.sessions.alerts.repository.AlertsRepository
import com.yugentech.sessions.sessions.model.Session
import com.yugentech.sessions.notification.model.Notification
import com.yugentech.sessions.notification.model.NotificationType
import com.yugentech.sessions.notification.repository.NotificationRepository
import com.yugentech.sessions.sessions.repository.SessionsRepository
import com.yugentech.sessions.sessions.result.SessionResult
import com.yugentech.sessions.timer.repository.TimerRepository
import com.yugentech.sessions.timer.effect.TimerEffect
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.ui.dash.state.SessionDashboardState
import com.yugentech.sessions.ui.dash.util.SessionDashboardCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

class TimerViewModel(
    private val timerRepository: TimerRepository,
    private val sessionsRepository: SessionsRepository,
    private val alertsRepository: AlertsRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val timerState = timerRepository.timerState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Real-time calculation of dashboard statistics based on timer state
    val dashboardState: StateFlow<SessionDashboardState> = timerState
        .map { state -> SessionDashboardCalculator.calculate(state) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SessionDashboardState()
        )

    init {
        observeTimerEffects()
    }

    private fun observeTimerEffects() {
        viewModelScope.launch {
            // React to one-time timer events like completion or goal reached
            timerRepository.timerEffects.collect { effect ->
                Timber.d("Received Timer Effect: $effect")
                when (effect) {
                    is TimerEffect.FocusCompleted -> handleFocusCompleted(effect.durationSeconds)
                    is TimerEffect.BreakCompleted -> handleBreakCompleted()
                    is TimerEffect.EndGoalReached -> handleEndGoalReached(effect.durationSeconds)
                }
            }
        }
    }

    private fun handleFocusCompleted(durationSeconds: Int) {
        Timber.d("Handling FocusCompleted. Triggering Break Sound.")
        // Play break start sound
        alertsRepository.onBreakStart(null)

        if (durationSeconds >= 60) {
            saveSessionToDb(durationSeconds)
        } else {
            _errorMessage.value = "Session too short to be saved"
        }
    }

    private fun handleEndGoalReached(durationSeconds: Int) {
        Timber.d("Handling EndGoalReached. Stopping Sound & Notification.")
        // Stop sounds and remove persistent notification
        alertsRepository.onFocusStop()
        stopActiveNotification()

        if (durationSeconds >= 60) {
            saveSessionToDb(durationSeconds)
        } else {
            _errorMessage.value = "Session too short to be saved"
        }
    }

    private fun handleBreakCompleted() {
        Timber.d("Handling BreakCompleted. Triggering Focus Sound.")
        // Play focus start sound
        alertsRepository.onFocusStart(null)
    }

    fun playPreview(soundId: String?) {
        alertsRepository.playPreview(soundId)
    }

    fun stopPreview() {
        alertsRepository.playPreview(null)
    }

    fun updateSessionTask(newTask: String) {
        timerRepository.updateSessionTask(newTask)
    }

    fun updateFocusDuration(minutes: Int) {
        timerRepository.updateFocusDuration(minutes)
    }

    fun updateShortBreakDuration(minutes: Int) {
        timerRepository.updateShortBreakDuration(minutes)
    }

    fun updateLongBreakAndTargetSets(sets: Int, longBreakMins: Int) {
        timerRepository.updateLongBreakAndTargetSets(longBreakMins, sets)
    }

    fun updateBackgroundSound(soundId: String?) {
        val current = timerState.value.timerConfig
        if (current.activeBackgroundSoundId != soundId) {
            Timber.i("Updating Background Sound in TimerConfig: $soundId")
            timerRepository.updateActiveBackgroundSound(soundId)
        }
    }

    fun startTimer(view: View? = null) {
        if (timerState.value.isTimerRunning) return

        viewModelScope.launch {
            timerRepository.start()
            startActiveNotification()
            alertsRepository.onFocusStart(view)
        }
    }

    fun stopTimer(view: View? = null) {
        if (!timerState.value.isTimerRunning) return

        viewModelScope.launch {
            timerRepository.pause()
            stopActiveNotification()
            alertsRepository.onFocusPause(view)
        }
    }

    fun skipToNextMode(view: View? = null) {
        timerRepository.skipToNext()
    }

    fun stopAndDiscardSession(view: View? = null) {
        viewModelScope.launch {
            timerRepository.reset()
            alertsRepository.onFocusStop(view)
            stopActiveNotification()
        }
    }

    fun onLeave(view: View? = null) {
        viewModelScope.launch {
            timerRepository.reset()
            alertsRepository.onLeave(view)
            stopActiveNotification()
        }
    }

    fun stopAndSaveSession(view: View? = null) {
        val state = timerState.value
        val timeSpentSeconds = (state.totalTime - state.currentTime).toInt()

        if (timeSpentSeconds < 60) {
            _errorMessage.value = "Session too short to be saved"
            viewModelScope.launch {
                timerRepository.reset()
                stopActiveNotification()
                alertsRepository.onFocusStop(view)
            }
            return
        }

        if (state.currentMode == TimerMode.Focus) {
            saveSessionToDb(timeSpentSeconds)
        }

        viewModelScope.launch {
            timerRepository.reset()
            stopActiveNotification()
            alertsRepository.onFocusStop(view)
        }
    }

    private fun saveSessionToDb(durationSeconds: Int) {
        viewModelScope.launch {
            val task = timerState.value.timerConfig.sessionTask
            val session = Session(
                sessionId = UUID.randomUUID().toString(),
                duration = durationSeconds,
                timestamp = System.currentTimeMillis(),
                sessionTask = task.ifBlank { "Focus Session" }
            )

            // Persist the completed session to local storage
            when (sessionsRepository.saveSession(session)) {
                is SessionResult.Success -> Timber.i("Session saved successfully")
                is SessionResult.Error -> Timber.e("Failed to save session")
            }
        }
    }

    private fun startActiveNotification() {
        val remainingSeconds = timerState.value.currentTime.toInt()
        val modeName = when (timerState.value.currentMode) {
            TimerMode.Focus -> "Focus"
            TimerMode.ShortBreak -> "Short Break"
            TimerMode.LongBreak -> "Long Break"
        }

        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", true)

        viewModelScope.launch {
            // Show a sticky notification with the current timer status
            notificationRepository.startActiveNotification(
                Notification(
                    id = 1001,
                    title = modeName,
                    message = "Session in progress",
                    type = NotificationType.ACTIVE,
                    isOngoing = true,
                    remainingSeconds = remainingSeconds.toLong()
                )
            )
        }
    }

    private fun stopActiveNotification() {
        viewModelScope.launch {
            notificationRepository.stopActiveNotification()
        }
        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", false)
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}