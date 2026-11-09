package com.yugentech.sessions.ui.dash.homeScreen.layouts.studio

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import android.text.format.DateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.modeColor
import java.util.Date
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Ticks around the dial, one a minute on a clock face; every fifth is longer. */
private const val TICKS = 60

/**
 * The timer as a watch face: sixty ticks around a quiet disc, filling in the mode's colour as
 * the block runs (the leading tick fills smoothly, not in steps), with a bead riding the edge at
 * the exact point reached. Inside, large light numerals that roll as they change, the mode above
 * them and one quiet line below. A soft light gathers in the face while the clock runs.
 */
@Composable
fun StudioDial(
    seconds: Int,
    progress: Float,
    mode: TimerMode,
    isRunning: Boolean,
    isSessionActive: Boolean,
    errorMessage: String?,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val accent = modeColor(mode)
    val scheme = MaterialTheme.colorScheme
    val shown by animateFloatAsState(progress, tween(700, easing = FastOutSlowInEasing), label = "dialProgress")
    val light by animateFloatAsState(if (isRunning) 1f else 0f, tween(1200), label = "dialLight")
    val bead by animateFloatAsState(if (isSessionActive) 1f else 0f, tween(500), label = "dialBead")
    val breath = if (isRunning) {
        rememberInfiniteTransition(label = "dialBreath").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(3600, easing = LinearEasing), RepeatMode.Reverse),
            label = "dialBreathValue"
        )
    } else {
        null
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawFace(
                faceTop = scheme.surfaceContainerHigh,
                faceBottom = scheme.surfaceContainerLowest,
                rimLight = scheme.onSurface,
                beadRing = scheme.surface,
                track = scheme.onSurface.copy(alpha = 0.12f),
                accent = accent,
                progress = shown,
                light = light * (0.75f + 0.25f * (breath?.value ?: 0f)),
                bead = bead
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                targetState = mode,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(250)) },
                label = "dialMode"
            ) { m ->
                Text(
                    text = when (m) {
                        TimerMode.Focus -> stringResource(R.string.mode_focus).uppercase()
                        TimerMode.ShortBreak -> stringResource(R.string.mode_short_break).uppercase()
                        TimerMode.LongBreak -> stringResource(R.string.mode_long_break).uppercase()
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.5.sp,
                    color = accent
                )
            }

            RollingClock(
                seconds = seconds,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = (size.value * 0.24f).sp,
                    lineHeight = (size.value * 0.27f).sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-1.5).sp,
                    fontFeatureSettings = "tnum"
                ),
                color = scheme.onSurface
            )

            Spacer(Modifier.height(2.dp))

            val line = when {
                errorMessage != null -> errorMessage
                isRunning -> stringResource(R.string.timer_ends, clockTime(seconds))
                isSessionActive -> stringResource(R.string.timer_paused)
                else -> stringResource(R.string.onboarding_focus_idle)
            }
            AnimatedContent(
                targetState = line,
                transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(200)) },
                label = "dialLine"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = if (text == errorMessage) scheme.error else scheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun DrawScope.drawFace(
    faceTop: Color,
    faceBottom: Color,
    rimLight: Color,
    beadRing: Color,
    track: Color,
    accent: Color,
    progress: Float,
    light: Float,
    bead: Float
) {
    val outer = size.minDimension / 2f
    val faceRadius = outer - 10.dp.toPx()
    val top = center.y - faceRadius
    val bottom = center.y + faceRadius

    // The face as soft glass: a translucent surface, a touch lighter at the top, so the page and
    // the glow behind show through a little instead of it sitting there as a flat grey plate
    drawCircle(
        brush = Brush.verticalGradient(
            colors = listOf(faceTop.copy(alpha = 0.62f), faceBottom.copy(alpha = 0.38f)),
            startY = top,
            endY = bottom
        ),
        radius = faceRadius
    )
    // A faint wash of the mode's colour in the middle, deepening while the clock runs
    drawCircle(
        brush = Brush.radialGradient(
            listOf(accent.copy(alpha = 0.07f + 0.12f * light), Color.Transparent),
            center,
            faceRadius
        ),
        radius = faceRadius
    )
    // The rim, lit from above and settling into a hint of the mode's colour below
    drawCircle(
        brush = Brush.verticalGradient(
            colors = listOf(
                rimLight.copy(alpha = 0.22f),
                rimLight.copy(alpha = 0.05f),
                accent.copy(alpha = 0.30f)
            ),
            startY = top,
            endY = bottom
        ),
        radius = faceRadius,
        style = Stroke(1.2.dp.toPx())
    )

    // The ticks, filling from the top clockwise
    val tickOuter = faceRadius - 12.dp.toPx()
    val reached = progress * TICKS
    for (i in 0 until TICKS) {
        val major = i % 5 == 0
        val length = (if (major) 13.dp else 7.dp).toPx()
        val fill = (reached - i).coerceIn(0f, 1f)
        val angle = (i.toFloat() / TICKS) * 2f * PI.toFloat() - PI.toFloat() / 2f
        val dir = Offset(cos(angle), sin(angle))
        drawLine(
            color = lerp(track, accent, fill),
            start = center + dir * (tickOuter - length),
            end = center + dir * tickOuter,
            strokeWidth = (if (major) 2.6.dp else 1.8.dp).toPx(),
            cap = StrokeCap.Round
        )
    }

    // The bead, riding the rim at exactly how far the block has run
    if (bead > 0f) {
        val angle = progress * 2f * PI.toFloat() - PI.toFloat() / 2f
        val at = center + Offset(cos(angle), sin(angle)) * faceRadius
        drawCircle(accent.copy(alpha = 0.22f * bead), 11.dp.toPx() * bead, at)
        // A solid ring under the dot, so it reads crisply over the see-through face
        drawCircle(beadRing, 6.dp.toPx() * bead, at)
        drawCircle(accent.copy(alpha = bead), 4.5.dp.toPx() * bead, at)
    }
}

/** "MM:SS", each digit rolling down into place as it changes, the way a counter turns over. */
@Composable
private fun RollingClock(seconds: Int, style: TextStyle, color: Color) {
    val text = "%02d:%02d".format(seconds / 60, seconds % 60)
    Row {
        text.forEachIndexed { index, char ->
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    (slideInVertically(tween(320)) { -it / 3 } + fadeIn(tween(320)))
                        .togetherWith(slideOutVertically(tween(320)) { it / 3 } + fadeOut(tween(200)))
                        .using(SizeTransform(clip = false))
                },
                label = "dialDigit$index"
            ) { c ->
                Text(text = c.toString(), style = style, color = color)
            }
        }
    }
}

/** The clock time [secondsFromNow] ahead, in the phone's 12 or 24 hour format. */
@Composable
internal fun clockTime(secondsFromNow: Int): String {
    val context = LocalContext.current
    return DateFormat.getTimeFormat(context).format(Date(System.currentTimeMillis() + secondsFromNow * 1000L))
}
