package com.yugentech.sessions.timer.timerRepository

import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    val isRunning: StateFlow<Boolean>
    val currentTime: StateFlow<Int>

    fun setDuration(seconds: Int)
    fun startTimer()
    fun stopTimer()
    fun resetTimer()
    fun getElapsedTime(): Int
    fun onTimerComplete(listener: () -> Unit)
}