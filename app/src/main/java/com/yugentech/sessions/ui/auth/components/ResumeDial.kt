package com.yugentech.sessions.ui.auth.components

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.ui.dash.homeScreen.layouts.refined.RefinedTimer
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.modeColor
import kotlinx.coroutines.delay

/** How big the sign-in dial is. */
internal val AuthHeaderHeight = 156.dp

/** The block the dial shows: five minutes, as in the onboarding's first scene. */
private const val BLOCK_SECONDS = 5 * 60

/** How far through the block it sits. */
private const val PROGRESS = 0.35f

/** How long each word stays under the digits. */
private const val WORD_MILLIS = 1_800L

/**
 * Sign-in's opener: Home's timer as the onboarding's first scene draws it (the Refined layout's
 * wavy ring, a light track in the ring's own colour, plain digits), held still 35% of the way
 * through a five-minute block. Nothing counts and the glow stays settled. Under the digits, what
 * people use their focus for (focusing, studying, working, reading…) rolls through in a shuffled
 * order, never the same word twice in a row, while the ring's waves run along it.
 */
@Composable
fun ResumeDial(modifier: Modifier = Modifier) {
    val remaining = (BLOCK_SECONDS * (1f - PROGRESS)).toInt()
    val focusing = stringResource(R.string.timer_hint_focusing)
    val activities = stringArrayResource(R.array.auth_dial_activities)
    // Keyed on the words, so a language switch starts over in the new language
    val words = remember(focusing, activities.toList()) { listOf(focusing) + activities }
    var word by remember(words) { mutableStateOf(words.first()) }

    LaunchedEffect(words) {
        while (true) {
            // One shuffled pass through every word, then another, with no repeat at the seam
            val order = words.shuffled().let { if (it.first() == word) it.drop(1) + it.first() else it }
            for (next in order) {
                delay(WORD_MILLIS)
                word = next
            }
        }
    }

    RefinedTimer(
        displayTime = remaining,
        selectedDuration = BLOCK_SECONDS,
        // Not "running", so the glow doesn't breathe and the hint comes from idleHint
        isStudying = false,
        currentMode = TimerMode.Focus,
        modifier = modifier,
        isSessionActive = false,
        idleHint = word,
        showEndTime = false,
        dialSize = AuthHeaderHeight,
        timeFontSize = 32.sp,
        trackColor = modeColor(TimerMode.Focus).copy(alpha = 0.18f),
        rollingDigits = false,
        animateProgress = false,
        rollingHint = true
    )
}
