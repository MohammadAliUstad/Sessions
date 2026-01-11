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
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionDashboardCalculator
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionDashboardState
import com.yugentech.sessions.ui.dash.states.SessionConfig
import com.yugentech.sessions.ui.dash.states.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    // ============================================================================================
    // REGION: STATE MANAGEMENT
    // ============================================================================================

    // 1. User Settings (Updates only on user interaction)
    private val _sessionConfig = MutableStateFlow(SessionConfig())
    val sessionConfig: StateFlow<SessionConfig> = _sessionConfig.asStateFlow()

    // 2. Timer Status (Updates every second via engine)
    private val _sessionStatus = MutableStateFlow(SessionStatus())
    val sessionStatus: StateFlow<SessionStatus> = _sessionStatus.asStateFlow()

    // 3. UI Events/Errors
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 4. Derived Dashboard State (Combines Config + Status for UI calculations)
    val dashboardState: StateFlow<SessionDashboardState> = combine(
        _sessionConfig,
        _sessionStatus
    ) { config, status ->
        SessionDashboardCalculator.calculate(config, status)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionDashboardState()
    )

    private var currentUserId: String? = null

    init {
        initializeTimerObserver()
        initializeAlertsObserver()
        initializeSessionCompletionListener()
    }

    private fun initializeTimerObserver() {
        viewModelScope.launch {
            timerRepository.timerState.collect { timerState ->
                _sessionStatus.update {
                    it.copy(
                        isRunning = timerState.isTimerRunning,
                        currentMode = timerState.currentMode,
                        currentTime = timerState.currentTime,
                        totalTime = timerState.totalTime,
                        completedSets = timerState.completedSets
                    )
                }
                // Sync target sets from engine to config to keep them consistent
                _sessionConfig.update { it.copy(targetSets = timerState.config.targetSets) }
            }
        }
    }

    fun playPreview(soundId: String?) {
        alertsRepository.playPreview(soundId)
    }

    fun stopPreview() {
        alertsRepository.playPreview(null)
    }

    private fun initializeAlertsObserver() {
        viewModelScope.launch {
            alertsRepository.alertConfiguration.collect { alertConfig ->
                _sessionConfig.update {
                    it.copy(activeBackgroundSoundId = alertConfig.backgroundSound.id)
                }
            }
        }
    }

    private fun initializeSessionCompletionListener() {
        timerRepository.setOnTimerCompleteListener { sessionDurationSeconds ->
            handleTimerComplete(sessionDurationSeconds)
        }
    }

    fun setUserId(userId: String) {
        currentUserId = userId
        timerRepository.setSessionUserId(userId)
    }

    // ============================================================================================
    // REGION: CONFIGURATION UPDATES
    // ============================================================================================

    fun updateSessionTask(newTask: String) {
        _sessionConfig.update { it.copy(sessionTask = newTask) }
    }

    fun updateFocusDuration(minutes: Int) {
        _sessionConfig.update { it.copy(focusDurationMinutes = minutes) }
        pushTimerConfigUpdate(focusMinutes = minutes)
    }

    fun updateShortBreakDuration(minutes: Int) {
        _sessionConfig.update { it.copy(shortBreakDurationMinutes = minutes) }
        pushTimerConfigUpdate(shortBreakMinutes = minutes)
    }

    fun updateLongBreakDuration(minutes: Int) {
        _sessionConfig.update { it.copy(longBreakDurationMinutes = minutes) }
        pushTimerConfigUpdate(longBreakMinutes = minutes)
    }

    fun updateTargetSets(sets: Int) {
        _sessionConfig.update { it.copy(targetSets = sets) }
        pushTimerConfigUpdate(targetSets = sets)
    }

    fun updateBackgroundSound(soundId: String?) {
        viewModelScope.launch {
            alertsRepository.setBackgroundSound(soundId)
        }
    }

    /** Helper to push changes to the Engine Repository */
    private fun pushTimerConfigUpdate(
        focusMinutes: Int? = null,
        shortBreakMinutes: Int? = null,
        longBreakMinutes: Int? = null,
        targetSets: Int? = null
    ) {
        val currentEngineConfig = timerRepository.timerState.value.config
        val newConfig = TimerConfig(
            focusDurationMinutes = focusMinutes ?: currentEngineConfig.focusDurationMinutes,
            shortBreakDurationMinutes = shortBreakMinutes
                ?: currentEngineConfig.shortBreakDurationMinutes,
            longBreakDurationMinutes = longBreakMinutes
                ?: currentEngineConfig.longBreakDurationMinutes,
            targetSets = targetSets ?: currentEngineConfig.targetSets
        )
        timerRepository.updateConfig(newConfig)
    }

    // ============================================================================================
    // REGION: TIMER CONTROLS
    // ============================================================================================

    fun toggleTimer(view: View? = null) {
        if (_sessionStatus.value.isRunning) {
            stopTimer(view)
        } else {
            startTimer(view)
        }
    }

    private fun startTimer(view: View? = null) {
        if (_sessionStatus.value.isRunning) return
        viewModelScope.launch {
            Timber.i("Starting timer")
            startActiveNotification()
            timerRepository.startTimer()
            alertsRepository.onFocusStart(view)
        }
    }

    private fun stopTimer(view: View? = null) {
        viewModelScope.launch {
            stopActiveNotification()
            timerRepository.stopTimer()
            alertsRepository.onFocusStop(view)
        }
    }

    fun stopAndDiscardSession(view: View? = null) {
        viewModelScope.launch {
            timerRepository.stopAndResetTimer()
            alertsRepository.onFocusStop(view)
        }
    }

    fun stopAndSaveSession(view: View? = null) {
        val status = _sessionStatus.value
        val timeSpentSeconds = ((status.totalTime - status.currentTime) / 1000).toInt()

        // Only save if we actually spent time focusing
        if (timeSpentSeconds > 0 && status.currentMode == TimerMode.Focus) {
            val userId = currentUserId ?: timerRepository.getSessionUserId()
            if (userId != null) {
                saveSessionToDb(userId, timeSpentSeconds)
            }
        }
        stopTimer(view)
    }

    // ============================================================================================
    // REGION: DATA PERSISTENCE & SESSION LOGIC
    // ============================================================================================

    private fun handleTimerComplete(sessionDurationSeconds: Int) {
        val userId = currentUserId ?: timerRepository.getSessionUserId() ?: return

        // Natural completion -> Break Mode (Play "Ding" + Duck Audio)
        alertsRepository.onBreakStart()

        saveSessionToDb(userId, sessionDurationSeconds)
    }

    private fun saveSessionToDb(userId: String, durationSeconds: Int) {
        viewModelScope.launch {
            val session = Session(
                sessionId = UUID.randomUUID().toString(),
                duration = durationSeconds,
                timestamp = System.currentTimeMillis(),
                sessionTask = _sessionConfig.value.sessionTask.ifBlank { "Focus Session" }
            )

            when (sessionsRepository.saveSession(userId, session)) {
                is SessionResult.Success -> Timber.i("Session saved successfully")
                is SessionResult.Error -> _errorMessage.update { "Failed to save session" }
            }
        }
    }

    // ============================================================================================
    // REGION: NOTIFICATIONS & ERROR HANDLING
    // ============================================================================================

    private fun startActiveNotification() {
        val remaining = (_sessionStatus.value.currentTime / 1000).toInt()
        val task = _sessionConfig.value.sessionTask.ifBlank { "Focus" }

        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", true)

        viewModelScope.launch {
            notificationRepository.startActiveNotification(
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

    private fun stopActiveNotification() {
        viewModelScope.launch {
            notificationRepository.stopActiveNotification()
        }
        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", false)
    }
}