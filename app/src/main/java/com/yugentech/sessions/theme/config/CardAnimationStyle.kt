package com.yugentech.sessions.theme.config

// The moving background behind the Home session card, in the order Appearance offers them.
// Each one follows the timer: it moves while the timer runs and rests when it's paused.
enum class CardAnimationStyle(val label: String) {
    NONE("Off"),
    // Still SAND underneath, so a card set to the old hourglass carries over
    SAND("Desert crossing"),
    RAIN("Lo-fi rain"),
    CANDLE("Candle & embers"),
    AURORA("Aurora"),
    MESH("Mesh gradient")
}
