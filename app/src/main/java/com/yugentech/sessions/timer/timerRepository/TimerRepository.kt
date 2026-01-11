package com.yugentech.sessions.timer.timerRepository

import com.yugentech.sessions.timer.TimerConfig
import com.yugentech.sessions.timer.TimerState
import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    val timerState: StateFlow<TimerState>
    fun updateConfig(config: TimerConfig)
    fun startTimer()
    fun stopTimer()
    fun stopAndResetTimer()
    fun setOnTimerCompleteListener(listener: (Int) -> Unit)
    fun setSessionUserId(userId: String)
    fun getSessionUserId(): String?
}