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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun TimerDisplay(
    displayTime: Int,
    selectedDuration: Int,
    isStudying: Boolean,
    modifier: Modifier = Modifier
) {
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

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(300.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            strokeWidth = 6.dp,
            trackColor = Color.Transparent,
            strokeCap = StrokeCap.Round,
        )

        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(300.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 6.dp,
            trackColor = Color.Transparent,
            strokeCap = StrokeCap.Round,
        )

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
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Light
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isStudying) "time remaining" else "session duration",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}