package com.yugentech.sessions.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerService(
    private val coroutineScope: CoroutineScope
) {
    private var onTimerComplete: (() -> Unit)? = null

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _currentTime = MutableStateFlow(0)
    val currentTime: StateFlow<Int> = _currentTime

    private var timerJob: Job? = null
    private var duration = 0

    fun setDuration(seconds: Int) {
        val elapsed = duration - _currentTime.value
        duration = seconds
        val newRemaining = duration - elapsed
        _currentTime.value = if (newRemaining > 0) newRemaining else 0
    }


    fun start() {
        if (_isRunning.value) return

        _isRunning.value = true
        timerJob = coroutineScope.launch {
            while (_currentTime.value > 0 && _isRunning.value) {
                delay(1000)
                _currentTime.value = _currentTime.value - 1
            }
            if (_currentTime.value <= 0) {
                stop()
                onTimerComplete?.invoke()
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
        _isRunning.value = false
    }

    fun reset() {
        stop()
        _currentTime.value = duration
    }

    fun getElapsedTime(): Int = duration - _currentTime.value

    fun setOnTimerCompleteListener(listener: () -> Unit) {
        onTimerComplete = listener
    }
}
