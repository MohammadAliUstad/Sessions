package com.yugentech.sessions.ui.config.insightsScreen.components

import java.time.format.TextStyle
import java.time.DayOfWeek
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.remember
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun WeeklyRhythmChart(
    dailyVolume: Map<Int, Int>
) {
    // Sunday first, as dailyVolume counts them; each day named in the app's language
    val locale = LocalConfiguration.current.locales[0]
    val days = remember(locale) {
        listOf(
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        ).map { day -> day.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.titlecase(locale) } }
    }
    val totalVolume = dailyVolume.values.sum().toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.corners.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.m),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
        ) {
            days.forEachIndexed { index, dayName ->
                val durationSeconds = dailyVolume[index + 1] ?: 0
                val percentage = if (totalVolume > 0) durationSeconds / totalVolume else 0f
                val timeText = formatSeconds(durationSeconds)
                val displayText = stringResource(R.string.insights_time_percent, timeText, (percentage * 100).toInt())

                TaskProgressItem(
                    label = dayName,
                    progress = percentage,
                    valueText = displayText
                )
            }
        }
    }
}

@Composable
private fun formatSeconds(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) {
        stringResource(R.string.duration_hours_minutes_short, hours, minutes)
    } else {
        stringResource(R.string.duration_minutes_short, minutes)
    }
}