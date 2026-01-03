package com.yugentech.sessions.ui.dash.components.homeScreen.middle

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimerDisplay(
    displayTime: Int,       // Current seconds remaining
    selectedDuration: Int,  // Total seconds for this session
    isStudying: Boolean,    // Is the timer ticking?
    modifier: Modifier = Modifier,
    idleLabel: String = "Press play\nto start"
) {
    // 1. Responsive Sizing
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val timerSize = when {
        screenHeight < 600.dp -> 220.dp
        screenHeight < 700.dp -> 260.dp
        else -> 320.dp
    }

    // Slightly thicker stroke to emphasize the "Wavy" shape
    val strokeWidth = 10.dp

    // 2. Smooth Progress Animation
    val progress = remember(displayTime, selectedDuration) {
        if (selectedDuration > 0)
            (1f - (displayTime.toFloat() / selectedDuration.toFloat())).coerceIn(0f, 1f)
        else 0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "progress"
    )

    // 3. Live "Squiggle" Animation
    // This rotates the wave phase continuously so the circle looks "alive" like an amoeba
    val infiniteTransition = rememberInfiniteTransition(label = "wave-motion")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing), // 4 seconds per full rotation
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    // Only animate the wave motion when studying (or always if you prefer)
    val activePhase = if (isStudying) wavePhase else 0f
    val showTimeDisplay = isStudying || (displayTime < selectedDuration)

    Box(
        modifier = modifier.padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- CUSTOM WAVY CIRCLE INDICATOR ---
        SquigglyCircleIndicator(
            progress = if (showTimeDisplay) animatedProgress else 1f, // Full circle when idle
            phase = activePhase,
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            strokeWidth = strokeWidth,
            modifier = Modifier.size(timerSize)
        )

        // --- TEXT CONTENT ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 48.dp) // Extra padding to clear the waves
        ) {
            if (showTimeDisplay) {
                // ACTIVE / PAUSED STATE
                Text(
                    text = "%02d:%02d".format(displayTime / 60, displayTime % 60),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontFeatureSettings = "tnum" // Tabular numbers (monospaced digits)
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isStudying) "focusing..." else "paused",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                // IDLE (RESET) STATE
                Text(
                    text = idleLabel,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * A custom indicator that draws a circle where the radius oscillates based on a sine wave.
 * This creates a "Wavy" circumference.
 */
@Composable
fun SquigglyCircleIndicator(
    progress: Float,
    phase: Float,
    color: Color,
    trackColor: Color,
    strokeWidth: Dp,
    modifier: Modifier = Modifier,
    waveCount: Int = 18,    // How many bumps around the circle
    amplitude: Dp = 5.dp    // How deep the bumps are
) {
    Canvas(modifier = modifier) {
        // 1. Setup dimensions
        // Reduce radius slightly so the waves don't clip outside the bounds
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = (size.minDimension / 2) - amplitude.toPx() - (strokeWidth.toPx() / 2)
        val ampPx = amplitude.toPx()

        // Helper function to calculate x,y for a specific angle
        fun getWavyPoint(angleRad: Float): Offset {
            // The radius changes based on the angle (creating the wave)
            // 'phase' moves the wave around the circle
            val currentRadius = maxRadius + ampPx * sin((angleRad * waveCount) + phase)

            // Standard polar to cartesian conversion
            // We subtract PI/2 to start drawing at 12 o'clock (top)
            return Offset(
                x = center.x + currentRadius * cos(angleRad - PI.toFloat() / 2),
                y = center.y + currentRadius * sin(angleRad - PI.toFloat() / 2)
            )
        }

        // 2. Draw Track (The faint full circle background)
        val trackPath = Path()
        val steps = 360 // Resolution
        for (i in 0..steps) {
            val angle = (i.toFloat() / steps) * 2 * PI.toFloat()
            val point = getWavyPoint(angle)
            if (i == 0) trackPath.moveTo(point.x, point.y)
            else trackPath.lineTo(point.x, point.y)
        }
        trackPath.close()

        drawPath(
            path = trackPath,
            color = trackColor,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        // 3. Draw Active Progress (The filled part)
        if (progress > 0f) {
            val progressPath = Path()
            // Only draw up to the progress percentage
            val progressSteps = (steps * progress).toInt().coerceAtLeast(1)

            for (i in 0..progressSteps) {
                val angle = (i.toFloat() / steps) * 2 * PI.toFloat()
                val point = getWavyPoint(angle)
                if (i == 0) progressPath.moveTo(point.x, point.y)
                else progressPath.lineTo(point.x, point.y)
            }

            drawPath(
                path = progressPath,
                color = color,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}