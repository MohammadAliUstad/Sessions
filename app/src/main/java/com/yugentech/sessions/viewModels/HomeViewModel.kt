package com.yugentech.sessions.viewModels

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
import com.yugentech.sessions.timer.TimerConfig
import com.yugentech.sessions.timer.TimerMode
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import com.yugentech.sessions.user.userRepository.UserRepository
import com.yugentech.sessions.utils.HomeUiState
import com.yugentech.sessions.utils.SessionConfig
import com.yugentech.sessions.utils.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

class HomeViewModel(
    private val sessionsRepository: SessionsRepository,
    private val alertsRepository: AlertsRepository,
    private val timerRepository: TimerRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        // 1. Observe Timer Engine -> Update UI
        viewModelScope.launch {
            timerRepository.timerState.collect { timerState ->
                _uiState.update { ui ->
                    ui.copy(
                        // Sync Status
                        status = SessionStatus(
                            isRunning = timerState.isTimerRunning,
                            currentMode = timerState.currentMode,
                            currentTime = timerState.currentTime,
                            totalTime = timerState.totalTime,
                            completedSets = timerState.completedSets // Updated property name
                        ),
                        // Sync Config (Ensures UI always matches Engine)
                        config = SessionConfig(
                            sessionTask = ui.config.sessionTask, // Preserve local task input
                            focusDuration = timerState.config.focusDuration,
                            shortBreakDuration = timerState.config.shortBreakDuration,
                            longBreakDuration = timerState.config.longBreakDuration,
                            targetSets = timerState.config.targetSets, // Updated property name
                            // Loop mode and Sound are removed from Logic,
                            // but if your UI class still has them, we default them here:
                            autoStartNext = false,
                            soundId = null
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

    // --- Configuration Inputs ---

    fun updateSessionTask(newTask: String) {
        _uiState.update { it.copy(config = it.config.copy(sessionTask = newTask)) }
    }

    fun updateDurations(focusMinutes: Int, shortBreakMinutes: Int, longBreakMinutes: Int) {
        pushConfigToTimer(
            focusDuration = focusMinutes * 60 * 1000L,
            shortBreakDuration = shortBreakMinutes * 60 * 1000L,
            longBreakDuration = longBreakMinutes * 60 * 1000L
        )
    }

    fun updateTargetSets(sets: Int) {
        pushConfigToTimer(targetSets = sets)
    }

    // Note: 'toggleAutoStartMode' and 'updateSound' removed as per your request

    private fun pushConfigToTimer(
        focusDuration: Long? = null,
        shortBreakDuration: Long? = null,
        longBreakDuration: Long? = null,
        targetSets: Int? = null
    ) {
        // We get the current config from the ENGINE state, not just the UI state
        // This ensures we don't accidentally overwrite fields we aren't changing
        val current = timerRepository.timerState.value.config

        val newTimerConfig = TimerConfig(
            focusDuration = focusDuration ?: current.focusDuration,
            shortBreakDuration = shortBreakDuration ?: current.shortBreakDuration,
            longBreakDuration = longBreakDuration ?: current.longBreakDuration,
            targetSets = targetSets ?: current.targetSets
            // setsInterval defaults to 4 (or whatever is in TimerConfig default)
            // if we need to change it dynamically, we'd add a parameter here.
        )

        timerRepository.updateConfig(newTimerConfig)
    }

    // --- Timer Controls ---

    fun toggleTimer(view: View? = null) {
        if (_uiState.value.status.isRunning) {
            stopTimer(view, isPause = true)
        } else {
            startTimer(view)
        }
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
            Timber.i(if (isPause) "Pausing session" else "Stopping session")
            clearActiveSessionMonitoring()
            timerRepository.stopTimer() // Service handles Pause vs Reset logic
            alertsRepository.playSessionStopAlert(view)
        }
    }

    // "Discard": Stop and Reset without saving
    fun stopAndDiscardSession(view: View? = null) {
        stopTimer(view, isPause = false)
    }

    // "Finish": Save partial progress and Reset
    fun stopAndSaveSession(view: View? = null) {
        val status = _uiState.value.status
        // Calculate elapsed time (ms -> sec)
        val timeSpentSeconds = ((status.totalTime - status.currentTime) / 1000).toInt()

        // Only save meaningful progress (>0s) in Focus Mode
        if (timeSpentSeconds > 0 && status.currentMode == TimerMode.Focus) {
            val userId = currentUserId ?: timerRepository.getSessionUserId()
            if (userId != null) {
                processAndSaveSession(userId, timeSpentSeconds, view)
            }
        }
        stopTimer(view, isPause = false)
    }

    // --- Session Saving (Backend) ---

    // Triggered by TimerService when a Focus session finishes naturally
    private fun handleTimerComplete(sessionDurationSeconds: Int) {
        val userId = currentUserId ?: timerRepository.getSessionUserId() ?: return
        processAndSaveSession(userId, sessionDurationSeconds, null)
    }

    private fun processAndSaveSession(userId: String, durationSeconds: Int, view: View?) {
        viewModelScope.launch {
            if (view != null) alertsRepository.playSessionStopAlert(view)

            // Trigger a sync to ensure we are up to date before saving new data
            syncPendingSessions(userId)

            val session = Session(
                sessionId = UUID.randomUUID().toString(),
                duration = durationSeconds,
                timestamp = System.currentTimeMillis(),
                sessionTask = _uiState.value.config.sessionTask.ifBlank { "Focus Session" }
            )

            Timber.d("Saving session: ${session.sessionId}, Duration: ${durationSeconds}s")

            when (sessionsRepository.saveSession(userId, session)) {
                is SessionResult.Success -> Timber.i("Session saved successfully")
                is SessionResult.Error -> {
                    Timber.e("Failed to save session")
                    _uiState.update { it.copy(errorMessage = "Failed to save session") }
                }
            }
        }
    }

    // --- Helpers (Notifications & Data) ---

    private fun setupActiveSessionMonitoring() {
        val remainingSeconds = (_uiState.value.status.currentTime / 1000).toInt()
        val taskName = _uiState.value.config.sessionTask.ifBlank { "Focus" }

        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", true)

        viewModelScope.launch {
            notificationRepository.startActiveSession(
                Notification(
                    id = 1001,
                    title = "Sessions",
                    message = "$taskName in progress",
                    type = NotificationType.ACTIVE,
                    isOngoing = true,
                    remainingSeconds = remainingSeconds.toLong()
                )
            )
        }
    }

    private fun clearActiveSessionMonitoring() {
        viewModelScope.launch { notificationRepository.stopActiveSession() }
        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", false)
    }

    fun setUserId(userId: String) {
        currentUserId = userId
        timerRepository.setSessionUserId(userId)
    }

    fun fetchSessionsOnce(userId: String) {
        viewModelScope.launch { sessionsRepository.fetchSessionsOnce(userId) }
    }

    fun fetchUserOnce(userId: String) {
        viewModelScope.launch { userRepository.fetchUserOnce(userId) }
    }

    fun syncPendingSessions(userId: String) {
        viewModelScope.launch { sessionsRepository.syncSessions(userId) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}