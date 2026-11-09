package com.yugentech.sessions.ui.dash.homeScreen.components.hero

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.config.CardAnimationStyle
import com.yugentech.sessions.theme.config.CardStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

private val TAU = (2 * PI).toFloat()

/**
 * What the timer is doing, for the card's effect to follow: how far the current block has run
 * ([progress], 0..1), whether the timer is [running], and whether this block is a break.
 */
@Immutable
data class SessionPulse(
    val progress: Float = 0f,
    val running: Boolean = false,
    val onBreak: Boolean = false
)

/**
 * Seconds of effect time, counting only while [running], so a paused timer leaves its effect
 * exactly where it was and it carries on from there. Only read while drawing, so the effect
 * never recomposes the card's contents.
 */
@Composable
fun rememberEffectTime(running: Boolean): State<Float> {
    val time = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        var last = withFrameNanos { it }
        while (true) {
            withFrameNanos { now ->
                time.floatValue += (now - last) / 1_000_000_000f
                last = now
            }
        }
    }
    return time
}

/**
 * The moving layer behind the session card's text, following [pulse]. Pass a null [time] (or
 * [CardAnimationStyle.NONE]) and nothing is drawn.
 *
 * [ink] is what the effect is drawn in: white on dark cards, the theme colour on light ones
 * (see [heroPalette]); [shade] is what deepens, for the effects that darken as well as light.
 */
@Composable
fun HeroCardBackground(
    style: CardAnimationStyle,
    time: State<Float>?,
    pulse: SessionPulse,
    modifier: Modifier = Modifier,
    ink: Color = Color.White,
    shade: Color = Color.Black
) {
    if (time == null || style == CardAnimationStyle.NONE) return
    // Each of these eases rather than jumps, so pausing, a break or a new block settles in
    val activity = animateFloatAsState(if (pulse.running) 1f else 0f, tween(800, easing = FastOutSlowInEasing), label = "effectActivity")
    val rest = animateFloatAsState(if (pulse.onBreak) 1f else 0f, tween(1200, easing = FastOutSlowInEasing), label = "effectBreak")
    val progress = animateFloatAsState(pulse.progress, tween(1000, easing = LinearEasing), label = "effectProgress")

    // Aurora and mesh are soft washes of light that only work blurred together, so they get
    // their own blurred layer; everything else stays crisp
    if (style == CardAnimationStyle.AURORA || style == CardAnimationStyle.MESH) {
        val scheme = MaterialTheme.colorScheme
        val blobs = remember(scheme.tertiary, scheme.secondary, scheme.primaryContainer, scheme.primary) {
            listOf(
                scheme.tertiary.copy(alpha = 0.70f),
                scheme.primaryContainer.copy(alpha = 0.55f),
                lerp(scheme.primary, scheme.tertiary, 0.5f).copy(alpha = 0.65f),
                scheme.secondary.copy(alpha = 0.50f)
            )
        }
        Canvas(modifier = modifier.blur(if (style == CardAnimationStyle.AURORA) 36.dp else 40.dp)) {
            val t = time.value
            if (style == CardAnimationStyle.AURORA) drawAurora(t, activity.value, rest.value, ink, shade)
            else drawMesh(t, activity.value, rest.value, blobs, ink)
        }
        return
    }

    Canvas(modifier = modifier) {
        val t = time.value
        when (style) {
            CardAnimationStyle.NONE, CardAnimationStyle.AURORA, CardAnimationStyle.MESH -> Unit
            CardAnimationStyle.SAND -> drawDesert(t, progress.value, activity.value, rest.value, ink)
            CardAnimationStyle.RAIN -> drawRain(t, activity.value, rest.value, ink)
            CardAnimationStyle.CANDLE -> drawCandle(t, activity.value, ink)
        }
    }
}

// ───────────────────────────── Card style ─────────────────────────────

/** What a hero card's text ([content]) and animation ([ink], [shade]) are drawn in, per [CardStyle]. */
class HeroPalette(val content: Color, val ink: Color, val shade: Color)

