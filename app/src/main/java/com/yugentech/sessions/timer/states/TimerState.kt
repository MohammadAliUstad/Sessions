package com.yugentech.sessions.timer.states

data class TimerState(
    val currentTime: Long = 25 * 60,
    val totalTime: Long = 0L,
    val isTimerRunning: Boolean = false,
    val currentMode: TimerMode = TimerMode.Focus,
    val completedSets: Int = 0,
    val timerConfig: TimerConfig = TimerConfig()
) {
    val isPaused: Boolean
        get() = !isTimerRunning && totalTime > 0 && currentTime > 0 && currentTime < totalTime

    val isIdle: Boolean
        get() = !isTimerRunning && (totalTime == 0L || currentTime == totalTime)
}