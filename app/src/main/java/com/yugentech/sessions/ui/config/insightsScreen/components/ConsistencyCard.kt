package com.yugentech.sessions.ui.config.insightsScreen.components
import androidx.compose.ui.res.pluralStringResource
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
        streakCount == 0 -> stringResource(R.string.streak_getting_started) to stringResource(R.string.streak_getting_started_message)
        streakCount < 3 -> stringResource(R.string.streak_building) to pluralStringResource(R.plurals.streak_building_message, streakCount, streakCount)
        streakCount < 7 -> stringResource(R.string.streak_strong_start) to pluralStringResource(R.plurals.streak_strong_start_message, streakCount, streakCount)
        streakCount < 14 -> stringResource(R.string.streak_habit) to pluralStringResource(R.plurals.streak_habit_message, streakCount, streakCount)
        streakCount < 30 -> stringResource(R.string.streak_committed) to pluralStringResource(R.plurals.streak_committed_message, streakCount, streakCount)
        else -> stringResource(R.string.streak_master) to pluralStringResource(R.plurals.streak_master_message, streakCount, streakCount)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        shape = RoundedCornerShape(MaterialTheme.corners.large)
    ) {
        Column(
            Modifier.padding(MaterialTheme.spacing.m)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(MaterialTheme.spacing.xs))

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}