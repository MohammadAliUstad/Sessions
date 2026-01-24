package com.yugentech.sessions.timer.timerRepository

import com.yugentech.sessions.timer.states.TimerConfig
import com.yugentech.sessions.timer.states.TimerEffect
import com.yugentech.sessions.timer.states.TimerState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    val timerState: StateFlow<TimerState>
    val timerEffects: SharedFlow<TimerEffect>
    fun start()
    fun pause()
    fun skipToNext()
    fun reset()
    fun updateConfig(timerConfig: TimerConfig)
    fun updateSessionTask(newTask: String)
}