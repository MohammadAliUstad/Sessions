package com.yugentech.sessions.timer.timerRepository

import com.yugentech.sessions.timer.states.TimerEffect
import com.yugentech.sessions.timer.states.TimerState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    // Observable stream of the current timer state
    val timerState: StateFlow<TimerState>

    // Observable stream for one-off timer events (like completion)
    val timerEffects: SharedFlow<TimerEffect>

    // Control methods for the timer logic
    fun start()
    fun pause()
    fun skipToNext()
    fun reset()

    // Methods to update timer settings and persist them
    fun updateSessionTask(newTask: String)
    fun updateFocusDuration(duration: Int)
    fun updateShortBreakDuration(duration: Int)
    fun updateLongBreakAndTargetSets(duration: Int, sets: Int)
    fun updateActiveBackgroundSound(soundId: String?)
}