package com.yugentech.sessions.ui.components.homeScreen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.ui.Tokens
import java.util.Locale

@Composable
fun TimerDisplay(
    displayTime: Int,
    selectedDuration: Int,
    isStudying: Boolean,
    modifier: Modifier = Modifier
) {
    // Assuming 'Tokens' is provided via CompositionLocal or is a static object
    // as seen in your other files.
    val tokens = Tokens

    val safeProgress = remember(displayTime, selectedDuration) {
        if (selectedDuration > 0)
            (1f - (displayTime / selectedDuration.toFloat())).coerceIn(0f, 1f)
        else 0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = safeProgress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "TimerProgress"
    )

    // Use token for timer size
    val timerSize = tokens.components.timerSize

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Background track
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(timerSize),
            color = MaterialTheme.colorScheme.surfaceContainer,
            strokeWidth = tokens.strokeWidths.extraThick, // Use token
            trackColor = Color.Transparent,
            strokeCap = StrokeCap.Round,
        )

        // Progress indicator
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(timerSize),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = tokens.strokeWidths.extraThick, // Use token
            trackColor = Color.Transparent,
            strokeCap = StrokeCap.Round,
        )

        // Center time display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format(
                    Locale.US, "%02d:%02d",
                    displayTime / 60, displayTime % 60
                ),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = tokens.typography.display.sp, // Use token
                    fontWeight = FontWeight.Light
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(tokens.spacing.sm)) // Use token

            Text(
                text = if (isStudying) "time remaining" else "session duration",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = tokens.typography.body.sp // Use token
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}