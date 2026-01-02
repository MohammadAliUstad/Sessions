package com.yugentech.sessions.ui.dash.components.homeScreen.middle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.timer.TimerMode

@Composable
fun ModeTag(mode: TimerMode) {
    val (text, color) = when (mode) {
        TimerMode.Focus -> "Focus Time" to MaterialTheme.colorScheme.primary
        TimerMode.ShortBreak -> "Short Break" to MaterialTheme.colorScheme.tertiary
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}