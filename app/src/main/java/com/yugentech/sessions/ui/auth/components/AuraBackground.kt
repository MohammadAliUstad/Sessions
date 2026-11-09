package com.yugentech.sessions.ui.auth.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
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

/**
 * Shared state for the screens on [SessionsAuraBackground], as Quill's [AuraBackgroundState].
 * The background no longer moves, so nothing reads [travel] for now.
 */
@Stable
class AuraBackgroundState {
    var travel by mutableFloatStateOf(0f)
}

/** The [AuraBackgroundState] of the background drawn behind the current screen (AppNavHost). */
val LocalAuraBackground = staticCompositionLocalOf { AuraBackgroundState() }

/**
 * The routes that sit on the shared aura: the onboarding (first run, and opened again from
 * Settings) and both sign-in screens.
 */
val AuraRoutes = setOf("onboarding", "onboarding_replay", "sign_in", "sign_up")

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
 * deep shade of it, with glows of lighter and deeper shades laid over it and a deep shadow
 * between them for contrast. It holds still: the glows sit in fixed places, so the screens on it
 * move and the background doesn't.
 */
@Composable
fun SessionsAuraBackground(modifier: Modifier = Modifier) {
    val base = MaterialTheme.colorScheme.background
    val accent = MaterialTheme.colorScheme.primary
    val dark = base.luminance() < 0.5f
    // Shades of the colour itself. In a light theme: paler towards white, and richer towards the
    // brand's primary. In a dark theme: brighter towards primary, deeper towards black. Strong
    // enough to give it shape, never so far that the text on it stops reading.
    val light = if (dark) lerp(base, accent, 0.35f) else lerp(base, Color.White, 0.42f)
    val soft = if (dark) lerp(base, accent, 0.22f) else lerp(base, Color.White, 0.24f)
    val deep = if (dark) lerp(base, Color.Black, 0.50f) else lerp(base, accent, 0.40f)
    val gradient = Brush.verticalGradient(
        listOf(lerp(base, light, 0.6f), base, lerp(base, deep, 0.8f))
    )

    Canvas(modifier = modifier) {
        drawRect(gradient)

        // Deep shadow through the middle: the contrast the light needs.
        glow(
            center = Offset(size.width * 0.42f, size.height * 0.58f),
            radius = size.width * 0.75f,
            color = deep.copy(alpha = 0.45f)
        )
        // Light, top-left.
        glow(
            center = Offset(size.width * 0.12f, size.height * 0.10f),
            radius = size.width * 0.80f,
            color = light.copy(alpha = 0.55f)
        )
        // Soft light, right, a little above the middle.
        glow(
            center = Offset(size.width * 0.92f, size.height * 0.40f),
            radius = size.width * 0.65f,
            color = soft.copy(alpha = 0.50f)
        )
        // Light, bottom.
        glow(
            center = Offset(size.width * 0.30f, size.height * 0.94f),
            radius = size.width * 0.75f,
            color = light.copy(alpha = 0.35f)
        )
    }
}

private fun DrawScope.glow(center: Offset, radius: Float, color: Color) {
    drawCircle(
        brush = Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), center = center, radius = radius),
        radius = radius,
        center = center
    )
}
