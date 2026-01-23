package com.yugentech.sessions.timer

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yugentech.sessions.alerts.alertsRepository.AlertsRepository
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import com.yugentech.sessions.timer.states.TimerEffect
import com.yugentech.sessions.timer.states.TimerMode
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import com.yugentech.sessions.ui.dash.utils.SessionDashboardCalculator
import com.yugentech.sessions.ui.dash.states.SessionDashboardState
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

    // ============================================================================================
    // STATE
    // ============================================================================================

    val timerState = timerRepository.timerState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val dashboardState: StateFlow<SessionDashboardState> = timerState
        .map { state -> SessionDashboardCalculator.calculate(state) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SessionDashboardState()
        )

    init {
        observeTimerEffects()
        observeAlertConfiguration()
    }

    // ============================================================================================
    // TIMER EFFECTS
    // ============================================================================================

    private fun observeTimerEffects() {
        viewModelScope.launch {
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
        // 1. SOUND LOGIC (Always runs immediately)
        alertsRepository.onBreakStart()

        Timber.d("Saving chunk to DB ($durationSeconds s)")
        // 2. DATA LOGIC (Runs in parallel, ID handled by Repository)
        saveSessionToDb(durationSeconds)
    }

    private fun handleBreakCompleted() {
        Timber.d("Handling BreakCompleted. Triggering Focus Sound.")
        alertsRepository.onFocusStart(null)
    }

    private fun handleEndGoalReached(durationSeconds: Int) {
        Timber.d("Handling EndGoalReached. Stopping Sound & Notification.")
        // 1. SOUND & UI LOGIC (Always runs immediately)
        alertsRepository.onFocusStop()
        stopActiveNotification()

        Timber.d("Saving final session to DB ($durationSeconds s)")
        // 2. DATA LOGIC (Runs in parallel, ID handled by Repository)
        saveSessionToDb(durationSeconds)
    }

    // ============================================================================================
    // ALERT CONFIGURATION
    // ============================================================================================

    private fun observeAlertConfiguration() {
        viewModelScope.launch {
            alertsRepository.alertConfiguration.collect { alertConfig ->
                updateBackgroundSoundId(alertConfig.backgroundSound.id)
            }
        }
    }

    // ============================================================================================
    // SOUND PREVIEW
    // ============================================================================================

    fun playPreview(soundId: String?) {
        alertsRepository.playPreview(soundId)
    }

    fun stopPreview() {
        alertsRepository.playPreview(null)
    }

    // ============================================================================================
    // CONFIGURATION
    // ============================================================================================

    // In TimerViewModel.kt

    fun updateSessionTask(newTask: String) {
        timerRepository.updateSessionTask(newTask)
    }

    fun updateFocusDuration(minutes: Int) {
        val current = timerState.value.timerConfig
        timerRepository.updateConfig(current.copy(focusDuration = minutes))
    }

    fun updateShortBreakDuration(minutes: Int) {
        val current = timerState.value.timerConfig
        timerRepository.updateConfig(current.copy(shortBreakDuration = minutes))
    }

    fun updateLongBreakDuration(minutes: Int) {
        val current = timerState.value.timerConfig
        timerRepository.updateConfig(current.copy(longBreakDuration = minutes))
    }

    fun updateTargetSets(sets: Int) {
        val current = timerState.value.timerConfig
        timerRepository.updateConfig(current.copy(targetSets = sets))
    }

    fun updateBackgroundSound(soundId: String?) {
        viewModelScope.launch {
            alertsRepository.setBackgroundSound(soundId)
        }
    }

    private fun updateBackgroundSoundId(soundId: String?) {
        val current = timerState.value.timerConfig
        if (current.activeBackgroundSoundId != soundId) {
            timerRepository.updateConfig(current.copy(activeBackgroundSoundId = soundId))
        }
    }

    // ============================================================================================
    // TIMER CONTROLS
    // ============================================================================================

    fun startTimer(view: View? = null) {
        if (timerState.value.isTimerRunning) return

        viewModelScope.launch {
            // Start timer first, then trigger alerts
            timerRepository.start()
            startActiveNotification()
            alertsRepository.onFocusStart(view)
        }
    }

    fun stopTimer(view: View? = null) {
        if (!timerState.value.isTimerRunning) return

        viewModelScope.launch {
            // Stop timer first, then stop alerts
            timerRepository.pause()
            stopActiveNotification()
            alertsRepository.onFocusPause(view)
        }
    }

    fun onFocusStop(view: View? = null) {
        alertsRepository.onFocusStop(view)
    }

    fun stopAndDiscardSession(view: View? = null) {
        viewModelScope.launch {
            timerRepository.reset()
            stopActiveNotification()
        }
    }

    fun stopAndSaveSession(view: View? = null) {
        val state = timerState.value
        val timeSpentSeconds = (state.totalTime - state.currentTime).toInt()

        // 1. Trigger Save (if valid focus time)
        if (timeSpentSeconds > 0 && state.currentMode == TimerMode.Focus) {
            saveSessionToDb(timeSpentSeconds)
        }

        // 2. Reset Timer & UI
        viewModelScope.launch {
            timerRepository.reset()
            stopActiveNotification()
            alertsRepository.onFocusStop(view)
        }
    }

    // ============================================================================================
    // DATA PERSISTENCE
    // ============================================================================================

    private fun saveSessionToDb(durationSeconds: Int) {
        viewModelScope.launch {
            val task = timerState.value.timerConfig.sessionTask
            val session = Session(
                sessionId = UUID.randomUUID().toString(),
                duration = durationSeconds,
                timestamp = System.currentTimeMillis(),
                sessionTask = task.ifBlank { "Focus Session" }
            )

            when (sessionsRepository.saveSession(session)) {
                is SessionResult.Success -> Timber.i("Session saved successfully")
                is SessionResult.Error -> Timber.e("Failed to save session (User might be logged out)")
            }
        }
    }

    // ============================================================================================
    // NOTIFICATIONS
    // ============================================================================================

    private fun startActiveNotification() {
        val remainingSeconds = timerState.value.currentTime.toInt()
        val task = timerState.value.timerConfig.sessionTask.ifBlank { "Focus" }

        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", true)

        viewModelScope.launch {
            notificationRepository.startActiveNotification(
                Notification(
                    id = 1001,
                    title = "Sessions",
                    message = "$task in progress",
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
}