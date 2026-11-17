package com.yugentech.sessions.ui.config.insightsScreen.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

// The streak lengths the momentum track marks, matching where the streak titles change
private val milestones = listOf(3, 7, 14, 30)
private val dotSize = 26.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    val active = streakCount > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.corners.large))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(MaterialTheme.spacing.m)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StreakBadge(streakCount = streakCount, active = active)

            Spacer(Modifier.width(MaterialTheme.spacing.m))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(MaterialTheme.spacing.xxs))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.l))

        MilestoneTrack(streakCount = streakCount)
    }
}

/** The streak's day count on a cookie shape that slowly turns while there's a streak going. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StreakBadge(streakCount: Int, active: Boolean) {
    val rotation by rememberInfiniteTransition(label = "streakBadge").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(24_000, easing = LinearEasing), RepeatMode.Restart),
        label = "streakBadgeRotation"
    )
    val container = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
    val content = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(84.dp)) {
        // Only the shape turns; the number stays upright on top of it
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { rotationZ = if (active) rotation else 0f }
                .clip(MaterialShapes.Cookie9Sided.toShape())
                .background(container)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = streakCount.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = content
            )
        }
    }
}

/**
 * Four milestones (3, 7, 14 and 30 days) on a track that fills as the streak grows: reached ones
 * are filled with a tick, the next one is ringed, and the rest wait in a muted tone.
 */
@Composable
private fun MilestoneTrack(streakCount: Int) {
    // Where the streak sits along the track, in milestone steps: 0 at the first dot, 3 at the last
    val target = when {
        streakCount >= milestones.last() -> (milestones.size - 1).toFloat()
        streakCount <= milestones.first() -> 0f
        else -> {
            val i = milestones.indexOfLast { it <= streakCount }
            i + (streakCount - milestones[i]).toFloat() / (milestones[i + 1] - milestones[i])
        }
    }
    val fill = remember { Animatable(0f) }
    LaunchedEffect(target) {
        fill.animateTo(target, tween(900, easing = FastOutSlowInEasing))
    }

    val trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val fillColor = MaterialTheme.colorScheme.primary
    val nextIndex = milestones.indexOfFirst { it > streakCount }

    Box(modifier = Modifier.fillMaxWidth()) {
        // The line runs between the first and last dots' centres, behind the dots
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(dotSize)
        ) {
            val step = size.width / milestones.size
            val y = size.height / 2
            val start = step / 2
            val end = size.width - step / 2
            val stroke = 6.dp.toPx()
            drawLine(trackColor, Offset(start, y), Offset(end, y), stroke, StrokeCap.Round)
            if (fill.value > 0f) {
                drawLine(fillColor, Offset(start, y), Offset(start + step * fill.value, y), stroke, StrokeCap.Round)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            milestones.forEachIndexed { index, days ->
                val reached = streakCount >= days
                val isNext = index == nextIndex
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(
                                when {
                                    reached -> MaterialTheme.colorScheme.primary
                                    isNext -> MaterialTheme.colorScheme.surfaceContainerHighest
                                    else -> trackColor
                                }
                            )
                            .then(
                                if (isNext) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                else Modifier
                            )
                    ) {
                        if (reached) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(MaterialTheme.spacing.xs))

                    Text(
                        text = pluralStringResource(R.plurals.profile_streak_days, days, days),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (reached || isNext) FontWeight.Bold else FontWeight.Normal,
                        color = if (reached || isNext) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
