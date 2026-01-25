package com.yugentech.sessions.timer.states

// Represents one-time events that happen when a timer finishes or a goal is met
sealed class TimerEffect {
    data class FocusCompleted(val durationSeconds: Int) : TimerEffect()
    data class EndGoalReached(val durationSeconds: Int) : TimerEffect()
    data object BreakCompleted : TimerEffect()
}