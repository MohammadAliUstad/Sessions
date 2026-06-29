package com.yugentech.sessions.ui.dash.homeScreen.components.middle

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.timer.state.TimerMode

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimerDisplay(
    displayTime: Int,
    selectedDuration: Int,
    isStudying: Boolean,
    currentMode: TimerMode,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    idleLabel: String = "Press the play button\nto start."
) {
    val targetProgress = if (selectedDuration > 0) {
        val raw = 1f - (displayTime.toFloat() / selectedDuration.toFloat())
        raw.coerceIn(0f, 1f)
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "timer_progress"
    )

    Box(
        modifier = modifier
            .padding(MaterialTheme.spacing.m)
            .size(MaterialTheme.components.timerSize)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {

        CircularWavyProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        val showTimeDisplay = isStudying || (displayTime < selectedDuration)

        AnimatedContent(
            targetState = when {
                errorMessage != null -> errorMessage
                showTimeDisplay -> "timer"
                else -> idleLabel
            },
            transitionSpec = {
                val duration = 300
                (slideInVertically(animationSpec = tween(duration)) { it / 2 } + fadeIn(animationSpec = tween(duration)))
                    .togetherWith(slideOutVertically(animationSpec = tween(duration)) { -it / 2 } + fadeOut(animationSpec = tween(duration)))
                    .using(SizeTransform(clip = false))
            },
            contentAlignment = Alignment.Center,
            label = "timer_text_animation"
        ) { target ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.xl)
            ) {
                if (target == "timer") {
                    Text(
                        text = "%02d:%02d".format(displayTime / 60, displayTime % 60),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontFeatureSettings = "tnum"
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

                    ModeTag(mode = currentMode)
                } else {
                    Text(
                        text = target,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center,
                        color = if (target == errorMessage)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