/**
 * Filled keeps the card's own secondaryContainer look, so the progress bars (primary, tertiary,
 * error) still read on it. On a pale container white highlights vanish, so light mode glints in
 * the theme colour instead.
 */
@Composable
fun heroPalette(style: CardStyle): HeroPalette {
    val scheme = MaterialTheme.colorScheme
    val isDark = scheme.background.luminance() < 0.5f
    val ink = if (isDark) Color.White else scheme.primary
    val shade = if (isDark) Color.Black else scheme.primary
    return when (style) {
        CardStyle.FILLED -> HeroPalette(content = scheme.onSecondaryContainer, ink = ink, shade = shade)
        CardStyle.OUTLINED -> HeroPalette(content = scheme.onSurface, ink = scheme.primary, shade = scheme.primary)
        CardStyle.GLASS -> HeroPalette(content = scheme.onSurface, ink = ink, shade = shade)
    }
}

/** The gradient a filled card is painted with, and glass is washed with. */
@Composable
fun heroBrush(): Brush {
    val base = MaterialTheme.colorScheme.secondaryContainer
    return Brush.linearGradient(listOf(base, lerp(base, Color.Black, 0.12f)))
}

/**
 * The card's own surface: the gradient [brush] for filled cards, a quiet surface with a primary
 * edge for outlined ones, and for glass a thin wash of the gradient with a sheen and a lit edge.
 */
@Composable
fun Modifier.heroSurface(
    style: CardStyle,
    brush: Brush,
    shape: Shape,
    drawBorder: Boolean = true
): Modifier {
    val scheme = MaterialTheme.colorScheme
    return when (style) {
        CardStyle.FILLED -> clip(shape).background(brush)
        CardStyle.OUTLINED -> {
            val base = clip(shape).background(scheme.surfaceContainerLow)
            if (drawBorder) base.border(1.5.dp, scheme.primary.copy(alpha = 0.45f), shape) else base
        }
        CardStyle.GLASS -> {
            val isDark = scheme.background.luminance() < 0.5f
            val edge = if (isDark) Color.White else scheme.primary
            val base = clip(shape)
                .background(scheme.surfaceContainer)
                .background(brush, alpha = if (isDark) 0.30f else 0.22f)
                // Sheen: light falling across the top-left corner, behind the text
                .drawBehind {
                    drawRect(
                        Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = if (isDark) 0.14f else 0.45f), Color.Transparent),
                            start = Offset.Zero,
                            end = Offset(size.width * 0.55f, size.height)
                        )
                    )
                }
            if (drawBorder) {
                base.border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(edge.copy(alpha = 0.55f), edge.copy(alpha = 0.06f), edge.copy(alpha = 0.25f))
                    ),
                    shape = shape
                )
            } else {
                base
            }
        }
    }
}


// ───────────────────────────── Shared ─────────────────────────────

/** The fractional part, so a running value wraps into 0..1. */
private fun fraction(x: Float): Float = x - floor(x)

/** The [i]th point of an even but irregular-looking scatter over 0..1 × 0..1, the same every time. */
private fun scatter(i: Int): Offset = Offset(fraction(0.13f + i * 0.7548777f), fraction(0.29f + i * 0.5698403f))

/** How visible something is at [p] through its run: fading in at the start and out at the end. */
private fun fadeThrough(p: Float): Float = (p * 6f).coerceAtMost(1f) * ((1f - p) * 6f).coerceAtMost(1f)

// ───────────────────────────── Desert crossing ─────────────────────────────

/** The sun, and the warm light of dusk, both drawn towards the card's own colours. */
private val SunGold = Color(0xFFFFD27A)
private val Dusk = Color(0xFFFF9A5A)

/** One band of dunes: how tall, how many swells across the card, how bold, and how fast it slides by (dp a second). */
private class DuneBand(val height: Float, val swells: Float, val alpha: Float, val drift: Float)

