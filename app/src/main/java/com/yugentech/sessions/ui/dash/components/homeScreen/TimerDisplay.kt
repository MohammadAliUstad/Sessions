package com.yugentech.sessions.ui.dash.components.homeScreen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.animations
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.theme.tokens.strokes

@Composable
fun TimerDisplay(
    displayTime: Int,
    selectedDuration: Int,
    isStudying: Boolean,
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    val screenHeightDp = with(density) {
        windowInfo.containerSize.height.toDp()
    }

    val timerSize = when {
        screenHeightDp < 600.dp -> 200.dp
        screenHeightDp < 700.dp -> 240.dp
        else -> MaterialTheme.components.timerSize
    }

    val progress = remember(displayTime, selectedDuration) {
        if (selectedDuration > 0)
            (1f - (displayTime / selectedDuration.toFloat())).coerceIn(0f, 1f)
        else 0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = MaterialTheme.animations.durations.medium2,
            easing = FastOutSlowInEasing
        ),
        label = "timer-progress"
    )

    Box(
        modifier = modifier.padding(MaterialTheme.spacing.xsSmall),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(timerSize),
            color = MaterialTheme.colorScheme.surfaceContainer,
            strokeWidth = MaterialTheme.strokes.extraThick,
            trackColor = Color.Transparent,
            strokeCap = StrokeCap.Round
        )

        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(timerSize),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = MaterialTheme.strokes.thick,
            trackColor = Color.Transparent,
            strokeCap = StrokeCap.Round
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "%02d:%02d".format(displayTime / 60, displayTime % 60),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))

            Text(
                text = if (isStudying) "time remaining" else "session duration",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}