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
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var onTimerComplete: ((Int) -> Unit)? = null

    init {
        resetToDefaults()
    }

    // --- Control Methods ---

    fun startTimer() {
        if (_timerState.value.isTimerRunning) return

        _timerState.update { it.copy(isTimerRunning = true) }
        Timber.d("Timer started: Mode ${_timerState.value.currentMode}")

        timerJob = coroutineScope.launch {
            while (_timerState.value.isTimerRunning && _timerState.value.currentTime > 0) {
                delay(1000)
                _timerState.update { it.copy(currentTime = it.currentTime - 1000) }
            }

            if (_timerState.value.currentTime <= 0L) {
                handlePhaseComplete()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.update { it.copy(isTimerRunning = false) }
    }

    fun stopTimer() {
        pauseTimer()
        // Reset to full duration of CURRENT mode (user basically cancelled this phase)
        val currentTotal = _timerState.value.totalTime
        _timerState.update { it.copy(currentTime = currentTotal) }
    }

    // --- CORE LOGIC: The Pit Stop System ---

    private fun handlePhaseComplete() {
        timerJob?.cancel()

        val finishedState = _timerState.value
        val config = finishedState.config

        // 1. Notify Listener (Save Session if it was Focus)
        if (finishedState.currentMode == Focus) {
            val seconds = (finishedState.totalTime / 1000).toInt()
            onTimerComplete?.invoke(seconds)
        }

        // 2. Decide Next Step
        when (finishedState.currentMode) {
            Focus -> {
                // Work Finished. Update Progress.
                val newCompletedSets = finishedState.completedSets + 1

                // CHECK 1: Are we completely done?
                val isGoalReached = newCompletedSets >= config.targetSets

                if (isGoalReached) {
                    // SESSION FINISHED. No more breaks.
                    Timber.i("Target ($newCompletedSets) reached. Stopping session.")
                    transitionTo(
                        mode = Focus, // Reset to Focus for next time
                        duration = config.focusDuration,
                        completedSets = 0, // Reset counter
                        autoStart = false  // STOP
                    )
                } else {
                    // Goal NOT reached. We need a break.

                    // CHECK 2: Is it time for a "Pit Stop" (Long Break)?
                    // e.g., if Interval=3, trigger on set 3, 6, 9...
                    val isPitStopTime = (newCompletedSets % config.setsInterval) == 0

                    if (isPitStopTime) {
                        Timber.i("Set $newCompletedSets done. Taking Long Break (Pit Stop).")
                        transitionTo(
                            mode = LongBreak,
                            duration = config.longBreakDuration,
                            completedSets = newCompletedSets,
                            autoStart = true
                        )
                    } else {
                        Timber.i("Set $newCompletedSets done. Taking Short Break.")
                        transitionTo(
                            mode = ShortBreak,
                            duration = config.shortBreakDuration,
                            completedSets = newCompletedSets,
                            autoStart = true
                        )
                    }
                }
            }
            ShortBreak -> {
                // Short Break Done -> Back to Work
                Timber.i("Short Break Complete. Back to Focus.")
                transitionTo(
                    mode = Focus,
                    duration = config.focusDuration,
                    completedSets = finishedState.completedSets,
                    autoStart = true
                )
            }
            LongBreak -> {
                // Long Break Done -> Back to Work (Refreshed!)
                Timber.i("Long Break Complete. Back to Focus.")
                transitionTo(
                    mode = Focus,
                    duration = config.focusDuration,
                    completedSets = finishedState.completedSets,
                    autoStart = true
                )
            }
        }
    }

    private fun transitionTo(mode: TimerMode, duration: Long, completedSets: Int, autoStart: Boolean) {
        _timerState.update {
            it.copy(
                isTimerRunning = autoStart,
                currentMode = mode,
                currentTime = duration,
                totalTime = duration,
                completedSets = completedSets
            )
        }
        if (autoStart) {
            startTimer()
        }
    }

    // --- Configuration Sync ---

    fun updateConfig(newConfig: TimerConfig) {
        _timerState.update { currentState ->
            val isIdleAtStart = !currentState.isTimerRunning && currentState.currentTime == currentState.totalTime

            val shouldUpdateDisplay = isIdleAtStart && (
                    (currentState.currentMode == Focus && newConfig.focusDuration != currentState.config.focusDuration) ||
                            (currentState.currentMode == ShortBreak && newConfig.shortBreakDuration != currentState.config.shortBreakDuration) ||
                            (currentState.currentMode == LongBreak && newConfig.longBreakDuration != currentState.config.longBreakDuration)
                    )

            val newTotalTime = if (shouldUpdateDisplay) {
                when (currentState.currentMode) {
                    Focus -> newConfig.focusDuration
                    ShortBreak -> newConfig.shortBreakDuration
                    LongBreak -> newConfig.longBreakDuration
                }
            } else {
                currentState.totalTime
            }

            currentState.copy(
                config = newConfig,
                totalTime = newTotalTime,
                currentTime = if (shouldUpdateDisplay) newTotalTime else currentState.currentTime
            )
        }
    }

    private fun resetToDefaults() {
        val defaultConfig = TimerConfig()
        _timerState.value = TimerState(
            currentTime = defaultConfig.focusDuration,
            totalTime = defaultConfig.focusDuration,
            config = defaultConfig,
            currentMode = Focus,
            completedSets = 0,
            isTimerRunning = false
        )
    }

    fun setOnTimerCompleteListener(listener: (Int) -> Unit) {
        onTimerComplete = listener
    }
}