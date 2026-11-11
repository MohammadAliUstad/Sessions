package com.yugentech.sessions.ui.config.appearanceScreen.components

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.corners

/**
 * A colour theme as a tile in its own theme's surface colours: a round palette disc split into
 * the theme's three key colours (primary across the top, secondary and tertiary below, as
 * Android's own wallpaper colour picker shows them) beside the name, then a strip of swatches
 * along the bottom. Choosing one spins its disc a quarter turn and shows a check in its
 * top-right corner.
 */
@Composable
fun ThemeCard(
    name: String,
    scheme: ColorScheme,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(MaterialTheme.corners.large)
    // A quarter turn each time it's chosen, settling on a soft spring
    val spin = remember { Animatable(0f) }
    var wasSelected by remember { mutableStateOf(isSelected) }
    LaunchedEffect(isSelected) {
        val becameSelected = isSelected && !wasSelected
        wasSelected = isSelected
        if (becameSelected) {
            spin.snapTo(-90f)
            spin.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
        } else if (!isSelected && spin.value != 0f) {
            // Switched away mid-turn: this effect cancelled the spin wherever it was, so finish
            // settling it upright instead of leaving the disc tilted
            spin.animateTo(0f, spring(dampingRatio = 1f, stiffness = Spring.StiffnessMediumLow))
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            // No ripple and no press scale: the screen's fade is the only feedback
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isSelected) {
                    haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                    onClick()
                }
            }
            .background(scheme.surfaceContainer)
            .crossfadeSelectionOutline(
                isSelected = isSelected,
                shape = shape,
                unselectedColor = scheme.outlineVariant.copy(alpha = 0.5f)
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // The palette disc with the name beside it, so the tile stays short
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(
                    modifier = Modifier
                        .size(DiscSize)
                        .graphicsLayer { rotationZ = spin.value }
                ) {
                    drawPaletteDisc(scheme.primary, scheme.secondary, scheme.tertiary, cut = scheme.surfaceContainer)
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = scheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    // Kept clear of the corner check, so a long name ellipsizes before reaching it
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                )
            }

            Spacer(Modifier.height(10.dp))
            // A strip of the theme's other colours along the bottom: its containers and its surface tone
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            ) {
                // Each container is pulled part way toward its key colour, so the strip reads lively
                // instead of muddy, most of all in dark mode where the containers are deep tones
                listOf(
                    lerp(scheme.primaryContainer, scheme.primary, SwatchLift),
                    lerp(scheme.secondaryContainer, scheme.secondary, SwatchLift),
                    lerp(scheme.tertiaryContainer, scheme.tertiary, SwatchLift),
                    lerp(scheme.surfaceVariant, scheme.outline, SwatchLift)
                ).forEach { swatch ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .background(swatch)
                    )
                }
            }
        }

        ThemeCheck(
            isSelected = isSelected,
            containerColor = scheme.primary,
            contentColor = scheme.onPrimary,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}

/**
 * The corner check, on Material 3 Expressive's standard motion: it pops in on the fast spatial
 * spring with a quick fade, and leaves on the fast effects spec with no bounce.
 */
@Composable
private fun ThemeCheck(
    isSelected: Boolean,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val motion = MaterialTheme.motionScheme
    AnimatedVisibility(
        visible = isSelected,
        enter = scaleIn(motion.fastSpatialSpec()) + fadeIn(motion.fastEffectsSpec()),
        exit = scaleOut(motion.fastEffectsSpec()) + fadeOut(motion.fastEffectsSpec()),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(containerColor)
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = stringResource(R.string.state_selected),
                tint = contentColor,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

/** How long the screen fades across when a colour theme is chosen. */
const val COLOR_THEME_FADE_MS = 250

/** How far each swatch in the strip is pulled toward its key colour. */
private const val SwatchLift = 0.35f

/** The palette disc's size on a tile. */
private val DiscSize = 44.dp

/**
 * A disc split as Android's colour picker shows a palette: [top] across the upper half,
 * [bottomLeft] and [bottomRight] filling the two lower quarters, with thin gaps between them in
 * [cut], the card's own colour, so they read as cuts in light and dark alike.
 */
private fun DrawScope.drawPaletteDisc(top: Color, bottomLeft: Color, bottomRight: Color, cut: Color) {
    val diameter = size.minDimension
    val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
    val arc = Size(diameter, diameter)
    drawArc(top, startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = topLeft, size = arc)
    drawArc(bottomRight, startAngle = 0f, sweepAngle = 90f, useCenter = true, topLeft = topLeft, size = arc)
    drawArc(bottomLeft, startAngle = 90f, sweepAngle = 90f, useCenter = true, topLeft = topLeft, size = arc)
    val gap = 2.dp.toPx()
    val center = Offset(size.width / 2f, size.height / 2f)
    drawLine(cut, Offset(topLeft.x, center.y), Offset(topLeft.x + diameter, center.y), gap)
    drawLine(cut, center, Offset(center.x, topLeft.y + diameter), gap)
}
