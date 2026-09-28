package com.yugentech.sessions.ui.config.insightsScreen.components.heatMap

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.model.insights.HeatmapDay
import com.yugentech.sessions.ui.dash.util.formatTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Floating card shown above a tapped heatmap cell. Must be placed inside the cell's
 * layout so the popup anchors to it.
 */
@Composable
fun HeatmapDayTooltip(day: HeatmapDay) {
    val gap = with(LocalDensity.current) { 6.dp.roundToPx() }
    val positionProvider = remember(gap) { AboveAnchorPositionProvider(gap) }

    Popup(popupPositionProvider = positionProvider) {
        Surface(
            shape = RoundedCornerShape(MaterialTheme.corners.medium),
            color = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.s,
                    vertical = MaterialTheme.spacing.xs
                )
            ) {
                Text(
                    text = formatTooltipDate(day.date),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = formatFocusTime(day.focusSeconds),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatTooltipDate(date: LocalDate): String {
    val pattern = if (date.year == LocalDate.now().year) "EEE, MMM d" else "EEE, MMM d, yyyy"
    return date.format(DateTimeFormatter.ofPattern(pattern))
}

private fun formatFocusTime(seconds: Long): String = when {
    seconds <= 0L -> "No focus time"
    seconds < 60L -> "< 1 min"
    else -> formatTime(seconds)
}

/**
 * Centres the popup horizontally over the anchor, keeping it within the window.
 * Flips below the anchor if there isn't room above.
 */
private class AboveAnchorPositionProvider(
    private val gap: Int
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val x = (anchorBounds.center.x - popupContentSize.width / 2).coerceIn(0, maxX)

        val above = anchorBounds.top - popupContentSize.height - gap
        val y = if (above >= 0) above else anchorBounds.bottom + gap

        return IntOffset(x, y)
    }
}
