package com.yugentech.sessions.timer.service

import android.os.SystemClock
import com.yugentech.sessions.timer.config.TimerConfig
import com.yugentech.sessions.timer.effect.TimerEffect
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.timer.state.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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

// Pure countdown engine. Has no knowledge of persistence or configuration storage.
// Config is pushed in via updateConfig() by TimerRepositoryImpl.
class TimerService(
    private val coroutineScope: CoroutineScope
) {

    private var timerJob: Job? = null

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _timerEffects = MutableSharedFlow<TimerEffect>(
        replay = 0,
        extraBufferCapacity = 5
    )
    val timerEffects: SharedFlow<TimerEffect> = _timerEffects.asSharedFlow()

    // Called by TimerRepositoryImpl whenever DataStore emits a new config.
    // If the duration for the current mode changed, cancels any active countdown
    // and resets to the new duration. Non-duration changes (e.g. task name) are
    // applied without disturbing the running timer.
    fun updateConfig(newConfig: TimerConfig) {
        val current = _timerState.value
        val oldDuration = getDurationMinutes(current.currentMode, current.timerConfig)
        val newDuration = getDurationMinutes(current.currentMode, newConfig)

        if (oldDuration != newDuration) {
            val newSeconds = newDuration * 60L
            cancelTimer()
            _timerState.update {
                it.copy(
                    timerConfig = newConfig,
                    totalTime = newSeconds,
                    currentTime = newSeconds,
                    isTimerRunning = false
                )
            }
        } else {
            _timerState.update { it.copy(timerConfig = newConfig) }
        }
    }

    fun start() {
        if (_timerState.value.isTimerRunning) return

        val current = _timerState.value
        val durationMillis = if (current.currentTime > 0 && current.totalTime > 0) {
            current.currentTime * 1000L
        } else {
            val fullDuration = getDurationMinutes(current.currentMode, current.timerConfig) * 60L
            _timerState.update { it.copy(totalTime = fullDuration) }
            fullDuration * 1000L
        }

        startCountdown(durationMillis)
    }

    fun pause() {
        cancelTimer()
        _timerState.update { it.copy(isTimerRunning = false) }
    }

    fun skipToNext() {
        cancelTimer()
        coroutineScope.launch { onTimerComplete() }
    }

    private fun startCountdown(durationMillis: Long) {
        cancelTimer()
        _timerState.update { it.copy(isTimerRunning = true) }

        timerJob = coroutineScope.launch {
            delay(1000)
            val endTime = SystemClock.elapsedRealtime() + durationMillis
            val tickInterval = 100L

            while (isActive) {
                val remaining = endTime - SystemClock.elapsedRealtime()
                if (remaining <= 0) break

                _timerState.update { it.copy(currentTime = (remaining / 1000)) }
                delay(tickInterval)
            }

            if (isActive) {
                _timerState.update { it.copy(currentTime = 0, isTimerRunning = false) }
                onTimerComplete()
            }
        }
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private suspend fun onTimerComplete() {
        val state = _timerState.value
        val config = state.timerConfig
        val actualDurationSeconds = (state.totalTime - state.currentTime).toInt()

        when (state.currentMode) {
            TimerMode.Focus -> handleFocusComplete(config, actualDurationSeconds, state.completedSets)
            TimerMode.ShortBreak, TimerMode.LongBreak -> handleBreakComplete(config, state.completedSets)
        }
    }

    private suspend fun handleFocusComplete(
        config: TimerConfig,
        durationSeconds: Int,
        completedSets: Int
    ) {
        val newCompletedSets = completedSets + 1

        if (newCompletedSets >= config.targetSets) {
            Timber.i("Target reached ($newCompletedSets/${config.targetSets}). Session complete!")
            reset()
            _timerEffects.emit(TimerEffect.EndGoalReached(durationSeconds))
        } else {
            Timber.i("Set $newCompletedSets/${config.targetSets} done. Moving to break.")
            transitionToBreak(config, newCompletedSets)
            _timerEffects.emit(TimerEffect.FocusCompleted(durationSeconds))
        }
    }

    private suspend fun handleBreakComplete(config: TimerConfig, completedSets: Int) {
        transitionAndStart(TimerMode.Focus, config.focusDuration, completedSets)
        _timerEffects.emit(TimerEffect.BreakCompleted)
    }

    private fun transitionToBreak(config: TimerConfig, newCompletedSets: Int) {
        val isLongBreak = (newCompletedSets % config.setsPerLongBreak == 0)
        val (breakMode, breakMinutes) = if (isLongBreak) {
            TimerMode.LongBreak to config.longBreakDuration
        } else {
            TimerMode.ShortBreak to config.shortBreakDuration
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

    fun reset() {
        cancelTimer()
        val config = _timerState.value.timerConfig
        val focusSeconds = config.focusDuration * 60L
        _timerState.update {
            it.copy(
                completedSets = 0,
                currentMode = TimerMode.Focus,
                currentTime = focusSeconds,
                totalTime = focusSeconds,
                isTimerRunning = false
            )
        }
    }

    private fun getDurationMinutes(timerMode: TimerMode, timerConfig: TimerConfig): Int {
        return when (timerMode) {
            TimerMode.Focus -> timerConfig.focusDuration
            TimerMode.ShortBreak -> timerConfig.shortBreakDuration
            TimerMode.LongBreak -> timerConfig.longBreakDuration
        }
    }
}