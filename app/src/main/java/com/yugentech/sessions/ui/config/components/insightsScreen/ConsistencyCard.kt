package com.yugentech.sessions.ui.config.components.insightsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun ConsistencyCard(
    streakCount: Int
) {
    val (title, message) = when {
        streakCount == 0 -> "Getting Started" to "Begin your first session to start building a streak"
        streakCount < 3 -> "Building Momentum" to "You're on a $streakCount-day streak. Keep going!"
        streakCount < 7 -> "Strong Start" to "You've maintained a $streakCount-day focus streak. Great progress!"
        streakCount < 14 -> "Habit Forming" to "Impressive $streakCount-day streak! You're building a solid routine."
        streakCount < 30 -> "Committed" to "$streakCount days of consistent focus. You're on fire!"
        else -> "Focus Master" to "Outstanding $streakCount-day streak. Your dedication is remarkable!"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(MaterialTheme.corners.medium)
    ) {
        Column(Modifier.padding(MaterialTheme.spacing.m)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(MaterialTheme.spacing.xs))

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}