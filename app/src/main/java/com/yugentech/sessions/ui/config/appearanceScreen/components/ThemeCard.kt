package com.yugentech.sessions.ui.config.appearanceScreen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable

/**
 * A colour theme as a tile in its own theme's surface colours: a round palette disc in the top
 * corner split into the theme's three key colours (primary across the top, secondary and
 * tertiary below, as Android's own wallpaper colour picker shows them), then the name and a
 * strip of swatches along the bottom. Choosing one spins its disc a quarter turn and drops a
 * check into its centre.
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
    LaunchedEffect(isSelected) {
        if (isSelected) {
            spin.snapTo(-90f)
            spin.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
        }
    }
    val check by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "paletteCheck"
    )
    val borderColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else scheme.outlineVariant.copy(alpha = 0.5f),
        label = "paletteBorder"
    )

    Column(
        modifier = modifier
            .aspectRatio(TILE_ASPECT)
            .pressClickable(shape = shape, pressedScale = 0.95f) {
                if (!isSelected) {
                    haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                    onClick()
                }
            }
            .background(scheme.surfaceContainer)
            .border(if (isSelected) 2.5.dp else 1.dp, borderColor, shape)
            .padding(14.dp)
    ) {
        // The palette disc, in the top corner
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(DiscSize)) {
            Canvas(
                modifier = Modifier
                    .size(DiscSize)
                    .graphicsLayer { rotationZ = spin.value }
            ) {
                drawPaletteDisc(scheme.primary, scheme.secondary, scheme.tertiary, cut = scheme.surfaceContainer)
            }
            // The check, in a small primary-container dot at the centre
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer {
                        scaleX = check
                        scaleY = check
                        alpha = check.coerceIn(0f, 1f)
                    }
                    .clip(CircleShape)
                    .background(scheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = scheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = scheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(10.dp))
        // A strip of the theme's other colours along the bottom: its containers and its surface tone
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape)
        ) {
            listOf(
                scheme.primaryContainer,
                scheme.secondaryContainer,
                scheme.tertiaryContainer,
                scheme.surfaceVariant
            ).forEach { swatch ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .background(swatch)
                )
            }
        }
    }
}

/** The tiles' shape: a little wider than tall. */
private const val TILE_ASPECT = 1.3f

/** The palette disc's size on a tile. */
private val DiscSize = 64.dp

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
