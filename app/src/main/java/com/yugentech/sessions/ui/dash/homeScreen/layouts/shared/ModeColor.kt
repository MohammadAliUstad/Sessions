package com.yugentech.sessions.ui.dash.homeScreen.layouts.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.yugentech.sessions.timer.state.TimerMode

/** The colour each mode is drawn in across Home: focus in primary, breaks in their own tones. */
@Composable
fun modeColor(mode: TimerMode): Color {
    val scheme = MaterialTheme.colorScheme
    val target = when (mode) {
        TimerMode.Focus -> scheme.primary
        TimerMode.ShortBreak -> scheme.tertiary
        TimerMode.LongBreak -> scheme.secondary
    }
    return animateColorAsState(target, tween(600), label = "modeColor").value
}

/** What reads on top of [modeColor]: the theme's own on-colour for that role. */
@Composable
fun modeOnColor(mode: TimerMode): Color {
    val scheme = MaterialTheme.colorScheme
    val target = when (mode) {
        TimerMode.Focus -> scheme.onPrimary
        TimerMode.ShortBreak -> scheme.onTertiary
        TimerMode.LongBreak -> scheme.onSecondary
    }
    return animateColorAsState(target, tween(600), label = "modeOnColor").value
}
