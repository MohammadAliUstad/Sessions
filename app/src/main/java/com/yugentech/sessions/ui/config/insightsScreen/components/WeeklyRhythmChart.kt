package com.yugentech.sessions.ui.config.insightsScreen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.time.temporal.WeekFields

/**
 * Focus time per weekday as a bar chart. The week starts where the user's locale starts it, the
 * busiest day is picked to begin with, and tapping a day shows its time and share up top.
 */
@Composable
fun WeeklyRhythmChart(
    dailyVolume: Map<Int, Int>
) {
    val locale = LocalConfiguration.current.locales[0]
    // dailyVolume is keyed like Calendar.DAY_OF_WEEK: Sunday is 1, Saturday 7
    val days = remember(locale) {
        val first = WeekFields.of(locale).firstDayOfWeek
        (0L until 7L).map { first.plus(it) }
    }
    val seconds = days.map { dailyVolume[calendarKey(it)] ?: 0 }
    val total = seconds.sum()
    val max = seconds.maxOrNull() ?: 0
    val busiest = if (max > 0) seconds.indexOf(max) else 0

    var selected by rememberSaveable(busiest) { mutableIntStateOf(busiest) }

    // Bars grow in from the baseline when the chart first shows
    val grow = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        grow.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.corners.large))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(MaterialTheme.spacing.m)
    ) {
        // The picked day, written out, with its time and share of the week
        val pickedSeconds = seconds[selected]
        val share = if (total > 0) (pickedSeconds * 100f / total).toInt() else 0
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = days[selected].getDisplayName(TextStyle.FULL, locale)
                    .replaceFirstChar { it.titlecase(locale) },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.insights_time_percent, formatSeconds(pickedSeconds), share),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.m))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
        ) {
            days.forEachIndexed { index, day ->
                val isSelected = index == selected
                val fraction = if (max > 0) seconds[index].toFloat() / max else 0f
                val barColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    label = "barColor"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "dayLabelColor"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { selected = index },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // A full-height track with the day's bar rising inside it
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(MaterialTheme.corners.medium))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (fraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight((fraction * grow.value).coerceAtLeast(0.04f))
                                    .clip(RoundedCornerShape(MaterialTheme.corners.medium))
                                    .background(barColor)
                            )
                        }
                    }

                    Spacer(Modifier.height(MaterialTheme.spacing.xs))

                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, locale)
                            .replaceFirstChar { it.titlecase(locale) },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = labelColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/** Calendar.DAY_OF_WEEK for a [DayOfWeek]: Sunday 1, Monday 2 … Saturday 7. */
private fun calendarKey(day: DayOfWeek): Int = day.value % 7 + 1

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
