package com.yugentech.sessions.ui.dash.homeScreen.layouts.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.timer.state.TimerMode
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin

private val TAU = (2 * PI).toFloat()

/** Thin vertical slices the fade is built from, so its lower edge can roll like a horizon. */
private const val SLICES = 64

/** How finely each slice's fade is sampled; a smooth curve in many small steps never bands. */
private const val STEPS = 12

/** How far the light sweep travels (in dp of flow) between one pass and the next. */
private const val SWEEP_EVERY_DP = 520f

/** How the glow behaves in each state of the timer. */
private class Mood(
    /** How strong the colour is, before the light/dark adjustment. */
    val strength: Float,
    /** How fast everything flows, in dp a second. */
    val speed: Float,
    /** How far down the glow reaches, as a share of its area, before the horizon rolls it. */
    val reach: Float
)

private val Idle = Mood(strength = 0.27f, speed = 38f, reach = 0.58f)
private val Paused = Mood(strength = 0.22f, speed = 18f, reach = 0.58f)
private val Running = Mood(strength = 0.35f, speed = 72f, reach = 0.68f)

/**
 * A living glow across the top of the screen in the selected theme's own colours, built only from
 * straight gradients, never shapes:
 *  - two currents of the theme's colours streaming sideways in opposite directions,
 *  - a soft diagonal band of light gliding across every so often,
 *  - and a lower edge that rolls like a horizon, two slow waves layered so its outline is always
 *    changing, fading softly into the page.
 *
 * It follows the session. The colour leads with the [mode]'s own (primary for focus, tertiary for
 * a short break, secondary for a long one). While [running] it flows faster, glows brighter and
 * reaches a touch lower, with a swell of light the moment it starts; paused it slows to a drift;
 * with no [sessionActive] it rests calmly. Every change eases rather than jumps.
 */
@Composable
fun AliveGradient(
    mode: TimerMode,
    running: Boolean,
    sessionActive: Boolean,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val isDark = scheme.background.luminance() < 0.5f
    // Light themes need a gentler hand, or the colour turns muddy over a pale page
    val themeStrength = if (isDark) 1f else 0.6f

    // The mode's own colour leads; the theme's other two fill in around it
    val (leadTarget, secondTarget, thirdTarget) = when (mode) {
        TimerMode.Focus -> Triple(scheme.primary, scheme.secondary, lerp(scheme.primary, scheme.tertiary, 0.5f))
        TimerMode.ShortBreak -> Triple(scheme.tertiary, scheme.primary, lerp(scheme.tertiary, scheme.secondary, 0.5f))
        TimerMode.LongBreak -> Triple(scheme.secondary, scheme.tertiary, lerp(scheme.secondary, scheme.primary, 0.5f))
    }
    val colorSpec = tween<Color>(1400, easing = FastOutSlowInEasing)
    val lead by animateColorAsState(leadTarget, colorSpec, label = "aliveLead")
    val second by animateColorAsState(secondTarget, colorSpec, label = "aliveSecond")
    val third by animateColorAsState(thirdTarget, colorSpec, label = "aliveThird")
    // The sweep is the lead colour lifted towards white, so it reads as light rather than paint
    val light = lerp(lead, Color.White, if (isDark) 0.45f else 0.25f)

    val mood = when {
        running -> Running
        sessionActive -> Paused
        else -> Idle
    }
    val strength by animateFloatAsState(mood.strength * themeStrength, tween(1600), label = "aliveStrength")
    val reach by animateFloatAsState(mood.reach, tween(1800, easing = FastOutSlowInEasing), label = "aliveReach")
    // Speed eases too; the flow below is advanced by it every frame, so it never lurches
    val speed = animateFloatAsState(mood.speed, tween(2000, easing = FastOutSlowInEasing), label = "aliveSpeed")

    // A swell of light the moment the timer starts, settling back over a second or so
    val swell = remember { Animatable(0f) }
    LaunchedEffect(running) {
        if (running) {
            swell.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
            swell.animateTo(0f, tween(1500, easing = LinearOutSlowInEasing))
        } else {
            swell.snapTo(0f)
        }
    }

    // How far everything has flowed, in dp; only ever read while drawing
    val flow = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var last = withFrameNanos { it }
        while (true) {
            withFrameNanos { now ->
                flow.floatValue += (now - last) / 1_000_000_000f * speed.value
                last = now
            }
        }
    }

    // Offscreen, so the fade masks only the glow and never the page behind it
    Canvas(modifier = modifier.graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }) {
        val f = flow.floatValue
        val alpha = (strength + 0.12f * swell.value * themeStrength).coerceAtMost(1f)

        drawCurrents(f.dp.toPx(), lead, second, third, alpha)
        drawSweep(f, light, alpha * (0.4f + 0.4f * swell.value))
        maskHorizon(f, reach)
    }
}

