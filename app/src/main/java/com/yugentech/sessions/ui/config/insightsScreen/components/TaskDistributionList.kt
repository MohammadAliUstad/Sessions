package com.yugentech.sessions.ui.config.insightsScreen.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.util.displayTaskName

// One shape per legend entry, so each task keeps a little identity beyond its colour
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private val legendShapes = listOf(
    MaterialShapes.Cookie9Sided,
    MaterialShapes.Clover4Leaf,
    MaterialShapes.Sunny,
    MaterialShapes.Gem,
    MaterialShapes.Flower
)

private const val GAP_DEGREES = 3f

/**
 * Where the focus time went: a donut of the top five tasks (anything else as a muted slice) with
 * the total in the middle, the leading task beside it, and a legend underneath.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskDistributionList(
    taskDistribution: Map<String, Int>
) {
    val top = taskDistribution.toList()
        .sortedByDescending { it.second }
        .take(legendShapes.size)
    val total = taskDistribution.values.sum()
    val rest = total - top.sumOf { it.second }

    val scheme = MaterialTheme.colorScheme
    val palette = listOf(
        scheme.primary,
        scheme.tertiary,
        scheme.secondary,
        scheme.primary.copy(alpha = 0.5f),
        scheme.tertiary.copy(alpha = 0.5f)
    )
    val restColor = scheme.outlineVariant

    // The slices sweep round from twelve o'clock when the card first shows
    val sweep = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        sweep.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.corners.large))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(MaterialTheme.spacing.m)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(136.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 18.dp.toPx()
                    val inset = stroke / 2
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    val slices = top.mapIndexed { i, (_, s) -> s to palette[i] } +
                        (if (rest > 0) listOf(rest to restColor) else emptyList())
                    val gap = if (slices.size > 1) GAP_DEGREES else 0f
                    var start = -90f
                    slices.forEach { (s, color) ->
                        val full = if (total > 0) s * 360f / total else 0f
                        val drawn = ((full - gap) * sweep.value).coerceAtLeast(0f)
                        drawArc(
                            color = color,
                            startAngle = start + gap / 2,
                            sweepAngle = drawn,
                            useCenter = false,
                            topLeft = Offset(inset, inset),
                            size = arcSize,
                            style = Stroke(width = stroke)
                        )
                        start += full * sweep.value
                    }
                }
                Text(
                    text = formatSeconds(total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.width(MaterialTheme.spacing.m))

            // The task that took the most time, and its share
            top.firstOrNull()?.let { (task, s) ->
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.insights_primary_focus),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = displayTaskName(task),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${percentOf(s, total)}%",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = palette[0]
                    )
                }
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.m))

        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)) {
            top.forEachIndexed { index, (task, s) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(legendShapes[index].toShape())
                            .background(palette[index])
                    )
                    Spacer(Modifier.width(MaterialTheme.spacing.sm))
                    Text(
                        text = displayTaskName(task),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(MaterialTheme.spacing.s))
                    Text(
                        text = stringResource(R.string.insights_time_percent, formatSeconds(s), percentOf(s, total)),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun percentOf(part: Int, total: Int): Int =
    if (total > 0) (part * 100f / total).toInt() else 0

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
