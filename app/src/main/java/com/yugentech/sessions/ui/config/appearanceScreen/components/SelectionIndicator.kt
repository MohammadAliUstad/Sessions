package com.yugentech.sessions.ui.config.appearanceScreen.components

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.yugentech.sessions.theme.SLOW_FADE_MS

/** How long the outline takes to fade between its resting and selected looks. */
const val SELECTION_FADE_MS = 280

/**
 * Standard animated BorderStroke for selectable items in the Appearance screen: a plain eased
 * fade between the resting outline and the selected one, colour and width together, with no
 * overshoot. Every selectable item uses it, so they all fade the same way.
 */
@Composable
fun rememberSelectionBorderStroke(
    isSelected: Boolean,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.outlineVariant,
    selectedWidth: Dp = 2.5.dp,
    unselectedWidth: Dp = 1.dp
): BorderStroke {
    // With no resting outline, it fades from nothing in the selected colour, not from a grey
    val restingColor = if (unselectedWidth == 0.dp) selectedColor.copy(alpha = 0f) else unselectedColor
    val fade by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(SELECTION_FADE_MS, easing = FastOutSlowInEasing),
        label = "selectionOutlineFade"
    )
    return BorderStroke(
        width = lerp(unselectedWidth, selectedWidth, fade).coerceAtLeast(0.dp),
        color = lerp(restingColor, selectedColor, fade)
    )
}

/**
 * Standard animated selection outline modifier for selectable cards in the Appearance screen.
 */
@Composable
fun Modifier.selectionOutline(
    isSelected: Boolean,
    shape: Shape,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.outlineVariant,
    selectedWidth: Dp = 2.5.dp,
    unselectedWidth: Dp = 1.dp
): Modifier = this.border(
    border = rememberSelectionBorderStroke(
        isSelected = isSelected,
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        selectedWidth = selectedWidth,
        unselectedWidth = unselectedWidth
    ),
    shape = shape
)

/** How the crossfade outline and label fade: the same length and curve as the screen's theme fade. */
private fun <T> selectionCrossfadeSpec() = tween<T>(SLOW_FADE_MS, easing = FastOutSlowInEasing)

/**
 * Selection outline for cards whose change doesn't run the screen's theme fade (Home Layout,
 * Card Style, Card Animation), made to look the way the outline does under that fade: the thin
 * resting outline always drawn, with the full selected outline crossfading in or out on top of
 * it over [SLOW_FADE_MS]. The line never grows in width; only its opacity changes, as it does
 * when the frozen screen dissolves over the new one.
 */
@Composable
fun Modifier.crossfadeSelectionOutline(
    isSelected: Boolean,
    shape: Shape,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.outlineVariant,
    selectedWidth: Dp = 2.5.dp,
    unselectedWidth: Dp = 1.dp
): Modifier {
    val fade by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = selectionCrossfadeSpec(),
        label = "selectionOutlineCrossfade"
    )
    val resting = if (unselectedWidth > 0.dp) this.border(unselectedWidth, unselectedColor, shape) else this
    return if (fade > 0f) {
        resting.border(selectedWidth, selectedColor.copy(alpha = selectedColor.alpha * fade), shape)
    } else {
        resting
    }
}

/** A colour that fades between [unselected] and [selected] in step with [crossfadeSelectionOutline]. */
@Composable
fun animateSelectionColor(isSelected: Boolean, selected: Color, unselected: Color): Color {
    val color by animateColorAsState(
        targetValue = if (isSelected) selected else unselected,
        animationSpec = selectionCrossfadeSpec(),
        label = "selectionColorCrossfade"
    )
    return color
}

/**
 * Standard animated tick badge for selected items: pops in with a lively spring scale animation
 * and scales away cleanly when deselected, with no slow alpha fade.
 */
@Composable
fun SelectionTick(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    iconSize: Dp = 14.dp,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    AnimatedVisibility(
        visible = isSelected,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = Spring.StiffnessMedium
            )
        ),
        exit = scaleOut(
            animationSpec = tween(durationMillis = 150, easing = FastOutLinearInEasing)
        ),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(containerColor)
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = stringResource(R.string.selected),
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
