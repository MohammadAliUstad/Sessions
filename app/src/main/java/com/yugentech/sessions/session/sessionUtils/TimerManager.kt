package com.yugentech.sessions.session.sessionUtils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerManager {

    private var onTimerComplete: (() -> Unit)? = null

    fun setOnTimerCompleteListener(listener: () -> Unit) {
        onTimerComplete = listener
    }

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _currentTime = MutableStateFlow(0)
    val currentTime: StateFlow<Int> = _currentTime

    private var timerJob: Job? = null
    private var duration = 0

    fun setDuration(seconds: Int) {
        duration = seconds
        _currentTime.value = duration
    }

    fun start() {
        if (_isRunning.value) return

        _isRunning.value = true
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (_currentTime.value > 0 && _isRunning.value) {
                delay(1000)
                _currentTime.update { it - 1 }
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
}