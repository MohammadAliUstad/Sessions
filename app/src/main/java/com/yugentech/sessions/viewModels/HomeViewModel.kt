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
        // 1. Observe the new Rich TimerState from the Repository
        viewModelScope.launch {
            timerRepository.timerState.collect { timerState ->
                _uiState.update { ui ->
                    ui.copy(
                        isRunning = timerState.isTimerRunning,
                        // Convert ms to seconds for the UI
                        currentTime = (timerState.currentTime / 1000).toInt(),
                        selectedDuration = (timerState.totalTime / 1000).toInt(),
                        // Optional: map timerState.currentMode to a UI property if you added one
                        // e.g. isBreakMode = timerState.currentMode != TimerMode.Focus
                    )
                }
            }
        }

        // 2. Listen for natural timer completion (handled by Service)
        timerRepository.setOnTimerCompleteListener { sessionDurationSeconds ->
            handleTimerComplete(sessionDurationSeconds)
        }
    }

    // --- Configuration ---

    // Replaces updateSelectedDuration
    // Call this from your new BottomSheet when user picks times
    fun updateConfig(focusMinutes: Int, shortBreakMinutes: Int, longBreakMinutes: Int) {
        val currentConfig = timerRepository.timerState.value.config

        // Create updated config
        val newConfig = currentConfig.copy(
            focusDuration = focusMinutes * 60 * 1000L,
            shortBreakDuration = shortBreakMinutes * 60 * 1000L,
            longBreakDuration = longBreakMinutes * 60 * 1000L
        )

        Timber.d("Updating config: Focus=$focusMinutes, SB=$shortBreakMinutes, LB=$longBreakMinutes")
        timerRepository.updateConfig(newConfig)
    }

    fun updateSessionTask(newTask: String) {
        _uiState.update { it.copy(sessionTask = newTask) }
    }

//     Add this helper to HomeViewModel.kt if you haven't yet:
 fun updateFullConfig(newConfig: TimerConfig) {
     timerRepository.updateConfig(newConfig)
 }

    // --- Timer Controls ---

    fun toggleTimer(view: View? = null) {
        if (_uiState.value.isRunning) {
            stopTimer(view) // Or pauseTimer(view) if you prefer pause behavior
        } else {
            startTimer(view)
        }
    }

    fun startTimer(view: View? = null) {
        if (_uiState.value.isRunning) return

        viewModelScope.launch {
            Timber.i("Starting session")

            // Crashlytics & Notifications
            setupActiveSessionMonitoring()

            timerRepository.startTimer()
            alertsRepository.playSessionStartAlert(view)
        }
    }

    fun stopTimer(view: View? = null) {
        viewModelScope.launch {
            Timber.i("Stopping/Pausing session")

            // Cleanup monitoring
            clearActiveSessionMonitoring()

            // Decide: Do you want 'Stop' to PAUSE or RESET?
            // timerRepository.pauseTimer() // If you want to resume later
            timerRepository.stopTimer()     // If you want to reset to start of session

            alertsRepository.playSessionStopAlert(view)
        }
    }

    // Discard current progress and reset
    fun stopAndDiscardSession(view: View? = null) {
        Timber.i("Discarding session")
        stopTimer(view) // stopTimer now resets internally in the service
    }

    // Stops timer and attempts to save the session data
    fun stopAndSaveSession(view: View? = null) {
        stopTimer()
    }

    // --- Session Saving Logic ---

    // Called automatically by TimerService when countdown hits 0
    private fun handleTimerComplete(sessionDuration: Int) {
        Timber.i("Timer completed naturally: $sessionDuration seconds")
        val userId = currentUserId ?: timerRepository.getSessionUserId() ?: return

        // Only save if it was a FOCUS session (Service filters this, but safety check doesn't hurt)
        // You can check _uiState or timerState here if needed.

        processAndSaveSession(userId, sessionDuration, null)
    }

    private fun processAndSaveSession(userId: String, duration: Int, view: View?) {
        viewModelScope.launch {
            clearActiveSessionMonitoring()
            alertsRepository.playSessionStopAlert(view)
            syncPendingSessions(userId)

            val session = Session(
                sessionId = UUID.randomUUID().toString(),
                duration = duration,
                timestamp = System.currentTimeMillis()
            )

            Timber.d("Saving session: ${session.sessionId}, duration: $duration")

            when (sessionsRepository.saveSession(userId, session)) {
                is SessionResult.Success -> {
                    Timber.i("Session saved successfully")
                    // No need to call resetSessionState(); TimerService handles the transition to Break automatically!
                }
                is SessionResult.Error -> {
                    Timber.e("Failed to save session")
                    _uiState.update { it.copy(errorMessage = "Failed to save session") }
                }
            }
        }
    }

    // --- Helpers ---

    private fun setupActiveSessionMonitoring() {
        val durationSeconds = _uiState.value.selectedDuration
        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", true)
        FirebaseCrashlytics.getInstance().setCustomKey("session_duration", durationSeconds)

        viewModelScope.launch {
            notificationRepository.startActiveSession(
                Notification(
                    id = 1001,
                    title = "Sessions",
                    message = "Focus is active", // You can customize this based on Focus/Break mode later
                    type = NotificationType.ACTIVE,
                    isOngoing = true,
                    remainingSeconds = durationSeconds
                )
            )
        }
    }

    private fun clearActiveSessionMonitoring() {
        viewModelScope.launch {
            notificationRepository.stopActiveSession()
        }
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