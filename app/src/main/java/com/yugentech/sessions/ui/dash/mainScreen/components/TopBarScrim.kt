package com.yugentech.sessions.ui.dash.mainScreen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val SCRIM_STEPS = 16

/**
 * Top-bar scrim that fades [color] from [topAlpha] to fully transparent along a smoothstep curve
 * (flat at both ends). A few hand-placed linear stops change slope abruptly, and each change
 * shows up as a visible band; sampling one smooth curve avoids that.
 */
fun smoothScrimBrush(color: Color, topAlpha: Float = 0.9f): Brush =
    Brush.verticalGradient(
        colorStops = Array(SCRIM_STEPS + 1) { i ->
            val t = i / SCRIM_STEPS.toFloat()
            val eased = t * t * (3f - 2f * t)
            t to color.copy(alpha = topAlpha * (1f - eased))
        }
    )

/** The same smooth fade flipped: clear at the top, [color] at [bottomAlpha] along the bottom. */
fun smoothBottomScrimBrush(color: Color, bottomAlpha: Float = 0.9f): Brush =
    Brush.verticalGradient(
        colorStops = Array(SCRIM_STEPS + 1) { i ->
            val t = i / SCRIM_STEPS.toFloat()
            val eased = t * t * (3f - 2f * t)
            t to color.copy(alpha = bottomAlpha * eased)
        }
    )

@Composable
fun rememberSmoothScrimBrush(color: Color, topAlpha: Float = 0.9f): Brush =
    remember(color, topAlpha) { smoothScrimBrush(color, topAlpha) }

/**
 * How tall the transparent top bar is. The main pages draw right up under it (so their
 * backgrounds and glows reach the top of the screen) and start their content this far down.
 */
val LocalTopBarInset = compositionLocalOf { 0.dp }

/** Shorthand for [LocalTopBarInset] in places that just need the number. */
val topBarInset: Dp
    @Composable get() = LocalTopBarInset.current

/**
 * How much of the bottom the floating nav dock covers (the dock, the gap above it and the system
 * navigation bar). The main pages run down under it and end their content this far up.
 */
val LocalBottomBarInset = compositionLocalOf { 0.dp }

/** Shorthand for [LocalBottomBarInset] in places that just need the number. */
val bottomBarInset: Dp
    @Composable get() = LocalBottomBarInset.current
