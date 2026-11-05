package com.yugentech.sessions.ui.auth.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import kotlin.math.cos
import kotlin.math.sin

/**
 * What the screens on the shared [SessionsAuraBackground] tell it, as Quill's [AuraBackgroundState]
 * does: the glows glide to new places when [travel] moves, while the sign-in screens leave it where it is.
 */
@Stable
class AuraBackgroundState {
    var travel by mutableFloatStateOf(0f)
}

/** The [AuraBackgroundState] of the background drawn behind the current screen (AppNavHost). */
val LocalAuraBackground = staticCompositionLocalOf { AuraBackgroundState() }

/** The routes that sit on the shared aura: both sign-in and sign-up screens. */
val AuraRoutes = setOf("sign_in", "sign_up")

/**
 * The app's own colours, kept while [AuraTheme] recolours the open background, so cards and
 * pages on it can go back to them with [OnPaper].
 */
private val LocalPaperScheme = staticCompositionLocalOf<ColorScheme?> { null }

/**
 * The colours for screens on [SessionsAuraBackground] (the sign-in and sign-up screens). The
 * app's own colours throughout, so buttons, bars and accents stay in the theme's primary; only
 * the background becomes the aura's tone, and text on it takes that tone's own "on" colour so it
 * reads on the gradient. Cards and pages can go back to the untouched colours with [OnPaper].
 */
@Composable
fun AuraTheme(content: @Composable () -> Unit) {
    val original = MaterialTheme.colorScheme
    val aura = original.copy(
        background = auraBase(original),
        onBackground = original.onPrimaryContainer
    )
    CompositionLocalProvider(LocalPaperScheme provides original) {
        MaterialTheme(colorScheme = aura) {
            CompositionLocalProvider(LocalContentColor provides aura.onBackground, content = content)
        }
    }
}

/** The app's own colours, for drawing a card on the aura; the current colours anywhere else. */
@Composable
fun paperColors(): ColorScheme = LocalPaperScheme.current ?: MaterialTheme.colorScheme

/** [content] in the app's own colours again, for a card or page sitting on the aura. */
@Composable
fun OnPaper(content: @Composable () -> Unit) {
    val paper = LocalPaperScheme.current
    if (paper == null) {
        content()
    } else {
        MaterialTheme(colorScheme = paper) {
            CompositionLocalProvider(LocalContentColor provides paper.onSurface, content = content)
        }
    }
}

/**
 * The aura's colour: the theme's primary container, a soft tone of the brand colour, mixed back
 * toward the plain background so it's calm rather than bright. The theme's primary (buttons,
 * bars, accents) and ordinary text both stand out on it, in light and dark.
 */
private fun auraBase(scheme: ColorScheme): Color = lerp(scheme.background, scheme.primaryContainer, AURA_TINT)

/** How much of the brand tint the aura takes over the plain background (0 = none, 1 = full). */
private const val AURA_TINT = 0.6f

/**
 * Sessions' auth background, after Quill's aura backdrop: all in the aura colour's own hue
 * (from [AuraTheme], the theme's primary container), a top-to-bottom gradient from a light to a
 * deep shade of it, with glows of lighter and deeper shades drifting over it and a deep shadow
 * wandering between them for contrast. Each glides back and forth on its own cycle, so the
 * background has depth and slow, constant movement.
 */
@Composable
fun SessionsAuraBackground(modifier: Modifier = Modifier, travel: () -> Float = { 0f }) {
    val base = MaterialTheme.colorScheme.background
    val accent = MaterialTheme.colorScheme.primary
    val dark = base.luminance() < 0.5f
    val drift = rememberInfiniteTransition(label = "aura")
    val a = drift.wave(12_000)
    val b = drift.wave(16_000)
    val c = drift.wave(21_000)
    val d = drift.wave(27_000)
    // Its slow wander, on top of wherever the steps have sent it
    val wander by drift.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(24_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "auraWander"
    )
    // Shades of the colour itself. In a light theme: paler towards white, and richer towards the
    // brand's primary. In a dark theme: brighter towards primary, deeper towards black. Never so
    // far that the text on it stops reading.
    // Kept gentle: the light shades lift it only a little, so it never glares.
    val light = if (dark) lerp(base, accent, 0.25f) else lerp(base, Color.White, 0.30f)
    val soft = if (dark) lerp(base, accent, 0.15f) else lerp(base, Color.White, 0.15f)
    val deep = if (dark) lerp(base, Color.Black, 0.40f) else lerp(base, accent, 0.30f)
    val gradient = Brush.verticalGradient(
        listOf(lerp(base, light, 0.4f), base, lerp(base, deep, 0.6f))
    )

    Canvas(modifier = modifier) {
        val t = travel() + wander
        drawRect(gradient)

        // Deep shadow, wandering through the middle: the contrast the light needs.
        glow(
            center = Offset(
                size.width * (0.30f + 0.40f * d.value + 0.25f * sin(t * 0.7f + 2f) - 0.23f),
                size.height * (0.55f + 0.15f * a.value - 0.10f * sin(t * 1.2f))
            ),
            radius = size.width * 0.70f,
            color = deep.copy(alpha = 0.30f)
        )
        // Light, top-left, drifting right and down.
        glow(
            center = Offset(
                size.width * (0.05f + 0.25f * a.value + 0.35f * sin(t * 1.3f)),
                size.height * (0.08f + 0.10f * b.value + 0.12f * (1f - cos(t * 1.1f)))
            ),
            radius = size.width * 0.75f,
            color = light.copy(alpha = 0.35f)
        )
        // Soft light, right, drifting up and down through the middle.
        glow(
            center = Offset(
                size.width * (0.95f - 0.20f * b.value - 0.40f * sin(t * 0.9f)),
                size.height * (0.35f + 0.20f * c.value + 0.15f * sin(t * 1.4f))
            ),
            radius = size.width * 0.65f,
            color = soft.copy(alpha = 0.40f)
        )
        // Light, bottom, drifting left and right.
        glow(
            center = Offset(
                size.width * (0.20f + 0.50f * c.value + 0.30f * sin(t * 1.7f + 1f) - 0.25f),
                size.height * (0.92f - 0.08f * a.value - 0.14f * (1f - cos(t * 0.8f)))
            ),
            radius = size.width * 0.75f,
            color = light.copy(alpha = 0.25f)
        )
    }
}

/** 0 → 1 → 0 over [periodMs] × 2, eased at both ends. */
@Composable
private fun InfiniteTransition.wave(periodMs: Int): State<Float> = animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(periodMs, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "wave$periodMs"
)

private fun DrawScope.glow(center: Offset, radius: Float, color: Color) {
    drawCircle(
        brush = Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), center = center, radius = radius),
        radius = radius,
        center = center
    )
}
