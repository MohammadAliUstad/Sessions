package com.yugentech.sessions.timer

data class TimerConfig(
    // 1. How long is a "Work" session? (default 25 mins)
    val focusDuration: Long = 25 * 60 * 1000L,

    // 2. How long is a "Short Break"? (default 5 mins)
    val shortBreakDuration: Long = 5 * 60 * 1000L,

    // 3. How long is a "Long Break"? (default 15 mins)
    val longBreakDuration: Long = 15 * 60 * 1000L,

    // 4. How many work sessions before the Long Break happens? (default 4)
    val roundsBeforeLongBreak: Int = 4,

    // 5. Should the timer start automatically when a session ends?
    // Good for "Hardcore Mode" users who don't want to click "Start" every time.
    val autoStartNext: Boolean = false,
    
    // 6. Which background sound to play? (We will use IDs or Strings later)
    val backgroundSoundId: String? = null
)