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
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import com.yugentech.sessions.user.userRepository.UserRepository
import com.yugentech.sessions.utils.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    private var sessionStartTime: Long? = null

    init {
        updateSelectedDuration(25)
        // Combine timer state to update UI in real-time
        viewModelScope.launch {
            combine(
                timerRepository.isRunning,
                timerRepository.currentTime
            ) { isRunning, currentTime ->
                _uiState.value.copy(
                    isRunning = isRunning,
                    currentTime = currentTime
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }

        // Register callback for when timer finishes naturally
        timerRepository.onTimerComplete { sessionDuration ->
            handleTimerComplete(sessionDuration)
        }
    }

    // Sets the user context for the session logic
    fun setUserId(userId: String) {
        currentUserId = userId
        timerRepository.setSessionUserId(userId)
    }

    // Triggers a one-time fetch of historical sessions
    fun fetchSessionsOnce(userId: String) {
        viewModelScope.launch {
            sessionsRepository.fetchSessionsOnce(userId)
        }
    }

    // Triggers a one-time fetch of user profile
    fun fetchUserOnce(userId: String) {
        viewModelScope.launch {
            userRepository.fetchUserOnce(userId)
        }
    }

    // Retries uploading any locally saved sessions
    fun syncPendingSessions(userId: String) {
        viewModelScope.launch {
            sessionsRepository.syncSessions(userId)
        }
    }

    // Updates the timer duration based on user selection with validation
    fun updateSelectedDuration(minutes: Int) {
        val durationInSeconds = minutes * 60


        if (!_uiState.value.isRunning) {

            val elapsedTime = timerRepository.getElapsedTime()
            if (elapsedTime > durationInSeconds) {
                Timber.w("Prevented duration change: Elapsed ($elapsedTime) > New Target ($durationInSeconds)")
                _uiState.update {
                    it.copy(errorMessage = "Cannot switch to $minutes min: Time elapsed already exceeds this duration.")
                }
                return
            }

            Timber.d("Updating selected duration to $minutes minutes")
            timerRepository.setDuration(durationInSeconds)
            _uiState.update { it.copy(selectedDuration = durationInSeconds) }
        }
    }

    // Helper to clear error state from UI (call this on Toast dismiss)
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun getElapsedTime(): Int {
        return timerRepository.getElapsedTime()
    }

    // Starts the focus session, service, and sets crashlytics state
    fun startTimer(view: View? = null) {
        if (_uiState.value.isRunning) return

        viewModelScope.launch {
            Timber.i("Starting focus session")
            sessionStartTime = System.currentTimeMillis()

            // Update Crashlytics context to track crashes during active sessions
            FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", true)
            FirebaseCrashlytics.getInstance().setCustomKey("session_duration", _uiState.value.selectedDuration)

            notificationRepository.startActiveSession(
                Notification(
                    id = 1001,
                    title = "Focus",
                    message = "Session in progress",
                    type = NotificationType.ACTIVE,
                    isOngoing = true,
                    remainingSeconds = _uiState.value.selectedDuration
                )
            )

            timerRepository.startTimer()
            alertsRepository.playSessionStartAlert(view)
        }
    }

    // Pauses or stops the timer without saving
    fun stopTimer(view: View? = null) {
        viewModelScope.launch {
            Timber.i("Stopping focus session")
            notificationRepository.stopActiveSession()
            timerRepository.stopTimer()
            alertsRepository.playSessionStopAlert(view)

            FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", false)
        }
    }

    // Stops timer and attempts to save the session data
    fun stopAndSaveSession(view: View? = null) {
        val userId = currentUserId ?: timerRepository.getSessionUserId() ?: return
        val elapsedTime = timerRepository.getElapsedTime()

        if (elapsedTime <= 0) {
            Timber.w("Elapsed time 0, discarding session")
            stopAndDiscardSession(view)
            return
        }

        processAndSaveSession(userId, elapsedTime, view)
    }

    // Stops timer and discards data (e.g. user cancelled)
    fun stopAndDiscardSession(view: View? = null) {
        Timber.i("Discarding session")
        viewModelScope.launch {
            notificationRepository.stopActiveSession()
            alertsRepository.playSessionStopAlert(view)
        }
        resetSessionState()
    }

    // Resets local timer state and crashlytics keys
    fun resetSessionState() {
        timerRepository.stopTimer()
        timerRepository.resetTimer()
        sessionStartTime = null
        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", false)
    }

    // Handles automatic completion when timer hits zero
    private fun handleTimerComplete(sessionDuration: Int) {
        Timber.i("Timer completed naturally")
        val userId = currentUserId ?: timerRepository.getSessionUserId() ?: return
        processAndSaveSession(userId, sessionDuration, null)
    }

    // Internal logic to persist session to repository
    private fun processAndSaveSession(userId: String, duration: Int, view: View?) {
        viewModelScope.launch {
            notificationRepository.stopActiveSession()
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
                    resetSessionState()
                }
                is SessionResult.Error -> {
                    Timber.e("Failed to save session")
                    _uiState.value = _uiState.value.copy(errorMessage = "Failed to save session")
                }
            }
        }
    }
}