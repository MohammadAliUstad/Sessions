package com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.state.ItemStatus
import com.yugentech.sessions.ui.dash.state.SessionVisualItem

/**
 * Every block in the session in order, shared by all three Home cards: focus blocks as bars,
 * short breaks as small circles and long breaks as larger ones. Each mark waits as a dim version
 * of itself and fills with its full colour as its block runs: bars sweep from the left, circles
 * brighten from their centre. Finishing a block (or skipping it) sweeps it full while the next
 * grows from empty, instead of the whole row jumping at once.
 *
 * [progress] is how far the current block has run (0..1); [ink] is the card's text colour.
 */
@Composable
fun SessionTimeline(
    items: List<SessionVisualItem>,
    progress: Float,
    ink: Color,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val spacing = MaterialTheme.spacing

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
    ) {
        items.forEachIndexed { index, item ->
            val (fillColor, trackColor) = when (item) {
                is SessionVisualItem.FocusBlock -> scheme.primary to ink.copy(alpha = 0.1f)
                is SessionVisualItem.ShortBreak -> scheme.tertiary to scheme.tertiary.copy(alpha = 0.3f)
                is SessionVisualItem.LongBreak -> scheme.error to scheme.error.copy(alpha = 0.3f)
            }

            val fill by animateFloatAsState(
                targetValue = when (item.status) {
                    ItemStatus.Completed -> 1f
                    ItemStatus.Current -> progress.coerceIn(0f, 1f)
                    ItemStatus.Upcoming -> 0f
                },
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
                label = "timelineFill$index"
            )

            val markModifier = when (item) {
                is SessionVisualItem.FocusBlock -> Modifier
                    .weight(1f)
                    .height(spacing.xsSmall)

                is SessionVisualItem.ShortBreak -> Modifier.size(spacing.xsSmall)

                is SessionVisualItem.LongBreak -> Modifier.size(spacing.sm)
            }

            Box(
                modifier = markModifier
                    .clip(CircleShape)
                    .background(trackColor)
                    .drawBehind { drawFill(fill, fillColor) }
            )
        }
    }
}

/**
 * [fill] (0..1) of a timeline mark in [color]: bars fill from the left; circles, too short to
 * read as a sweep, grow from their centre instead.
 */
private fun DrawScope.drawFill(fill: Float, color: Color) {
    if (fill <= 0f) return
    if (size.width > size.height * 1.5f) {
        drawRoundRect(
            color = color,
            size = Size((size.width * fill).coerceAtLeast(size.height), size.height),
            cornerRadius = CornerRadius(size.height / 2f),
            alpha = (fill * 6f).coerceAtMost(1f)
        )
    } else {
        drawCircle(color = color, radius = size.minDimension / 2f * fill)
    }
}
