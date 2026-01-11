package com.yugentech.sessions.timer

import android.os.CountDownTimer
import com.yugentech.sessions.timer.TimerMode.Focus
import com.yugentech.sessions.timer.TimerMode.LongBreak
import com.yugentech.sessions.timer.TimerMode.ShortBreak
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.math.ceil

class TimerService(
    private val coroutineScope: CoroutineScope
) {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    // Official Android Timer Component
    private var countDownTimer: CountDownTimer? = null

    private var onTimerComplete: ((Int) -> Unit)? = null

    init {
        resetToDefaults()
    }

    // --- Control Methods ---

    fun startTimer() {
        if (_timerState.value.isTimerRunning) return

        // 1. Determine Duration in Milliseconds
        // If resuming, use current seconds * 1000.
        // If fresh start, calculate from config minutes.
        val currentState = _timerState.value

        val durationInMillis = if (currentState.currentTime > 0) {
            currentState.currentTime * 1000L
        } else {
            getDurationMinutes(currentState.currentMode, currentState.config) * 60 * 1000L
        }

        startCountDown(durationInMillis)
    }

    private fun startCountDown(durationInMillis: Long) {
        _timerState.update { it.copy(isTimerRunning = true) }
        Timber.d("Timer started: ${durationInMillis}ms")

        // Cancel any existing timer to be safe
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update State in SECONDS for the UI
                _timerState.update {
                    it.copy(currentTime = millisUntilFinished / 1000)
                }
            }

            override fun onFinish() {
                // Force 0 check
                _timerState.update {
                    it.copy(currentTime = 0, isTimerRunning = false)
                }
                handlePhaseComplete()
            }
        }.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        _timerState.update { it.copy(isTimerRunning = false) }
    }

    fun stopAndReset() {
        stopTimer()
        // Reset to full duration of CURRENT mode (Seconds)
        val currentState = _timerState.value
        val fullDurationSeconds = getDurationMinutes(currentState.currentMode, currentState.config) * 60L

        _timerState.update {
            it.copy(
                currentTime = fullDurationSeconds,
                totalTime = fullDurationSeconds
            )
        }
    }

    // --- CORE LOGIC: The Pit Stop System ---

    private fun handlePhaseComplete() {
        val finishedState = _timerState.value
        val config = finishedState.config

        // 1. Notify Listener (Save Session if it was Focus)
        // Note: totalTime is now in Seconds, so we pass it directly
        if (finishedState.currentMode == Focus) {
            onTimerComplete?.invoke(finishedState.totalTime.toInt())
        }

        // 2. Decide Next Step
        when (finishedState.currentMode) {
            Focus -> {
                val newCompletedSets = finishedState.completedSets + 1

                // A. Check if Session Goal is Reached
                if (newCompletedSets >= config.targetSets) {
                    Timber.i("Target ($newCompletedSets) reached. Stopping session.")
                    transitionTo(
                        mode = Focus,
                        minutes = config.focusDurationMinutes,
                        completedSets = 0, // Reset for fresh start
                        autoStart = false
                    )
                } else {
                    // B. Determine Break Type (100-Minute Rule)

                    // Logic: Calculate how many sets create a ~100 min block
                    val setsPerLongBreak = if (config.focusDurationMinutes > 0) {
                        ceil(100f / config.focusDurationMinutes).toInt()
                    } else 1

                    // 1. Is this a natural stopping point? (e.g. Set 4, 8, 12)
                    val isLongBreakInterval = (newCompletedSets % setsPerLongBreak == 0)

                    // 2. Have we actually earned it? (Total time >= 100m)
                    // (Prevents short 1-minute sessions from triggering long breaks)
                    val totalFocusTime = newCompletedSets * config.focusDurationMinutes
                    val hasEarnedLongBreak = totalFocusTime >= 100

                    if (isLongBreakInterval && hasEarnedLongBreak) {
                        Timber.i("Long Break Triggered (Sets: $newCompletedSets, Time: $totalFocusTime min).")
                        transitionTo(
                            mode = LongBreak,
                            minutes = config.longBreakDurationMinutes,
                            completedSets = newCompletedSets,
                            autoStart = true
                        )
                    } else {
                        Timber.i("Short Break Triggered.")
                        transitionTo(
                            mode = ShortBreak,
                            minutes = config.shortBreakDurationMinutes,
                            completedSets = newCompletedSets,
                            autoStart = true
                        )
                    }
                }
            }
            ShortBreak, LongBreak -> {
                Timber.i("Break Complete. Back to Focus.")
                transitionTo(
                    mode = Focus,
                    minutes = config.focusDurationMinutes,
                    completedSets = finishedState.completedSets,
                    autoStart = true
                )
            }
        }
    }

    private fun transitionTo(mode: TimerMode, minutes: Int, completedSets: Int, autoStart: Boolean) {
        val seconds = minutes * 60L

        _timerState.update {
            it.copy(
                isTimerRunning = autoStart,
                currentMode = mode,
                currentTime = seconds,
                totalTime = seconds,
                completedSets = completedSets
            )
        }

        if (autoStart) {
            startCountDown(seconds * 1000L)
        }
    }

    // --- Configuration Sync ---

    fun updateConfig(newConfig: TimerConfig) {
        _timerState.update { currentState ->
            // Only update display time if we are NOT running (IDLE)
            // and the timer hasn't partially elapsed.
            val isIdleAtStart = !currentState.isTimerRunning && (currentState.currentTime == currentState.totalTime)

            val newMinutes = getDurationMinutes(currentState.currentMode, newConfig)
            val newSeconds = newMinutes * 60L

            val currentMinutes = getDurationMinutes(currentState.currentMode, currentState.config)

            val shouldUpdateDisplay = isIdleAtStart && (newMinutes != currentMinutes)

            currentState.copy(
                config = newConfig,
                totalTime = if (shouldUpdateDisplay) newSeconds else currentState.totalTime,
                currentTime = if (shouldUpdateDisplay) newSeconds else currentState.currentTime
            )
        }
    }

    // --- Helpers ---

    private fun getDurationMinutes(mode: TimerMode, config: TimerConfig): Int {
        return when (mode) {
            Focus -> config.focusDurationMinutes
            ShortBreak -> config.shortBreakDurationMinutes
            LongBreak -> config.longBreakDurationMinutes
        }
    }

    private fun resetToDefaults() {
        val defaultConfig = TimerConfig()
        val defaultSeconds = defaultConfig.focusDurationMinutes * 60L

        _timerState.value = TimerState(
            currentTime = defaultSeconds,
            totalTime = defaultSeconds,
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