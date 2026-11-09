package com.yugentech.sessions.ui.dash.homeScreen.layouts.shared

import androidx.compose.runtime.Composable
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Immutable
import com.yugentech.sessions.theme.config.CardAnimationStyle
import com.yugentech.sessions.theme.config.CardStyle
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.timer.state.TimerState
import com.yugentech.sessions.ui.dash.homeScreen.components.hero.SessionPulse
import com.yugentech.sessions.ui.dash.state.SessionDashboardState

/**
 * Everything a Home layout shows, gathered once by HomeScreen so every layout draws the same
 * session; they differ only in how it looks.
 */
@Immutable
data class HomeUi(
    val timer: TimerState,
    val dashboard: SessionDashboardState,
    val errorMessage: String?,
    val cardStyle: CardStyle,
    val cardAnimationStyle: CardAnimationStyle
) {
    val config get() = timer.timerConfig
    val isRunning get() = timer.isTimerRunning
    val isSessionActive get() = !timer.isIdle
    val mode get() = timer.currentMode

    /** How far the current block has run, 0..1. */
    val blockProgress: Float
        get() = if (timer.totalTime > 0) {
            (1f - timer.currentTime.toFloat() / timer.totalTime).coerceIn(0f, 1f)
        } else {
            0f
        }

    /** What the session card's effect follows. */
    val pulse get() = SessionPulse(progress = blockProgress, running = isRunning, onBreak = mode != TimerMode.Focus)
}

/** Everything a Home layout can ask for; HomeScreen decides what each one does. */
class HomeActions(
    val onTaskClick: () -> Unit,
    val onToggleAmbient: () -> Unit,
    val onFocusClick: () -> Unit,
    val onShortBreakClick: () -> Unit,
    val onSoundClick: () -> Unit,
    val onSetsClick: () -> Unit,
    val onStartStop: () -> Unit,
    val onDiscard: () -> Unit,
    val onFinish: () -> Unit,
    val onSkip: () -> Unit
)

/** Every focus block plus the breaks between them (none after the last), long ones where the timer puts them. */
fun planMinutes(
    focus: Int,
    shortBreak: Int,
    longBreak: Int,
    sets: Int,
    setsPerLongBreak: Int,
    longBreakEnabled: Boolean
): Int {
    val count = sets.coerceAtLeast(1)
    var total = focus * count
    for (done in 1 until count) {
        val isLong = longBreakEnabled && setsPerLongBreak > 0 && done % setsPerLongBreak == 0
        total += if (isLong) longBreak else shortBreak
    }
    return total
}

/** 95 → "1h 35m", 60 → "1h", 25 → "25m". */
@Composable
fun formatMinutes(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return when {
        h == 0 -> stringResource(R.string.duration_minutes_short, m)
        m == 0 -> stringResource(R.string.duration_hours_short, h)
        else -> stringResource(R.string.duration_hours_minutes_short, h, m)
    }
}
