package com.yugentech.sessions.ui.dash.components.homeScreen.middle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.theme.tokens.strokes
import com.yugentech.sessions.timer.states.TimerMode

@Composable
fun ModeTag(
    mode: TimerMode
) {
    val (text, color) = when (mode) {
        TimerMode.Focus -> "Focus Time" to MaterialTheme.colorScheme.primary
        TimerMode.ShortBreak -> "Short Break" to MaterialTheme.colorScheme.tertiary
        TimerMode.LongBreak -> "Long Break" to MaterialTheme.colorScheme.secondary
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = BorderStroke(MaterialTheme.strokes.thin, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.sm,
                vertical = MaterialTheme.spacing.xsSmall
            )
        )
    }
}