package com.yugentech.sessions.timer

import android.os.SystemClock
import com.yugentech.sessions.timer.states.TimerConfig
import com.yugentech.sessions.timer.states.TimerEffect
import com.yugentech.sessions.timer.states.TimerMode
import com.yugentech.sessions.timer.states.TimerMode.Focus
import com.yugentech.sessions.timer.states.TimerMode.LongBreak
import com.yugentech.sessions.timer.states.TimerMode.ShortBreak
import com.yugentech.sessions.timer.states.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

class TimerService(
    private val scope: CoroutineScope
) {

    // ------------------------------------------------------------------------
    // State & Effects
    // ------------------------------------------------------------------------

    private var timerJob: Job? = null

    private val _timerState = MutableStateFlow(createState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _timerEffects = MutableSharedFlow<TimerEffect>(replay = 0)
    val timerEffects: SharedFlow<TimerEffect> = _timerEffects.asSharedFlow()

    // ------------------------------------------------------------------------
    // Public Controls (UI API)
    // ------------------------------------------------------------------------

    fun start() {
        if (_timerState.value.isTimerRunning) return

        val current = _timerState.value
        val durationMillis = if (current.currentTime > 0) {
            current.currentTime * 1000L
        } else {
            getDurationMinutes(current.currentMode, current.timerConfig) * 60 * 1000L
        }

        startCountdown(durationMillis)
    }

    fun pause() {
        cancelTimer()
        _timerState.update { it.copy(isTimerRunning = false) }
    }

    fun reset() {
        cancelTimer()
        val current = _timerState.value
        val fullDurationSeconds = getDurationMinutes(
            timerMode = current.currentMode,
            timerConfig = current.timerConfig
        ) * 60L

        _timerState.update {
            it.copy(
                currentTime = fullDurationSeconds,
                totalTime = fullDurationSeconds,
                isTimerRunning = false
            )
        }
    }

    fun discardSession() {
        cancelTimer()

        val config = _timerState.value.timerConfig
        val focusSeconds = config.focusDuration * 60L

        _timerState.update {
            it.copy(
                completedSets = 0,
                currentMode = Focus,
                currentTime = focusSeconds,
                totalTime = focusSeconds,
                isTimerRunning = false
            )
        }
    }

    fun updateConfig(config: TimerConfig) {
        _timerState.update { current ->
            val isIdle = !current.isTimerRunning && (current.currentTime == current.totalTime)
            val newMinutes = getDurationMinutes(current.currentMode, config)
            val newSeconds = newMinutes * 60L
            val currentMinutes = getDurationMinutes(current.currentMode, current.timerConfig)
            val shouldUpdateDisplay = isIdle && (newMinutes != currentMinutes)

            current.copy(
                timerConfig = config,
                totalTime = if (shouldUpdateDisplay) newSeconds else current.totalTime,
                currentTime = if (shouldUpdateDisplay) newSeconds else current.currentTime
            )
        }
    }

    // Timer Engine
    private fun startCountdown(durationMillis: Long) {
        cancelTimer()

        _timerState.update { it.copy(isTimerRunning = true) }

        timerJob = scope.launch {
            delay(1000)

            val endTime = SystemClock.elapsedRealtime() + durationMillis
            val tickInterval = 100L

            while (isActive) {
                val remaining = endTime - SystemClock.elapsedRealtime()

                if (remaining <= 0) {
                    break
                }

                _timerState.update {
                    it.copy(currentTime = (remaining / 1000))
                }

                delay(tickInterval)
            }

            if (isActive) {
                _timerState.update {
                    it.copy(
                        currentTime = 0,
                        isTimerRunning = false
                    )
                }

                onTimerComplete()
            }
        }
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private suspend fun onTimerComplete() {
        val timerState = _timerState.value
        val timerConfig = timerState.timerConfig
        val durationSeconds = timerState.totalTime.toInt()

        when (timerState.currentMode) {
            Focus ->
                handleFocusComplete(
                    timerConfig,
                    durationSeconds,
                    timerState.completedSets
                )

            ShortBreak, LongBreak ->
                handleBreakComplete(
                    timerConfig,
                    timerState.completedSets
                )
        }
    }

    // Pomodoro Machine
    private suspend fun handleFocusComplete(timerConfig: TimerConfig, durationSeconds: Int, completedSets: Int) {
        Timber.i("Focus session completed: ${durationSeconds}s")
        val newCompletedSets = completedSets + 1

        if (newCompletedSets >= timerConfig.targetSets) {
            Timber.i("Target reached ($newCompletedSets/${timerConfig.targetSets} sets). Session complete!")
            resetState(timerConfig)
            emitEffect(TimerEffect.EndGoalReached(durationSeconds))
        } else {
            Timber.i("Completed set $newCompletedSets/${timerConfig.targetSets}. Moving to break.")
            transitionToBreak(timerConfig, newCompletedSets)
            emitEffect(TimerEffect.FocusCompleted(durationSeconds))
        }
    }

    private suspend fun handleBreakComplete(config: TimerConfig, completedSets: Int) {
        Timber.i("Break Complete. Returning to Focus.")
        transitionAndStart(Focus, config.focusDuration, completedSets)
        emitEffect(TimerEffect.BreakCompleted)
    }

    private fun transitionToBreak(config: TimerConfig, newCompletedSets: Int) {
        val setsBeforeLast = (config.targetSets - 1).coerceAtLeast(0)
        val timeBeforeLastSet = setsBeforeLast * config.focusDuration
        val isEligibleForLongBreak = timeBeforeLastSet >= 100
        val isLongBreakInterval = (newCompletedSets % config.setsPerLongBreak == 0)
        val shouldTriggerLongBreak = isLongBreakInterval && isEligibleForLongBreak

        val (breakMode, breakMinutes) = if (shouldTriggerLongBreak) {
            Timber.i("Long Break triggered!")
            LongBreak to config.longBreakDuration
        } else {
            Timber.i("Short Break triggered")
            ShortBreak to config.shortBreakDuration
        }

        transitionAndStart(breakMode, breakMinutes, newCompletedSets)
    }

    private fun transitionAndStart(timerMode: TimerMode, minutes: Int, completedSets: Int) {
        val seconds = minutes * 60L

        _timerState.update {
            it.copy(
                currentMode = timerMode,
                currentTime = seconds,
                totalTime = seconds,
                completedSets = completedSets,
                isTimerRunning = false
            )
        }

        startCountdown(seconds * 1000L)
    }


    // Effects
    private suspend fun emitEffect(timerEffect: TimerEffect) {
        _timerEffects.emit(timerEffect)
    }

    // Helpers
    private fun createState(): TimerState {
        val timerConfig = TimerConfig()
        val seconds = timerConfig.focusDuration * 60L
        return TimerState(
            currentTime = seconds,
            totalTime = seconds,
            timerConfig = timerConfig,
            currentMode = Focus,
            completedSets = 0,
            isTimerRunning = false
        )
    }

    private fun resetState(timerConfig: TimerConfig) {
        val focusSeconds = timerConfig.focusDuration * 60L
        _timerState.update {
            it.copy(
                completedSets = 0,
                currentMode = Focus,
                currentTime = focusSeconds,
                totalTime = focusSeconds,
                isTimerRunning = false
            )
        }
    }

    private fun getDurationMinutes(timerMode: TimerMode, timerConfig: TimerConfig): Int {
        return when (timerMode) {
            Focus -> timerConfig.focusDuration
            ShortBreak -> timerConfig.shortBreakDuration
            LongBreak -> timerConfig.longBreakDuration
        }
    }

    fun clear() {
        cancelTimer()
        scope.cancel()
    }
}