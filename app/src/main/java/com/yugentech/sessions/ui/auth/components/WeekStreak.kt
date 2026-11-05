package com.yugentech.sessions.ui.auth.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Days = listOf("M", "T", "W", "T", "F", "S", "S")

/** A week of focus, each day as a share of the tallest bar. */
private val Week = listOf(0.45f, 0.70f, 0.55f, 0.90f, 0.65f, 0.35f, 1.00f)

private val BarWidth = 20.dp
private val BarMaxHeight = 92.dp

/** Time between one day's bar rising and the next. */
private const val DAY_STAGGER_MILLIS = 220L

/**
 * Sign-up's opener: a week of focus building up. Seven bars, Monday to Sunday, rise one after
 * another, each a day's focused minutes, and it all stays put. Plays once when the screen opens. A habit, made a day at a time.
 */
@Composable
fun WeekStreak(modifier: Modifier = Modifier) {
    val rises = remember { List(Days.size) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        for (i in Days.indices) {
            launch { rises[i].animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow)) }
            delay(DAY_STAGGER_MILLIS)
        }
    }

    val week = Week
    val colors = MaterialTheme.colorScheme
    val accent = colors.primary
    val ink = colors.onBackground

    // Only as tall as the bars and day letters, with no empty space above them
    Column(
        modifier = modifier.width(248.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The week: each day a bar rising from the baseline, the tallest in full colour
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(BarMaxHeight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            week.forEachIndexed { i, share ->
                val rise = rises[i].value
                Box(
                    modifier = Modifier
                        .width(BarWidth)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // The day's track, faint, full height
                    Box(
                        modifier = Modifier
                            .width(BarWidth)
                            .fillMaxHeight()
                            .background(ink.copy(alpha = 0.07f), RoundedCornerShape(8.dp))
                    )
                    // The day's focus, rising with a little overshoot
                    Box(
                        modifier = Modifier
                            .width(BarWidth)
                            .fillMaxHeight((share * rise).coerceIn(0.001f, 1f))
                            .graphicsLayer { alpha = rise.coerceIn(0f, 1f) }
                            .background(lerp(ink, accent, 0.35f + 0.65f * share), RoundedCornerShape(8.dp))
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Day letters under the bars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Days.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = ink.copy(alpha = 0.6f),
                    modifier = Modifier.width(BarWidth),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
