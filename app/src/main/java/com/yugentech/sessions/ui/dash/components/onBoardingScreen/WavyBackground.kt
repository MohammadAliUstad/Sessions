package com.yugentech.sessions.ui.dash.components.onBoardingScreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun WavyBackground(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    // Rotate the entire wave slowly
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing), // Slower, smoother rotation
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse the size slightly (Breathing effect)
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Canvas(modifier = modifier.graphicsLayer {
        rotationZ = rotation
        scaleX = scale
        scaleY = scale
    }) {
        val radius = size.minDimension / 2

        // 1. Outer Ring (Secondary Color - Subtle)
        drawCircle(
            color = secondaryColor.copy(alpha = 0.4f),
            radius = radius,
            style = Stroke(width = 6.dp.toPx()) // Thicker stroke for "Expressive" feel
        )

        // 2. Inner Circle (Primary Color - Filled)
        // We draw it slightly smaller to create the ring effect
        drawCircle(
            color = primaryColor.copy(alpha = 0.5f),
            radius = radius * 0.85f,
            style = Fill
        )
    }
}