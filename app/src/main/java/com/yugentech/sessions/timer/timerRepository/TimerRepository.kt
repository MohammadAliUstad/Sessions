package com.yugentech.sessions.timer.timerRepository

import com.yugentech.sessions.timer.TimerConfig
import com.yugentech.sessions.timer.TimerState
import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    // 1. The Single Source of Truth
    // The ViewModel observes this to get Time, Mode (Focus/Break), and Status all at once.
    val timerState: StateFlow<TimerState>

    // 2. Configuration
    // Instead of setDuration(int), we pass the full configuration rules.
    fun updateConfig(config: TimerConfig)

    // 3. Controls
    fun startTimer()
    fun stopTimer() // Pauses or Resets based on implementation

    // 4. Lifecycle & Events
    fun setOnTimerCompleteListener(listener: (Int) -> Unit)

    // 5. User Context (Persisted in Repo to survive VM recreation)
    fun setSessionUserId(userId: String)
    fun getSessionUserId(): String?
}