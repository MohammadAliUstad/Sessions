package com.yugentech.sessions.timer

enum class TimerMode {
    Focus,      // The red/pink state. The user is working.
    ShortBreak, // The green/teal state. A quick 5-min rest.
    LongBreak   // The blue state. A longer 15-min rest.
}