package com.yugentech.sessions.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class TimerService(
    private val coroutineScope: CoroutineScope
) {
    // 1. Explicit State Tracking
    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _currentTime = MutableStateFlow(0)
    val currentTime: StateFlow<Int> = _currentTime.asStateFlow()

    private var timerJob: Job? = null
    private var duration = 0
    private var onTimerComplete: ((Int) -> Unit)? = null

    fun start() {
        if (_timerState.value == TimerState.RUNNING) return

        _timerState.value = TimerState.RUNNING
        Timber.d("Timer service started")

        timerJob = coroutineScope.launch {
            while (_currentTime.value > 0 && _timerState.value == TimerState.RUNNING) {
                delay(1000)
                if (_timerState.value != TimerState.RUNNING) break

                _currentTime.value -= 1
            }

            if (_currentTime.value <= 0) {
                Timber.i("Timer reached zero")
                handleCompletion()
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null

        // If we stopped while running, we are now PAUSED
        if (_timerState.value == TimerState.RUNNING) {
            _timerState.value = TimerState.PAUSED
            Timber.d("Timer paused")
        }
    }

    fun reset() {
        stop()
        _timerState.value = TimerState.IDLE
        _currentTime.value = duration
        Timber.i("Timer reset to IDLE")
    }

    private fun handleCompletion() {
        _timerState.value = TimerState.FINISHED
        onTimerComplete?.invoke(duration)
        timerJob?.cancel()
        timerJob = null
    }

    // 2. The Logic Fix
    fun setDuration(seconds: Int) {
        val currentState = _timerState.value

        // CASE 1: Fresh Start
        // If we are IDLE (App start/Reset) or FINISHED (Session done),
        // we always treat this as a NEW session -> Elapsed is 0.
        if (currentState == TimerState.IDLE || currentState == TimerState.FINISHED) {
            duration = seconds
            _currentTime.value = seconds
            _timerState.value = TimerState.IDLE // Ensure we go back to IDLE
            return
        }

        // CASE 2: Resizing a Paused Session
        // Only preserve elapsed time if we are explicitly PAUSED.
        if (currentState == TimerState.PAUSED) {
            val elapsed = duration - _currentTime.value
            duration = seconds
            // Recalculate remaining based on new duration - old elapsed
            val newRemaining = (duration - elapsed).coerceAtLeast(0)

            _currentTime.value = newRemaining
            Timber.d("Resized paused session. New Duration: $seconds, Preserved Elapsed: $elapsed")
        }
    }

    fun getElapsedTime(): Int {
        // Optional: If IDLE, elapsed is technically 0
        if (_timerState.value == TimerState.IDLE) return 0
        return duration - _currentTime.value
    }

    fun setOnTimerCompleteListener(listener: (Int) -> Unit) {
        onTimerComplete = listener
    }
}