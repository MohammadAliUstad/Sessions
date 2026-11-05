package com.yugentech.sessions.ui.dash.homeScreen.layouts.refined

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.modeColor

/** The current mode as a small spaced-out word in its own colour, above the time. */
@Composable
fun RefinedModeTag(
    mode: TimerMode
) {
    val fadeThrough = AppAnimations.Transitions.rememberExpressiveFadeThrough()
    val color = modeColor(mode)
    AnimatedContent(targetState = mode, transitionSpec = { fadeThrough() }, label = "modeTag") { m ->
        Text(
            text = when (m) {
                TimerMode.Focus -> "FOCUS"
                TimerMode.ShortBreak -> "SHORT BREAK"
                TimerMode.LongBreak -> "LONG BREAK"
            },
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = color
        )
    }
}
