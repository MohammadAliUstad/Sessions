package com.yugentech.sessions.timer

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yugentech.sessions.alerts.alertsDatastore.AlertsRepository
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionDashboardCalculator
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionDashboardState
import com.yugentech.sessions.utils.HomeUiState
import com.yugentech.sessions.utils.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

class TimerViewModel(
    private val timerRepository: TimerRepository,
    private val sessionsRepository: SessionsRepository,
    private val alertsRepository: AlertsRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val dashboardState: StateFlow<SessionDashboardState> = combine(
        uiState.map { it.config },
        uiState.map { it.status }
    ) { config, status ->
        SessionDashboardCalculator.calculate(config, status)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionDashboardState())

    private var currentUserId: String? = null

    init {
        // 1. Observe Timer Engine -> Update UI State
        viewModelScope.launch {
            timerRepository.timerState.collect { timerState ->
                _uiState.update { ui ->
                    ui.copy(
                        status = SessionStatus(
                            isRunning = timerState.isTimerRunning,
                            currentMode = timerState.currentMode,
                            currentTime = timerState.currentTime,
                            totalTime = timerState.totalTime,
                            completedSets = timerState.completedSets
                        ),
                        // IMPORTANT: Use copy() to preserve user's manual settings (minutes)
                        // while syncing the targetSets from the engine.
                        config = ui.config.copy(
                            targetSets = timerState.config.targetSets
                        )
                    )
                }
            }
        }

        // 2. Listen for Session Completion (Save Data)
        timerRepository.setOnTimerCompleteListener { sessionDurationSeconds ->
            handleTimerComplete(sessionDurationSeconds)
        }
    }

    fun setUserId(userId: String) {
        currentUserId = userId
        timerRepository.setSessionUserId(userId)
    }

    // --- Separated Configuration Inputs ---

    fun updateSessionTask(newTask: String) {
        _uiState.update { it.copy(config = it.config.copy(sessionTask = newTask)) }
    }

    fun updateFocusDuration(minutes: Int) {
        // 1. Update UI instantly
        _uiState.update {
            it.copy(config = it.config.copy(focusDurationMinutes = minutes))
        }
        // 2. Update Engine
        updateTimerConfig(focusMinutes = minutes)
    }

    fun updateShortBreakDuration(minutes: Int) {
        _uiState.update {
            it.copy(config = it.config.copy(shortBreakDurationMinutes = minutes))
        }
        updateTimerConfig(shortBreakMinutes = minutes)
    }

    fun updateLongBreakDuration(minutes: Int) {
        _uiState.update {
            it.copy(config = it.config.copy(longBreakDurationMinutes = minutes))
        }
        updateTimerConfig(longBreakMinutes = minutes)
    }

    fun updateTargetSets(sets: Int) {
        _uiState.update {
            it.copy(config = it.config.copy(targetSets = sets))
        }
        updateTimerConfig(targetSets = sets)
    }

    /**
     * Helper to push changes to the Repository.
     * Only parameters passed as non-null will be updated.
     */
    private fun updateTimerConfig(
        focusMinutes: Int? = null,
        shortBreakMinutes: Int? = null,
        longBreakMinutes: Int? = null,
        targetSets: Int? = null
    ) {
        val current = timerRepository.timerState.value.config

        val newConfig = TimerConfig(
            focusDurationMinutes = focusMinutes ?: current.focusDurationMinutes,
            shortBreakDurationMinutes = shortBreakMinutes ?: current.shortBreakDurationMinutes,
            longBreakDurationMinutes = longBreakMinutes ?: current.longBreakDurationMinutes,
            targetSets = targetSets ?: current.targetSets
        )

        timerRepository.updateConfig(newConfig)
    }

    // --- Timer Controls ---

    fun toggleTimer(view: View? = null) {
        if (_uiState.value.status.isRunning) stopTimer(view, isPause = true)
        else startTimer(view)
    }

    private fun startTimer(view: View? = null) {
        if (_uiState.value.status.isRunning) return
        viewModelScope.launch {
            Timber.i("Starting timer")
            setupActiveSessionMonitoring()
            timerRepository.startTimer()
            alertsRepository.playSessionStartAlert(view)
        }
    }

    private fun stopTimer(view: View? = null, isPause: Boolean = false) {
        viewModelScope.launch {
            clearActiveSessionMonitoring()
            timerRepository.stopTimer()
            alertsRepository.playSessionStopAlert(view)
        }
    }

    fun stopAndDiscardSession(view: View? = null) {
        viewModelScope.launch {
            timerRepository.stopAndResetTimer()
        }
    }

    fun stopAndSaveSession(view: View? = null) {
        val status = _uiState.value.status
        val timeSpentSeconds = ((status.totalTime - status.currentTime) / 1000).toInt()

        if (timeSpentSeconds > 0 && status.currentMode == TimerMode.Focus) {
            val userId = currentUserId ?: timerRepository.getSessionUserId()
            if (userId != null) {
                processAndSaveSession(userId, timeSpentSeconds, view)
            }
        }
        stopTimer(view, isPause = false)
    }

    // --- Session Saving Logic ---

    private fun handleTimerComplete(sessionDurationSeconds: Int) {
        val userId = currentUserId ?: timerRepository.getSessionUserId() ?: return
        processAndSaveSession(userId, sessionDurationSeconds, null)
    }

    private fun processAndSaveSession(userId: String, durationSeconds: Int, view: View?) {
        viewModelScope.launch {
            if (view != null) alertsRepository.playSessionStopAlert(view)

            val session = Session(
                sessionId = UUID.randomUUID().toString(),
                duration = durationSeconds,
                timestamp = System.currentTimeMillis(),
                sessionTask = _uiState.value.config.sessionTask.ifBlank { "Focus Session" }
            )

            when (sessionsRepository.saveSession(userId, session)) {
                is SessionResult.Success -> Timber.i("Session saved")
                is SessionResult.Error -> _uiState.update { it.copy(errorMessage = "Failed to save") }
            }
        }
    }

    // --- Notifications ---

    private fun setupActiveSessionMonitoring() {
        val remaining = (_uiState.value.status.currentTime / 1000).toInt()
        val task = _uiState.value.config.sessionTask.ifBlank { "Focus" }

        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", true)

        viewModelScope.launch {
            notificationRepository.startActiveSession(
                Notification(
                    id = 1001,
                    title = "Sessions",
                    message = "$task in progress",
                    type = NotificationType.ACTIVE,
                    isOngoing = true,
                    remainingSeconds = remaining.toLong()
                )
            )
        }
    }

    private fun clearActiveSessionMonitoring() {
        viewModelScope.launch { notificationRepository.stopActiveSession() }
        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", false)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}