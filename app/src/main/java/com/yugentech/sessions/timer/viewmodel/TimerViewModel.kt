package com.yugentech.sessions.timer.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yugentech.sessions.alerts.repository.AlertsRepository
import com.yugentech.sessions.notification.repository.NotificationRepository
import com.yugentech.sessions.timer.effect.TimerEffect
import com.yugentech.sessions.timer.repository.TimerRepository
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.ui.dash.state.SessionDashboardState
import com.yugentech.sessions.ui.dash.util.SessionDashboardCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

class TimerViewModel(
    private val timerRepository: TimerRepository,
    private val alertsRepository: AlertsRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val timerState = timerRepository.timerState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showGoalReachedDialog = MutableStateFlow(false)
    val showGoalReachedDialog: StateFlow<Boolean> = _showGoalReachedDialog.asStateFlow()

    private val _showFinishConfirmation = MutableStateFlow<Int?>(null)
    val showFinishConfirmation: StateFlow<Int?> = _showFinishConfirmation.asStateFlow()

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
        Timber.d("Handling FocusCompleted.")
        if (durationSeconds < 60) {
            _errorMessage.value = "Session too short to be saved"
            viewModelScope.launch {
                delay(2000.milliseconds)
                _errorMessage.value = null
            }
        }
    }

    private fun handleEndGoalReached(durationSeconds: Int) {
        Timber.d("Handling EndGoalReached.")
        _showGoalReachedDialog.value = true

        if (durationSeconds < 60) {
            _errorMessage.value = "Session too short to be saved"
            viewModelScope.launch {
                delay(2000.milliseconds)
                _errorMessage.value = null
            }
        }
    }

    private fun handleBreakCompleted() {
        Timber.d("Handling BreakCompleted.")
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
            
            // If a valid sound is selected, automatically enable ambient sound
            if (soundId != null && soundId != "none") {
                timerRepository.toggleAmbientSound(true)
            }
        }
    }

    fun toggleAmbientSound() {
        val current = timerState.value.timerConfig
        // If sound is None, we don't toggle enabled state, just stay None
        if (current.activeBackgroundSoundId == null || current.activeBackgroundSoundId == "none") return
        
        timerRepository.toggleAmbientSound(!current.isAmbientEnabled)
    }

    fun startTimer(view: View? = null) {
        if (timerState.value.isTimerRunning) return
        clearErrorMessage()

        viewModelScope.launch {
            // 1. Mark as running and start the internal engine (which has its own 1s delay)
            timerRepository.start()
            
            // 2. Play the start sound/haptic immediately for responsive feedback
            alertsRepository.onFocusStart(view)

            // 3. Match the 1-second delay of the countdown engine
            delay(1000.milliseconds)

            // 4. Only start the foreground service if the session is still active and running
            // This prevents a notification from sticking around if the user rapid-toggled play/pause.
            if (timerState.value.isTimerRunning) {
                startActiveNotification()
            }
        }
    }

    fun stopTimer(view: View? = null) {
        if (!timerState.value.isTimerRunning) return

        viewModelScope.launch {
            timerRepository.pause()
            alertsRepository.onFocusPause(view)
        }
    }

    fun skipToNextMode(view: View? = null) {
        timerRepository.skipToNext()
    }

    fun stopAndDiscardSession(view: View? = null) {
        viewModelScope.launch {
            timerRepository.reset()
            // Alerts are now handled exclusively by the ActiveForeground service 
            // to prevent double haptics/sounds when stopping from the app.
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

        // 1. "Too short" message ONLY in Focus mode
        if (state.currentMode == TimerMode.Focus && timeSpentSeconds < 60) {
            _errorMessage.value = "Session too short to be saved"
            viewModelScope.launch {
                timerRepository.reset()
                // The ActiveForeground service will handle the single onFocusStop haptic.
                stopActiveNotification()
                delay(3000.milliseconds)
                _errorMessage.value = null
            }
            return
        }

        // 2. Check remaining sets for confirmation
        val targetSets = state.timerConfig.targetSets
        val completedSets = state.completedSets
        val setsRemaining = if (state.currentMode == TimerMode.Focus) {
            (targetSets - (completedSets + 1)).coerceAtLeast(0)
        } else {
            (targetSets - completedSets).coerceAtLeast(0)
        }

        if (setsRemaining > 0) {
            _showFinishConfirmation.value = setsRemaining
        } else {
            // Final set: proceed to save and show goal reached
            finishSession(view, showCelebration = true)
        }
    }

    fun confirmFinishSession(view: View? = null) {
        _showFinishConfirmation.value = null
        // User confirmed they want to finish early despite remaining sets
        finishSession(view, showCelebration = false)
    }

    fun dismissFinishConfirmation() {
        _showFinishConfirmation.value = null
    }

    private fun finishSession(view: View? = null, showCelebration: Boolean = true) {
        timerRepository.saveCurrentSession()

        viewModelScope.launch {
            timerRepository.reset()
            if (showCelebration) {
                // Service will handle alertsRepository.onGoalReached(null)
                notificationRepository.finishActiveNotification()
                _showGoalReachedDialog.value = true
            } else {
                // Service will handle alertsRepository.onFocusStop(null)
                notificationRepository.stopActiveNotification()
            }
        }
    }

    private fun startActiveNotification() {
        FirebaseCrashlytics.getInstance().setCustomKey("is_session_active", true)
        viewModelScope.launch {
            notificationRepository.startActiveNotification()
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

    fun dismissGoalReachedDialog() {
        _showGoalReachedDialog.value = false
    }
}
