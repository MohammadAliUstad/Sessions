package com.yugentech.sessions.ui.auth.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/** Material's password eye (path_password_eye), on its 24-unit viewport. */
private const val EYE_PATH =
    "M12,4.5C7,4.5 2.73,7.61 1,12c1.73,4.39 6,7.5 11,7.5s9.27,-3.11 11,-7.5c-1.73,-4.39 -6,-7.5 -11,-7.5z" +
        "M12,17c-2.76,0 -5,-2.24 -5,-5s2.24,-5 5,-5 5,2.24 5,5 -2.24,5 -5,5z" +
        "M12,9c-1.66,0 -3,1.34 -3,3s1.34,3 3,3 3,-1.34 3,-3 -1.34,-3 -3,-3z"

private const val VIEWPORT = 24f
private const val STRIKE_WIDTH = 1.8f
private val StrikeStart = Offset(3.27f, 4.27f)
private val StrikeEnd = Offset(19.74f, 20.74f)

/** The two mask corners that slide along the strike: cut out beside it when hidden, gone when shown. */
private val MaskCornerAHidden = Offset(19.73f, 22f)
private val MaskCornerBHidden = Offset(22.27f, 19.46f)
private val MaskCornerAShown = Offset(2f, 4.27f)
private val MaskCornerBShown = Offset(4.54f, 1.73f)

/** Material's show_password_duration and hide_password_duration. */
private const val SHOW_MILLIS = 200
private const val HIDE_MILLIS = 320

/**
 * The password field's show/hide eye, drawn in Compose as on Hajime: the same trim-and-clip
 * animation as asl_trimclip_eye (a strike draws across the eye as the password hides and wipes
 * back as it shows, with a gap cut beside it).
 *
 * It used to play that drawable in an ImageView through AndroidView. An Android View inside the
 * password field joins the window's own focus handling, so moving between fields briefly took
 * focus away from the text editor and the keyboard closed and opened again. Drawn on a Canvas,
 * the field is pure Compose and the keyboard stays up.
 */
@Composable
fun AnimatedEyeIcon(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    // 1 while the password shows (open eye), 0 while it's hidden (struck through).
    val shown by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = if (isVisible) {
            tween(SHOW_MILLIS, easing = FastOutLinearInEasing)
        } else {
            tween(HIDE_MILLIS, easing = FastOutSlowInEasing)
        },
        label = "eye"
    )
    val eye = remember { PathParser().parsePathString(EYE_PATH).toPath() }
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    val description = if (isVisible) "Hide password" else "Show password"

    Canvas(modifier = modifier.semantics { contentDescription = description }) {
        scale(size.minDimension / VIEWPORT, pivot = Offset.Zero) {
            // trimPathEnd on the strike: full when hidden, nothing when shown.
            val strikeEnd = lerp(StrikeEnd, StrikeStart, shown)
            if (shown < 1f) {
                drawLine(
                    color = color,
                    start = StrikeStart,
                    end = strikeEnd,
                    strokeWidth = STRIKE_WIDTH,
                    cap = StrokeCap.Square
                )
            }

            val cornerA = lerp(MaskCornerAHidden, MaskCornerAShown, shown)
            val cornerB = lerp(MaskCornerBHidden, MaskCornerBShown, shown)
            val mask = Path().apply {
                moveTo(2f, 4.27f)
                lineTo(cornerA.x, cornerA.y)
                lineTo(cornerB.x, cornerB.y)
                lineTo(4.54f, 1.73f)
                lineTo(4.54f, 1f)
                lineTo(23f, 1f)
                lineTo(23f, 23f)
                lineTo(1f, 23f)
                lineTo(1f, 4.27f)
                close()
            }
            clipPath(mask) {
                drawPath(path = eye, color = color)
            }
        }
    }
}

private fun lerp(start: Offset, stop: Offset, fraction: Float) = start + (stop - start) * fraction
