package com.yugentech.sessions.timer.states

sealed class TimerEffect {
    data class FocusCompleted(val durationSeconds: Int) : TimerEffect()
    data class EndGoalReached(val durationSeconds: Int) : TimerEffect()
    data object BreakCompleted : TimerEffect()
}