package com.yugentech.sessions.ui.auth.components

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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** How tall both auth headers are, so the text under them never moves as they animate. */
internal val AuthHeaderHeight = 176.dp

/** One focus block, in seconds: what the dial counts down. */
private const val BLOCK_SECONDS = 25 * 60

/** Where the dial starts: partway through, as if you'd stepped away mid-session. */
private const val START_SECONDS = 17 * 60 + 43

/** Ticks around the dial, one a minute on a clock face; every fifth is longer. */
private const val TICKS = 60

/** Task names the dial cycles through, one per minute that ticks over. */
private val Tasks = listOf("Deep Work", "Study Flow", "Writing", "Design Sprint", "Exam Prep")

/**
 * Sign-in's opener: a focus session still running where you left it. A watch-style dial (the
 * same language as the Studio home) whose ticks fill as the block runs, with a bead riding the rim
 * and a soft light breathing in the face; inside, a real countdown that ticks every second, digits
 * rolling as they change, under the task it belongs to.
 */
@Composable
fun ResumeDial(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val accent = colors.primary
    val ink = colors.onBackground

    var seconds by remember { mutableIntStateOf(START_SECONDS) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds = if (seconds <= 0) BLOCK_SECONDS else seconds - 1
        }
    }
    val target = 1f - seconds / BLOCK_SECONDS.toFloat()
    val progress by animateFloatAsState(target, tween(900, easing = FastOutSlowInEasing), label = "resumeProgress")
    val breath by rememberInfiniteTransition(label = "resumeBreath").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Reverse),
        label = "resumeBreathValue"
    )
    val task = Tasks[(seconds / 60) % Tasks.size]

    Box(modifier = modifier.size(AuthHeaderHeight), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val faceRadius = size.minDimension / 2f - 8.dp.toPx()

            // A soft light gathered in the face, breathing slowly
            drawCircle(
                Brush.radialGradient(
                    listOf(accent.copy(alpha = 0.14f + 0.08f * breath), Color.Transparent),
                    center,
                    faceRadius
                ),
                faceRadius
            )
            // A hairline rim
            drawCircle(ink.copy(alpha = 0.14f), faceRadius, style = Stroke(1.dp.toPx()))

            // Ticks, filling clockwise from the top; the leading one fills smoothly
            val tickOuter = faceRadius - 9.dp.toPx()
            val reached = progress * TICKS
            val track = ink.copy(alpha = 0.18f)
            for (i in 0 until TICKS) {
                val major = i % 5 == 0
                val length = (if (major) 10.dp else 5.dp).toPx()
                val fill = (reached - i).coerceIn(0f, 1f)
                val angle = i.toFloat() / TICKS * 2f * PI.toFloat() - PI.toFloat() / 2f
                val dir = Offset(cos(angle), sin(angle))
                drawLine(
                    color = lerp(track, accent, fill),
                    start = center + dir * (tickOuter - length),
                    end = center + dir * tickOuter,
                    strokeWidth = (if (major) 2.2.dp else 1.5.dp).toPx(),
                    cap = StrokeCap.Round
                )
            }

            // The bead, riding the rim at exactly how far the block has run
            val angle = progress * 2f * PI.toFloat() - PI.toFloat() / 2f
            val at = center + Offset(cos(angle), sin(angle)) * faceRadius
            drawCircle(accent.copy(alpha = 0.25f), 9.dp.toPx(), at)
            drawCircle(accent, 4.dp.toPx(), at)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "FOCUS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = accent
            )
            RollingTime(
                seconds = seconds,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Light,
                    fontFeatureSettings = "tnum",
                    letterSpacing = (-1).sp
                ),
                color = ink
            )
            AnimatedContent(
                targetState = task,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(250)) },
                label = "resumeTask"
            ) { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium,
                    color = ink.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/** "MM:SS", each digit rolling down into place as it changes, the way a counter turns over. */
@Composable
private fun RollingTime(seconds: Int, style: TextStyle, color: Color) {
    val text = "%02d:%02d".format(seconds / 60, seconds % 60)
    Row {
        text.forEachIndexed { index, char ->
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    (slideInVertically(tween(300)) { -it / 3 } + fadeIn(tween(300)))
                        .togetherWith(slideOutVertically(tween(300)) { it / 3 } + fadeOut(tween(180)))
                        .using(SizeTransform(clip = false))
                },
                label = "resumeDigit$index"
            ) { c ->
                Text(text = c.toString(), style = style, color = color)
            }
        }
    }
}