private val DuneBands = listOf(
    DuneBand(height = 0.34f, swells = 1.3f, alpha = 0.08f, drift = 2f),
    DuneBand(height = 0.25f, swells = 1.9f, alpha = 0.12f, drift = 4f),
    DuneBand(height = 0.16f, swells = 2.6f, alpha = 0.18f, drift = 7f)
)

/** The top of a band of dunes at [x]: rolling swells with a smaller ripple on them, sliding by with [shift]. */
private fun DrawScope.duneTop(band: DuneBand, index: Int, x: Float, shift: Float): Float {
    val u = (x + shift) / size.width
    val swell = 0.55f + 0.30f * sin(u * TAU * band.swells + index * 1.7f) + 0.15f * sin(u * TAU * band.swells * 2.3f + index)
    return size.height - size.height * band.height * swell
}

/**
 * A desert crossing behind the card: bands of dunes along the bottom, kept faint so the card's
 * text sits clearly over them, and a sun that rises from the left and arcs across the sky as the
 * block runs, setting as it ends. While the timer runs the dunes slide slowly by and the wind
 * lifts sand off their crests; paused, the wind drops. A break turns it to dusk: a warm sky,
 * the sun sinking behind the dunes and the first stars.
 */
private fun DrawScope.drawDesert(t: Float, progress: Float, activity: Float, rest: Float, ink: Color) {
    val w = size.width
    val h = size.height
    val p = progress.coerceIn(0f, 1f)

    // Dusk: the sky warms on a break
    if (rest > 0f) {
        drawRect(
            Brush.verticalGradient(
                listOf(lerp(ink, Dusk, 0.6f).copy(alpha = 0.16f * rest), Color.Transparent),
                startY = 0f,
                endY = h * 0.75f
            )
        )
        repeat(14) { i ->
            val s = scatter(i + 800)
            val twinkle = 0.5f + 0.5f * sin(t * (0.6f + 0.2f * (i % 3)) * TAU + i)
            drawCircle(
                ink.copy(alpha = 0.45f * rest * twinkle),
                (0.7f + (i % 3) * 0.3f).dp.toPx(),
                Offset(s.x * w, s.y * h * 0.5f)
            )
        }
    }

    // The sun: across the sky with the block, sinking behind the dunes on a break
    val sun = Offset(
        x = w * (0.08f + 0.84f * p),
        y = h * (0.62f - 0.42f * sin(p * PI.toFloat())) + h * 0.38f * rest
    )
    val sunColour = lerp(lerp(ink, SunGold, 0.55f), Dusk, 0.5f * rest)
    val sunRadius = h * 0.07f
    val glow = sunRadius * 4.5f
    drawCircle(Brush.radialGradient(listOf(sunColour.copy(alpha = 0.22f), Color.Transparent), sun, glow), glow, sun)
    drawCircle(sunColour.copy(alpha = 0.40f), sunRadius, sun)

    // The dunes, back to front; the front band lit along its crest
    DuneBands.forEachIndexed { index, band ->
        val shift = t * band.drift.dp.toPx()
        val dune = Path().apply {
            moveTo(0f, h)
            val steps = 40
            for (s in 0..steps) {
                val x = w * s / steps
                lineTo(x, duneTop(band, index, x, shift))
            }
            lineTo(w, h)
            close()
        }
        drawPath(
            dune,
            Brush.verticalGradient(
                listOf(ink.copy(alpha = band.alpha * 1.3f), ink.copy(alpha = band.alpha * 0.6f)),
                startY = h * (1f - band.height),
                endY = h
            )
        )
        if (index == DuneBands.lastIndex) {
            val crest = Path().apply {
                val steps = 40
                for (s in 0..steps) {
                    val x = w * s / steps
                    val y = duneTop(band, index, x, shift)
                    if (s == 0) moveTo(x, y) else lineTo(x, y)
                }
            }
            drawPath(crest, ink.copy(alpha = 0.28f), style = Stroke(1.dp.toPx(), cap = StrokeCap.Round))
        }
    }

    if (activity <= 0f) return
    // Wind lifting sand off the front crest and carrying it across
    val front = DuneBands.last()
    val frontShift = t * front.drift.dp.toPx()
    repeat(28) { i ->
        val s = scatter(i + 900)
        val speed = 0.05f + 0.03f * (i % 3)
        val q = fraction(t * speed + s.x)
        val x = -10.dp.toPx() + q * (w + 20.dp.toPx())
        val lift = (4f + s.y * 14f).dp.toPx() * sin(q * PI.toFloat())
        val y = duneTop(front, DuneBands.lastIndex, x, frontShift) - lift
        drawCircle(ink.copy(alpha = 0.40f * activity * fadeThrough(q)), (0.6f + (i % 3) * 0.3f).dp.toPx(), Offset(x, y))
    }
    // A few thin wisps of blown sand
    repeat(3) { i ->
        val q = fraction(t * 0.035f + i / 3f)
        val x = -w * 0.3f + q * w * 1.6f
        val y = h * (0.62f + 0.08f * i)
        val length = w * 0.22f
        val wisp = Path().apply {
            moveTo(x, y)
            quadraticTo(x + length * 0.5f, y - 6.dp.toPx(), x + length, y - 2.dp.toPx())
        }
        drawPath(
            wisp,
            Brush.horizontalGradient(
                listOf(Color.Transparent, ink.copy(alpha = 0.16f * activity), Color.Transparent),
                startX = x,
                endX = x + length
            ),
            style = Stroke(1.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

// ───────────────────────────── Lo-fi rain ─────────────────────────────

/** A bead of water on glass: clear in the middle, darker at the rim, with a glint. */
private fun DrawScope.drawBead(c: Offset, r: Float, ink: Color, alpha: Float = 1f) {
    drawCircle(
        Brush.radialGradient(listOf(ink.copy(alpha = 0.03f * alpha), ink.copy(alpha = 0.20f * alpha)), c, r),
        r,
        c
    )
    drawArc(
        ink.copy(alpha = 0.32f * alpha), 20f, 140f, false,
        topLeft = c - Offset(r, r),
        size = Size(r * 2, r * 2),
        style = Stroke(0.6.dp.toPx())
    )
    drawCircle(Color.White.copy(alpha = 0.6f * alpha), r * 0.3f, c + Offset(-r * 0.35f, -r * 0.35f))
}

/** A sliding drop: round where it's heading, drawn out to a point behind it. */
private fun DrawScope.drawDrop(c: Offset, r: Float, ink: Color, alpha: Float) {
    // Mostly round, only a little drawn out behind: a sliding drop, not a streak
    val drop = Path().apply {
        moveTo(c.x, c.y - r * 1.45f)
        quadraticTo(c.x + r * 1.02f, c.y - r * 0.85f, c.x + r, c.y)
        arcTo(Rect(c, r), 0f, 180f, false)
        quadraticTo(c.x - r * 1.02f, c.y - r * 0.85f, c.x, c.y - r * 1.45f)
        close()
    }
    drawPath(
        drop,
        Brush.radialGradient(
            listOf(ink.copy(alpha = 0.06f * alpha), ink.copy(alpha = 0.30f * alpha)),
            center = c,
            radius = r * 1.6f
        )
    )
    drawPath(drop, ink.copy(alpha = 0.40f * alpha), style = Stroke(0.7.dp.toPx()))
    drawCircle(Color.White.copy(alpha = 0.7f * alpha), r * 0.3f, c + Offset(-r * 0.35f, -r * 0.2f))
}

/**
 * Rain on a study-room window: a faint downpour beyond the glass, beads clinging to it that
 * catch the light, and a few heavier drops sliding down in fits and starts, each leaving a wet
 * trail with a few beads in it. Steady while focusing, a drizzle on a break; paused, the drops
 * hold where they are and the rain beyond the glass fades.
 */
private fun DrawScope.drawRain(t: Float, activity: Float, rest: Float, ink: Color) {
    val w = size.width
    val h = size.height

    // Beyond the glass: thin, slanted, very faint
    val far = (1f - 0.6f * rest) * activity
    if (far > 0f) {
        repeat(40) { i ->
            val s = scatter(i + 300)
            val length = (10f + (i % 3) * 5f).dp.toPx()
            val y = fraction(t * (0.9f + 0.25f * (i % 4)) + s.y) * (h + length) - length
            val x = s.x * w + y * 0.12f
            drawLine(
                ink.copy(alpha = 0.10f * far),
                Offset(x, y),
                Offset(x + length * 0.12f, y + length),
                0.7.dp.toPx(),
                StrokeCap.Round
            )
        }
    }

    // Beads on the glass
    repeat(46) { i ->
        val s = scatter(i + 60)
        drawBead(Offset(s.x * w, s.y * h), (0.9f + (i % 5) * 0.55f).dp.toPx(), ink)
    }

    // Heavier drops sliding down; fewer on a break
    val drops = 7
    val shown = drops * (1f - 0.5f * rest)
    val margin = 8.dp.toPx()
    repeat(drops) { i ->
        val strength = (shown - i).coerceIn(0f, 1f)
        if (strength <= 0f) return@repeat
        val s = scatter(i + 400)
        val period = 10f + (i % 3) * 4f
        val phase = fraction(t / period + s.y)
        // Fits and starts: it stalls three times on the way down the glass
        val travel = phase - sin(phase * TAU * 3) / (TAU * 3)
        val y = -margin + travel * (h + margin * 2)
        val x0 = w * (0.05f + 0.9f * s.x)
        // Nearly straight down, with the slightest drift where the glass pulls it aside
        fun xAt(yy: Float) = x0 + sin(yy / h * TAU * 0.5f + i) * 0.8.dp.toPx()
        val r = (2.2f + (i % 3) * 0.7f).dp.toPx()

        // Behind it, a hairline of wet glass that dries out quickly, broken into tiny beads
        val trailTop = maxOf(-margin, y - h * 0.32f)
        if (y > trailTop) {
            drawLine(
                brush = Brush.verticalGradient(
                    listOf(Color.Transparent, ink.copy(alpha = 0.10f * strength)),
                    startY = trailTop,
                    endY = y
                ),
                start = Offset(xAt(trailTop), trailTop),
                end = Offset(xAt(y), y - r),
                strokeWidth = 0.6.dp.toPx(),
                cap = StrokeCap.Round
            )
            // Beads it has left behind, smaller and fainter the further back they are
            for (k in 1..6) {
                val ty = y - r - k * 7.dp.toPx()
                if (ty <= trailTop) break
                val fade = 1f - k / 7f
                drawBead(Offset(xAt(ty), ty), r * (0.42f - k * 0.04f), ink, strength * fade)
            }
        }
        drawDrop(Offset(xAt(y), y), r, ink, strength)
    }
}

// ───────────────────────────── Candle & embers ─────────────────────────────

/** A candle's warm light, the card's own colour pulled towards flame. */
private val Flame = Color(0xFFFFA94D)

/**
 * A candle burning just below the card: a warm glow along the bottom that flickers, with embers
 * drifting up and fading out. Brighter while the timer runs; paused, the embers settle and the
 * glow dims to a soft light.
 */
private fun DrawScope.drawCandle(t: Float, activity: Float, ink: Color) {
    val w = size.width
    val h = size.height
    val warm = lerp(ink, Flame, 0.65f)
    // Uneven flicker from a few slow waves on top of each other
    val flicker = 0.5f + 0.25f * sin(t * 1.7f * TAU) + 0.15f * sin(t * 2.9f * TAU + 1f) + 0.10f * sin(t * 4.3f * TAU + 2f)
    val strength = (0.45f + 0.55f * activity) * (0.85f + 0.15f * flicker)

    val glowCentre = Offset(w * 0.62f, h * 1.08f)
    val radius = w * (0.55f + 0.04f * flicker)
    drawCircle(
        Brush.radialGradient(listOf(warm.copy(alpha = 0.42f * strength), Color.Transparent), glowCentre, radius),
        radius,
        glowCentre
    )
    // A tighter, brighter heart to the light
    val heart = w * 0.22f
    drawCircle(
        Brush.radialGradient(listOf(warm.copy(alpha = 0.35f * strength), Color.Transparent), glowCentre, heart),
        heart,
        glowCentre
    )

    if (activity <= 0f) return
    repeat(16) { i ->
        val p = fraction(t * (0.12f + 0.05f * (i % 3)) + i / 16f)
        val start = scatter(i)
        val c = Offset(
            x = w * (0.62f + (start.x - 0.5f) * 0.6f) + sin((p * 1.5f + start.y) * TAU) * 10.dp.toPx(),
            y = h - p * h * 0.95f
        )
        val glow = (1f - p) * (p * 8f).coerceAtMost(1f) * activity
        val r = (1.1f + (i % 3) * 0.5f).dp.toPx()
        drawCircle(warm.copy(alpha = 0.25f * glow), r * 3f, c)
        drawCircle(warm.copy(alpha = 0.85f * glow), r, c)
    }
}

// ───────────────────────────── Aurora ─────────────────────────────

/** Seconds for the aurora and mesh washes to make one full drift around the card. */
private const val DRIFT_PERIOD_S = 24f

/**
 * Light and dark washes drifting slowly over the card's own colour, so it always matches the
 * theme. Brightest while the timer runs; paused, it dims to a quiet glow, and a break slows
 * it into a softer, darker sky.
 */
private fun DrawScope.drawAurora(t: Float, activity: Float, rest: Float, ink: Color, shade: Color) {
    val w = size.width
    val h = size.height
    val phase = t / DRIFT_PERIOD_S * TAU
    val strength = (0.55f + 0.45f * activity) * (1f - 0.35f * rest)
    val tints = listOf(
        ink.copy(alpha = 0.34f * strength),
        shade.copy(alpha = (0.26f + 0.10f * rest).coerceAtMost(1f)),
        ink.copy(alpha = 0.22f * strength)
    )
    tints.forEachIndexed { i, tint ->
        val center = Offset(
            x = w * (0.5f + 0.38f * sin(phase + i * 2.1f)),
            y = h * (0.5f + 0.34f * cos(phase + i * 1.3f))
        )
        val radius = w * (0.34f + 0.05f * i)
        drawCircle(
            brush = Brush.radialGradient(listOf(tint, Color.Transparent), center, radius),
            radius = radius,
            center = center
        )
    }
}

// ───────────────────────────── Mesh gradient ─────────────────────────────

/**
 * Four soft blobs in the theme's own colours drifting over the card and blending under a heavy
 * blur. Unlike the aurora it brings colour in, so every theme gets its own mix. Fuller while
 * the timer runs, fainter when paused or on a break.
 */
private fun DrawScope.drawMesh(t: Float, activity: Float, rest: Float, blobs: List<Color>, ink: Color) {
    val w = size.width
    val h = size.height
    val phase = t / DRIFT_PERIOD_S * TAU
    val strength = (0.65f + 0.35f * activity) * (1f - 0.3f * rest)
    blobs.forEachIndexed { i, color ->
        // Different whole turns on each axis per blob, so the paths cross without an obvious repeat
        val xTurns = 1 + i % 2
        val yTurns = 1 + (i + 1) % 3
        val center = Offset(
            x = w * (0.5f + 0.42f * sin(phase * xTurns + i * 1.7f)),
            y = h * (0.5f + 0.40f * cos(phase * yTurns + i * 2.3f))
        )
        val radius = w * (0.38f + 0.06f * (i % 2))
        drawCircle(
            brush = Brush.radialGradient(listOf(color.copy(alpha = color.alpha * strength), Color.Transparent), center, radius),
            radius = radius,
            center = center
        )
    }
    // A faint highlight so the mix keeps some light in it
    val glow = Offset(w * (0.25f + 0.1f * sin(phase)), h * 0.2f)
    drawCircle(
        brush = Brush.radialGradient(listOf(ink.copy(alpha = 0.18f * strength), Color.Transparent), glow, w * 0.3f),
        radius = w * 0.3f,
        center = glow
    )
}
