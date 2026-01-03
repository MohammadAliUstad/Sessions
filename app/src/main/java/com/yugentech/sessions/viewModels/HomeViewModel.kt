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
        // We map the 'TimerState' (Backend) to 'SessionStatus' & 'SessionConfig' (UI)
        viewModelScope.launch {
            timerRepository.timerState.collect { timerState ->
                _uiState.update { ui ->
                    ui.copy(
                        // Sync Status
                        status = SessionStatus(
                            isRunning = timerState.isTimerRunning,
                            currentMode = timerState.currentMode,
                            currentTime = timerState.currentTime, // Keep as Long (ms)
                            totalTime = timerState.totalTime,     // Keep as Long (ms)
                            completedSets = timerState.completedRounds
                        ),
                        // Sync Config (Ensures UI always matches Engine)
                        config = SessionConfig(
                            sessionTask = ui.config.sessionTask, // Preserve local task input
                            focusDuration = timerState.config.focusDuration,
                            shortBreakDuration = timerState.config.shortBreakDuration,
                            longBreakDuration = timerState.config.longBreakDuration,
                            targetSets = timerState.config.roundsBeforeLongBreak,
                            autoStartNext = timerState.config.autoStartNextFocus,
                            soundId = timerState.config.backgroundSoundId
                        )
                    )
                }
            }
        }

        // 2. Listen for Natural Completion (00:00)
        timerRepository.setOnTimerCompleteListener { sessionDurationSeconds ->
            handleTimerComplete(sessionDurationSeconds)
        }
    }

    // --- Configuration Inputs ---

    fun updateSessionTask(newTask: String) {
        // Task name is local to UI state until saved
        _uiState.update { it.copy(config = it.config.copy(sessionTask = newTask)) }
    }

    fun updateDurations(focusMinutes: Int, shortBreakMinutes: Int, longBreakMinutes: Int) {
        val focusMillis = focusMinutes * 60 * 1000L
        val shortBreakMillis = shortBreakMinutes * 60 * 1000L
        val longBreakMillis = longBreakMinutes * 60 * 1000L

        pushConfigToTimer(
            focusDuration = focusMillis,
            shortBreakDuration = shortBreakMillis,
            longBreakDuration = longBreakMillis
        )
    }

    fun updateTargetSets(sets: Int) {
        pushConfigToTimer(targetSets = sets)
    }

    fun toggleAutoStartMode() {
        val currentAutoStart = _uiState.value.config.autoStartNext
        pushConfigToTimer(autoStartNext = !currentAutoStart)
    }

    fun updateSound(soundId: String?) {
        pushConfigToTimer(soundId = soundId)
    }

    // --- Config Sync Helper ---
    private fun pushConfigToTimer(
        focusDuration: Long? = null,
        shortBreakDuration: Long? = null,
        longBreakDuration: Long? = null,
        targetSets: Int? = null,
        autoStartNext: Boolean? = null,
        soundId: String? = null
    ) {
        val current = _uiState.value.config

        // Construct full TimerConfig from arguments OR fallback to current state
        val newTimerConfig = TimerConfig(
            focusDuration = focusDuration ?: current.focusDuration,
            shortBreakDuration = shortBreakDuration ?: current.shortBreakDuration,
            longBreakDuration = longBreakDuration ?: current.longBreakDuration,
            roundsBeforeLongBreak = targetSets ?: current.targetSets,
            autoStartNextFocus = autoStartNext ?: current.autoStartNext,
            backgroundSoundId = soundId ?: current.soundId
        )

        // Send to Engine
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

            if (isPause) {
                timerRepository.stopTimer() // Fixed: Was stopTimer()
            } else {
                timerRepository.stopTimer()  // Resets session
            }

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
        val totalTime = status.totalTime
        val currentTime = status.currentTime

        // Calculate elapsed time (ms -> sec)
        val timeSpentSeconds = ((totalTime - currentTime) / 1000).toInt()

        // Only save meaningful progress (>0s) in Focus Mode
        if (timeSpentSeconds > 0 && status.currentMode == TimerMode.Focus) {
            val userId = currentUserId ?: timerRepository.getSessionUserId()
            if (userId != null) {
                processAndSaveSession(userId, timeSpentSeconds, view)
            }
        }

        // Reset timer
        stopTimer(view, isPause = false)
    }

    // --- Session Saving (Backend) ---

    // Triggered by TimerService when 00:00 is reached naturally
    private fun handleTimerComplete(sessionDurationSeconds: Int) {
        val userId = currentUserId ?: timerRepository.getSessionUserId() ?: return
        processAndSaveSession(userId, sessionDurationSeconds, null)
    }

    private fun processAndSaveSession(userId: String, durationSeconds: Int, view: View?) {
        viewModelScope.launch {
            // Play sound if manual save (Natural completion sound is handled by Service/AlertsRepo)
            if (view != null) alertsRepository.playSessionStopAlert(view)

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

    // --- Helpers ---

    private fun setupActiveSessionMonitoring() {
        // Display seconds remaining in notifications
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

    // --- Data Fetching ---

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