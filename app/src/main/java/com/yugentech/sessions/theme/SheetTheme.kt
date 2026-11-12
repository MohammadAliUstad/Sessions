package com.yugentech.sessions.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// Whether AMOLED black is showing right now (it's on and the app is dark). SessionsTheme
// provides it.
internal val LocalAmoledActive = staticCompositionLocalOf { false }

// How the sheet's edge is drawn: the corner it follows, how thick, and how far down the sides it
// runs before fading out.
private val SheetCornerRadius = 28.dp
private val SheetEdgeWidth = 1.5.dp
private val SheetEdgeFade = 96.dp
private const val SheetEdgeAlpha = 0.55f

/**
 * Wrap a bottom sheet in this. With AMOLED black showing, the sheet goes black like the screens
 * behind it; pair it with [sheetEdge] on the sheet so its outline still sets it apart. Otherwise
 * it changes nothing.
 */
@Composable
fun SheetTheme(content: @Composable () -> Unit) {
    if (LocalAmoledActive.current) {
        MaterialTheme(
            // surfaceContainerLow is the sheet's container colour
            colorScheme = MaterialTheme.colorScheme.copy(surfaceContainerLow = Color.Black),
            content = content
        )
    } else {
        content()
    }
}

/**
 * With AMOLED black showing, a thin line in the theme's primary colour along the sheet's rounded
 * top edge, fading out as it runs down the sides, so a black sheet still stands out from the black
 * screen behind it. Draws nothing otherwise. Goes on the ModalBottomSheet's own modifier, with
 * that sheet's [sheetState]: the modifier covers the sheet's full-height track, and the sheet sits
 * [SheetState.requireOffset] down it, so that's where the edge is drawn. Read while drawing, so
 * the edge follows the sheet as it's dragged.
 */
@OptIn(ExperimentalMaterial3Api::class)
fun Modifier.sheetEdge(sheetState: SheetState): Modifier = composed {
    if (!LocalAmoledActive.current) return@composed this
    val color = MaterialTheme.colorScheme.primary.copy(alpha = SheetEdgeAlpha)

    drawWithContent {
        drawContent()

        // Not laid out yet, so there's no sheet to outline
        val sheetTop = runCatching { sheetState.requireOffset() }.getOrNull() ?: return@drawWithContent
        val stroke = SheetEdgeWidth.toPx()
        val inset = stroke / 2f
        val radius = SheetCornerRadius.toPx()
        val top = sheetTop + inset
        val fade = sheetTop + SheetEdgeFade.toPx().coerceAtMost(size.height - sheetTop)
        val left = inset
        val right = size.width - inset

        // Up the left side, round the top corners and down the right side
        val path = Path().apply {
            moveTo(left, fade)
            lineTo(left, top + radius)
            arcTo(Rect(left, top, left + 2 * radius, top + 2 * radius), 180f, 90f, false)
            lineTo(right - radius, top)
            arcTo(Rect(right - 2 * radius, top, right, top + 2 * radius), 270f, 90f, false)
            lineTo(right, fade)
        }
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                0f to color,
                (radius / (fade - sheetTop)).coerceIn(0f, 1f) to color,
                1f to Color.Transparent,
                startY = sheetTop,
                endY = fade
            ),
            style = Stroke(width = stroke)
        )
    }
}
