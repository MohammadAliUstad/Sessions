package com.yugentech.sessions.timer

import com.yugentech.sessions.timer.TimerMode.Focus
import com.yugentech.sessions.timer.TimerMode.LongBreak
import com.yugentech.sessions.timer.TimerMode.ShortBreak
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class TimerService(
    private val coroutineScope: CoroutineScope
) {
    // 1. We now hold the specific 'TimerConfig' (Rules) and 'TimerState' (Live Data)
    // Initialize with default config
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null

    // Callback to tell ViewModel "Hey, a session just finished!"
    private var onTimerComplete: ((Int) -> Unit)? = null

    init {
        // Initialize the timer with the default config values
        resetToDefaults()
    }

    fun startTimer() {
        if (_timerState.value.isTimerRunning) return

        _timerState.update { it.copy(isTimerRunning = true) }
        Timber.d("Timer started in mode: ${_timerState.value.currentMode}")

        timerJob = coroutineScope.launch {
            while (_timerState.value.isTimerRunning && _timerState.value.currentTime > 0) {
                delay(1000)

                // Tick down
                _timerState.update {
                    it.copy(currentTime = it.currentTime - 1000L)
                }
            }

            // Check why loop ended: Did we hit 0?
            if (_timerState.value.currentTime <= 0L) {
                handleSessionComplete()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.update { it.copy(isTimerRunning = false) }
        Timber.d("Timer paused")
    }

    fun stopTimer() {
        pauseTimer()
        // Reset to the beginning of the CURRENT session type
        val currentTotal = _timerState.value.totalTime
        _timerState.update { it.copy(currentTime = currentTotal) }
    }

    // Handles the "Next Step" logic (Focus -> Break)
    private fun handleSessionComplete() {
        timerJob?.cancel()
        val completedState = _timerState.value

        // Notify ViewModel to save the session (Only save Focus sessions usually)
        if (completedState.currentMode == Focus) {
            onTimerComplete?.invoke((completedState.totalTime / 1000).toInt())
        }

        // CALCULATE NEXT STATE
        val config = completedState.config
        val nextMode: TimerMode
        val nextTime: Long
        val nextRounds: Int

        if (completedState.currentMode == Focus) {
            // We just finished work. What break do we earn?
            nextRounds = completedState.completedRounds + 1
            if (nextRounds >= config.roundsBeforeLongBreak) {
                nextMode = LongBreak
                nextTime = config.longBreakDuration
            } else {
                nextMode = ShortBreak
                nextTime = config.shortBreakDuration
            }
        } else {
            // We just finished a break. Back to work!
            nextMode = Focus
            nextTime = config.focusDuration
            // If we just finished a long break, reset rounds? (Optional choice)
            nextRounds =
                if (completedState.currentMode == LongBreak) 0 else completedState.completedRounds
        }

        // Apply the new state
        _timerState.update {
            it.copy(
                isTimerRunning = config.autoStartNext, // Auto-start if config says so
                currentMode = nextMode,
                currentTime = nextTime,
                totalTime = nextTime,
                completedRounds = nextRounds
            )
        }

        Timber.i("Session Complete. Switching to $nextMode. AutoStart: ${config.autoStartNext}")

        // If auto-start is on, recurse start
        if (config.autoStartNext) {
            startTimer()
        }
    }

    // Call this when user changes settings in UI
    fun updateConfig(newConfig: TimerConfig) {
        // If timer is running, we might not want to disturb it,
        // OR we might want to update real-time.
        // For safety, let's only update the "Next" settings unless we are Idle.

        _timerState.update {
            // If we are currently IDLE (full time remaining), update the current display too
            val isIdle = it.currentTime == it.totalTime && !it.isTimerRunning

            it.copy(
                config = newConfig,
                // If we were just sitting at 25:00 and user changed it to 50:00, update the display
                currentTime = if (isIdle && it.currentMode == Focus) newConfig.focusDuration else it.currentTime,
                totalTime = if (isIdle && it.currentMode == Focus) newConfig.focusDuration else it.totalTime
            )
        }
    }

    private fun resetToDefaults() {
        val defaultConfig = TimerConfig()
        _timerState.value = TimerState(
            currentTime = defaultConfig.focusDuration,
            totalTime = defaultConfig.focusDuration,
            config = defaultConfig,
            currentMode = Focus
        )
    }

    fun setOnTimerCompleteListener(listener: (Int) -> Unit) {
        onTimerComplete = listener
    }
}