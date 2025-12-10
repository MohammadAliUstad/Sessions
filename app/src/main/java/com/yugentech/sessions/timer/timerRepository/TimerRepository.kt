package com.yugentech.sessions.timer.timerRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

// Defines the contract for managing focus timer state and lifecycle
interface TimerRepository {
    // Observable state indicating if the countdown is currently active.
    // Changed to Flow<Boolean> to support mapping from TimerState.
    val isRunning: Flow<Boolean>

    // Observable state of the remaining seconds on the timer
    val currentTime: StateFlow<Int>

    // Updates the total duration for the countdown
    fun setDuration(seconds: Int)

    // Begins the countdown process
    fun startTimer()

    // Pauses or stops the current countdown
    fun stopTimer()

    // Resets the timer to its initial duration
    fun resetTimer()

    // Calculates the total time elapsed since start (Duration - Current)
    fun getElapsedTime(): Int

    // Registers a callback to be invoked when the timer reaches zero
    fun onTimerComplete(listener: (Int) -> Unit)

    // Persists the User ID for the current session to survive configuration changes
    fun setSessionUserId(userId: String)

    // Retrieves the stored User ID for the current session
    fun getSessionUserId(): String?
}