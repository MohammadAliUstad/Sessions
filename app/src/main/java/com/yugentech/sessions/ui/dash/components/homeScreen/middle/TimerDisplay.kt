package com.yugentech.sessions.ui.dash.components.homeScreen.middle

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimerDisplay(
    displayTime: Int,
    selectedDuration: Int,
    isStudying: Boolean,
    modifier: Modifier = Modifier,
    idleLabel: String = "Press play\nto start"
) {
    // 1. Calculate Progress
    val targetProgress = if (selectedDuration > 0) {
        val raw = 1f - (displayTime.toFloat() / selectedDuration.toFloat())
        raw.coerceIn(0f, 1f)
    } else {
        0f
    }

    // 2. Smooth Animation
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "timer_progress"
    )

    Box(
        modifier = modifier
            .padding(16.dp)
            .size(260.dp) // Size to fit text comfortably
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // 3. The Standard Indicator (Like the first example)
        // No custom stroke, no custom wavelength. Just the default component.
        CircularWavyProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            // We only set colors to match your theme
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        // 4. Timer Text Overlay
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            val showTimeDisplay = isStudying || (displayTime < selectedDuration)

            if (showTimeDisplay) {
                Text(
                    text = "%02d:%02d".format(displayTime / 60, displayTime % 60),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontFeatureSettings = "tnum"
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