/**
 * The colour itself: a main current of the three colours streaming one way, and a slower,
 * wider undercurrent in a different order streaming the other, so where they overlap the
 * colour keeps shifting.
 */
private fun DrawScope.drawCurrents(shift: Float, lead: Color, second: Color, third: Color, alpha: Float) {
    val w = size.width

    val span = w * 1.1f
    val front = shift % (span * 2f)
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(lead, second, third, lead),
            startX = -front,
            endX = -front + span,
            tileMode = TileMode.Mirror
        ),
        alpha = alpha
    )

    val underSpan = w * 1.7f
    val back = (shift * 0.6f) % (underSpan * 2f)
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(third, lead, second),
            startX = back - underSpan,
            endX = back,
            tileMode = TileMode.Mirror
        ),
        alpha = alpha * 0.6f
    )
}

/** A soft diagonal band of light gliding across, then a pause before the next. */
private fun DrawScope.drawSweep(flowDp: Float, light: Color, alpha: Float) {
    val w = size.width
    val h = size.height
    // 0..1 through each pass; the band is only on screen for the first part of it
    val pass = fraction(flowDp / SWEEP_EVERY_DP)
    val travel = (pass / 0.55f).coerceAtMost(1f)
    if (travel >= 1f) return
    val band = w * 0.32f
    val cx = -band + travel * (w + band * 2f)
    // Fades in as it enters and out as it leaves, so it never pops
    val presence = sin(travel * PI.toFloat())
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, light.copy(alpha = alpha * presence), Color.Transparent),
            start = Offset(cx - band, 0f),
            end = Offset(cx + band, h * 0.55f)
        )
    )
}

/**
 * Fades the glow into the page, slice by slice, each slice reaching a little further or less far
 * as two slow waves roll across, so the lower edge breathes like a horizon instead of sitting flat.
 */
private fun DrawScope.maskHorizon(flowDp: Float, reach: Float) {
    val w = size.width
    val h = size.height
    val t = flowDp / 90f
    for (i in 0 until SLICES) {
        // Whole pixels, so neighbouring slices meet exactly with no hairline between them
        val x0 = floor(i * w / SLICES)
        val x1 = floor((i + 1) * w / SLICES)
        val u = (i + 0.5f) / SLICES
        val roll = 1f + 0.16f * sin(u * TAU * 1.1f + t) + 0.08f * sin(u * TAU * 2.6f - t * 1.7f)
        val bottom = (h * reach * roll).coerceIn(h * 0.2f, h)
        drawRect(
            brush = Brush.verticalGradient(
                colorStops = Array(STEPS + 1) { s ->
                    val p = s / STEPS.toFloat()
                    val eased = p * p * (3f - 2f * p)
                    p to Color.Black.copy(alpha = 1f - eased)
                },
                startY = 0f,
                endY = bottom
            ),
            topLeft = Offset(x0, 0f),
            size = Size(x1 - x0, h),
            blendMode = BlendMode.DstIn
        )
    }
}

private fun fraction(x: Float): Float = x - floor(x)
