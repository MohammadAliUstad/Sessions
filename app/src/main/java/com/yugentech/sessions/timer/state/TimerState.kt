package com.yugentech.sessions.timer.states

// Represents the real-time snapshot of the timer, including current time and status
data class TimerState(
    val currentTime: Long = 25 * 60,
    val totalTime: Long = 0L,
    val isTimerRunning: Boolean = false,
    val currentMode: TimerMode = TimerMode.Focus,
    val completedSets: Int = 0,
    val timerConfig: TimerConfig = TimerConfig()
) {
    // Returns true if the timer has started but is currently stopped mid-way
    val isPaused: Boolean
        get() = !isTimerRunning && totalTime > 0 && currentTime > 0 && currentTime < totalTime

    // Returns true if the timer hasn't started or has fully reset
    val isIdle: Boolean
        get() = !isTimerRunning && (totalTime == 0L || currentTime == totalTime)
}