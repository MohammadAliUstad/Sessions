package com.yugentech.sessions.ui.dash.homeScreen.layouts.refined

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import android.text.format.DateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.modeColor
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.modeOnColor
import java.util.Date

/**
 * The timer as the centre of Home: a wavy ring in the current mode's colour with a soft glow
 * behind it that breathes while the clock runs. Inside, the mode, the time (each digit rolls as
 * it changes) and a quiet line under it: when this block will end, that it's paused, or how to
 * start. Before a session it shows the full block length, ready to go. The onboarding's demo
 * tunes the rest: its own start hint, no end time and plain digits (its clock runs as a
 * timelapse), a smaller size and a lighter track, and a ring that follows the time directly.
 * [isComplete] swaps the time for a check and [completeTitle] once a block has run out.
 */
@Composable
fun RefinedTimer(
    displayTime: Int,
    selectedDuration: Int,
    isStudying: Boolean,
    currentMode: TimerMode,
    modifier: Modifier = Modifier,
    isSessionActive: Boolean = isStudying,
    errorMessage: String? = null,
    idleHint: String = stringResource(R.string.timer_idle_refined),
    showEndTime: Boolean = true,
    dialSize: Dp = MaterialTheme.components.timerSize,
    timeFontSize: TextUnit = 64.sp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    rollingDigits: Boolean = true,
    animateProgress: Boolean = true,
    rollingHint: Boolean = false,
    isComplete: Boolean = false,
    completeTitle: String = stringResource(R.string.timer_session_complete),
    completeBody: String? = null
) {
    val accent = modeColor(currentMode)
    val targetProgress = if (selectedDuration > 0) {
        (1f - displayTime.toFloat() / selectedDuration.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "timerProgress"
    )
    // A clock that ticks every frame (the onboarding's timelapse) would leave the eased ring
    // trailing far behind the time, so it can follow the time directly instead
    val progress = if (animateProgress) animatedProgress else targetProgress
    // The glow rises while the clock runs and settles when it stops
    val glowLevel by animateFloatAsState(if (isStudying || isComplete) 1f else 0f, tween(900), label = "timerGlow")
    val breath = if (isStudying) {
        rememberInfiniteTransition(label = "timerBreath").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Reverse),
            label = "timerBreathValue"
        )
    } else {
        null
    }

    Box(
        modifier = modifier
            .padding(MaterialTheme.spacing.m)
            .size(dialSize)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val b = breath?.value ?: 0f
            val alpha = 0.06f + glowLevel * (0.08f + 0.06f * b)
            val radius = size.minDimension * (0.50f + 0.04f * glowLevel * b)
            drawCircle(
                brush = Brush.radialGradient(listOf(accent.copy(alpha = alpha), Color.Transparent), center, radius),
                radius = radius
            )
        }

        CircularWavyProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = accent,
            trackColor = trackColor
        )

        // The block's end: the time gives way to a check that pops in, and a short line
        AnimatedContent(
            targetState = isComplete,
            transitionSpec = {
                (fadeIn(tween(300)) + scaleIn(spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow), initialScale = 0.8f))
                    .togetherWith(fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.9f))
            },
            contentAlignment = Alignment.Center,
            label = "timerComplete"
        ) { complete ->
        if (complete) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.xl)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(accent)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = modeOnColor(currentMode),
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(Modifier.height(MaterialTheme.spacing.sm))
                Text(
                    text = completeTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (completeBody != null) {
                    Text(
                        text = completeBody,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.xl)
        ) {
            RefinedModeTag(mode = currentMode)

            Spacer(Modifier.height(MaterialTheme.spacing.xs))

            val timeStyle = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Normal,
                fontFeatureSettings = "tnum",
                fontSize = timeFontSize,
                lineHeight = timeFontSize * 1.0625f
            )
            if (rollingDigits) {
                RollingTime(seconds = displayTime, style = timeStyle, color = MaterialTheme.colorScheme.onSurface)
            } else {
                // Plain digits, as the Classic layout's timer shows them
                Text(
                    text = "%02d:%02d".format(displayTime / 60, displayTime % 60),
                    style = timeStyle,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(MaterialTheme.spacing.xs))

            val hint = when {
                errorMessage != null -> errorMessage
                isStudying && showEndTime -> stringResource(R.string.timer_ends_at, endTime(displayTime))
                isStudying -> stringResource(R.string.timer_hint_focusing)
                isSessionActive -> stringResource(R.string.timer_paused)
                else -> idleHint
            }
            AnimatedContent(
                targetState = hint,
                transitionSpec = {
                    if (rollingHint) {
                        // The same plain vertical swap as onboarding's Continue / Start focusing
                        // button: the new word pushes the old one out upward
                        (slideInVertically(tween(300)) { it } + fadeIn(tween(300)))
                            .togetherWith(slideOutVertically(tween(300)) { -it } + fadeOut(tween(300)))
                            .using(SizeTransform(clip = false) { _, _ -> tween(300) })
                    } else {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                    }
                },
                contentAlignment = Alignment.Center,
                label = "timerHint"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = if (text == errorMessage) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
        }
        }
    }
}

/** "MM:SS" with each digit rolling down into place as it changes, the way a counter turns over. */
@Composable
private fun RollingTime(seconds: Int, style: TextStyle, color: Color) {
    val text = "%02d:%02d".format(seconds / 60, seconds % 60)
    Row {
        text.forEachIndexed { index, char ->
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    (slideInVertically(tween(280)) { -it / 2 } + fadeIn(tween(280)))
                        .togetherWith(slideOutVertically(tween(280)) { it / 2 } + fadeOut(tween(180)))
                        .using(SizeTransform(clip = false))
                },
                label = "timerDigit$index"
            ) { c ->
                Text(text = c.toString(), style = style, color = color)
            }
        }
    }
}

/** The clock time [secondsLeft] from now, in the phone's own 12 or 24 hour format. */
@Composable
private fun endTime(secondsLeft: Int): String {
    val context = LocalContext.current
    return DateFormat.getTimeFormat(context).format(Date(System.currentTimeMillis() + secondsLeft * 1000L))
}
