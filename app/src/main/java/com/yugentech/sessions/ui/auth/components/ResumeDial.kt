package com.yugentech.sessions.ui.auth.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.ui.dash.homeScreen.layouts.refined.RefinedTimer
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.modeColor

/** How big the sign-in dial is. */
internal val AuthHeaderHeight = 176.dp

/** The block the dial shows: five minutes, as in the onboarding's first scene. */
private const val BLOCK_SECONDS = 5 * 60

/** How far through the block it sits. */
private const val PROGRESS = 0.35f

/**
 * Sign-in's opener: Home's timer as the onboarding's first scene draws it (the Refined layout's
 * wavy ring, a light track in the ring's own colour, plain digits), held still 35% of the way
 * through a five-minute block. Nothing counts and the glow stays settled; the only movement is
 * the ring's own waves running along it.
 */
@Composable
fun ResumeDial(modifier: Modifier = Modifier) {
    val remaining = (BLOCK_SECONDS * (1f - PROGRESS)).toInt()
    RefinedTimer(
        displayTime = remaining,
        selectedDuration = BLOCK_SECONDS,
        // Not "running", so the glow doesn't breathe and the hint stays fixed
        isStudying = false,
        currentMode = TimerMode.Focus,
        modifier = modifier,
        isSessionActive = false,
        idleHint = "Focusing",
        showEndTime = false,
        dialSize = AuthHeaderHeight,
        timeFontSize = 36.sp,
        trackColor = modeColor(TimerMode.Focus).copy(alpha = 0.18f),
        rollingDigits = false,
        animateProgress = false
    )
}
