package com.yugentech.sessions.timer

data class TimerState(
    // 1. The actual milliseconds ticking down (e.g., 24:59, 24:58...)
    val currentTime: Long = 0L,

    // 2. The total time of this specific session.
    // CRITICAL: We need this to calculate the progress circle.
    // Progress = currentTime / totalTime
    val totalTime: Long = 0L,

    // 3. Is the clock moving?
    // Controls the Play/Pause button icon.
    val isTimerRunning: Boolean = false,

    // 4. Which "Phase" are we in? (Focus, Break, etc.)
    val currentMode: TimerMode = TimerMode.Focus,

    // 5. How many "Focus" sessions have we finished in this cycle?
    // Used to decide when to trigger the "Long Break".
    val completedRounds: Int = 0,

    // 6. The User's Settings (Nested here for easy access)
    // The state "owns" the config, so the UI only needs to observe one object.
    val config: TimerConfig = TimerConfig()
)