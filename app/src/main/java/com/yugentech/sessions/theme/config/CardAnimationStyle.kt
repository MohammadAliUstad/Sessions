package com.yugentech.sessions.theme.config

import com.yugentech.sessions.R
import androidx.annotation.StringRes

// The moving background behind the Home session card, in the order Appearance offers them.
// Each one follows the timer: it moves while the timer runs and rests when it's paused.
enum class CardAnimationStyle(@param:StringRes val label: Int) {
    NONE(R.string.card_animation_off),
    // Still SAND underneath, so a card set to the old hourglass carries over
    SAND(R.string.card_animation_sand),
    RAIN(R.string.card_animation_rain),
    CANDLE(R.string.card_animation_candle),
    AURORA(R.string.card_animation_aurora),
    MESH(R.string.card_animation_mesh)
}
