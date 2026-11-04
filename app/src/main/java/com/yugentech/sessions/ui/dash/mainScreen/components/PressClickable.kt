package com.yugentech.sessions.ui.dash.mainScreen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer

/** How far a pressed item shrinks before springing back. */
const val PRESSED_SCALE = 0.97f

/**
 * Shrinks the element a touch while [interactionSource] is pressed and springs it back on
 * release, so taps feel physical. Put it before the element's clip and background so the
 * whole thing scales together, and share the source with its clickable.
 */
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = PRESSED_SCALE
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "pressScale"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * [clickable] with [pressScale] on the same press, for elements without their own source.
 * [shape] clips the ripple; it goes between the scale and the click, so both line up.
 */
fun Modifier.pressClickable(
    shape: Shape? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    pressedScale: Float = PRESSED_SCALE,
    onClick: () -> Unit
): Modifier = composed {
    val source = remember { MutableInteractionSource() }
    pressScale(source, pressedScale)
        .then(if (shape != null) Modifier.clip(shape) else Modifier)
        .clickable(
            interactionSource = source,
            indication = LocalIndication.current,
            enabled = enabled,
            onClickLabel = onClickLabel,
            onClick = onClick
        )
}
