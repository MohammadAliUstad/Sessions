package com.yugentech.sessions.ui.dash.homeScreen.layouts.studio

import com.yugentech.sessions.localization.rememberTodayLabel
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.alerts.model.BackgroundSound
import com.yugentech.sessions.ui.dash.homeScreen.layouts.refined.SoundPill
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.HomeActions
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.HomeUi
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.TaskTapInset
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.modeColor
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.taskTapArea

/**
 * Studio: Home as one calm composition. The day and the task set the scene at the top; the dial
 * holds the middle; the plan (or, once started, the session) sits beneath it; and the controls
 * float in a dock at the bottom. A faint wash of the mode's colour falls from the top of the
 * screen and deepens while the clock runs.
 */
@Composable
fun StudioHome(ui: HomeUi, actions: HomeActions, screenHeight: Dp) {
    val config = ui.config
    val accent = modeColor(ui.mode)
    // The glow across the top is drawn by HomeScreen behind every newer layout
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = screenHeight - 1.dp)
    ) {
        // As large as reads well, without crowding the rest on short screens
        val dialSize = min(min(maxWidth * 0.80f, 312.dp), screenHeight * 0.44f)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = screenHeight - 1.dp)
                // The side margin is on each child instead, so the header's tap area can reach
                // past it, exactly as Refined's does
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StudioHeader(
                task = config.sessionTask,
                // Renaming is for before starting or while paused, never while the timer runs
                canEdit = !ui.isRunning,
                soundId = config.activeBackgroundSoundId,
                soundOn = config.isAmbientEnabled,
                soundPlaying = ui.isRunning,
                onTask = actions.onTaskClick,
                onToggleSound = actions.onToggleAmbient
            )

            Spacer(Modifier.height(8.dp))

            StudioDial(
                seconds = ui.timer.currentTime.toInt(),
                progress = ui.blockProgress,
                mode = ui.mode,
                isRunning = ui.isRunning,
                isSessionActive = ui.isSessionActive,
                errorMessage = ui.errorMessage,
                size = dialSize
            )

            Spacer(Modifier.height(8.dp))

            // A fixed slot, the same height as both cards, so the dial above never moves on the swap
            AnimatedContent(
                targetState = ui.isSessionActive,
                transitionSpec = {
                    (fadeIn(tween(400, delayMillis = 100)) + scaleIn(tween(400), initialScale = 0.96f))
                        .togetherWith(fadeOut(tween(200)))
                },
                label = "studioMiddle",
                modifier = Modifier
                    .padding(horizontal = ScreenMargin)
                    .fillMaxWidth()
                    .height(StudioCardHeight)
            ) { active ->
                if (active) {
                    StudioSessionCard(
                        state = ui.dashboard,
                        targetSets = config.targetSets,
                        blockProgress = ui.blockProgress,
                        isRunning = ui.isRunning,
                        pulse = ui.pulse,
                        cardStyle = ui.cardStyle,
                        animationStyle = ui.cardAnimationStyle,
                        onSkip = actions.onSkip
                    )
                } else {
                    StudioPlan(
                        config = config,
                        onFocus = actions.onFocusClick,
                        onBreak = actions.onShortBreakClick,
                        onSets = actions.onSetsClick
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            StudioDock(
                isRunning = ui.isRunning,
                isSessionActive = ui.isSessionActive,
                onStartStop = actions.onStartStop,
                onSound = actions.onSoundClick,
                onSets = actions.onSetsClick,
                onDiscard = actions.onDiscard,
                onFinish = actions.onFinish,
                modifier = Modifier.padding(horizontal = ScreenMargin)
            )
        }
    }
}

/** The side margin of everything on the page; the task text lines up with it. */
private val ScreenMargin = 20.dp

/** Today's date as a quiet overline, the task as the heading, and the sound as a small round toggle. */
@Composable
private fun StudioHeader(
    task: String,
    canEdit: Boolean,
    soundId: String?,
    soundOn: Boolean,
    soundPlaying: Boolean,
    onTask: () -> Unit,
    onToggleSound: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val today = rememberTodayLabel()
    val sound = remember(soundId) { BackgroundSound.fromId(soundId) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Starts short of the side margin by the tap area's own inset, so the text still lines
            // up with the content below while the ripple gets room around it: the same as Refined
            .padding(start = ScreenMargin - TaskTapInset, end = ScreenMargin),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .taskTapArea(enabled = canEdit, onClick = onTask)
        ) {
            Text(
                text = today,
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.6.sp,
                color = scheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedContent(
                    targetState = task,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                    label = "studioTask",
                    modifier = Modifier.weight(1f, fill = false)
                ) { t ->
                    Text(
                        text = t.ifBlank { stringResource(R.string.task_placeholder_enter) },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = if (t.isBlank()) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (t.isBlank()) scheme.onSurfaceVariant.copy(alpha = 0.6f) else scheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                AnimatedVisibility(
                    visible = canEdit,
                    enter = fadeIn() + scaleIn(initialScale = 0.6f),
                    exit = fadeOut() + scaleOut(targetScale = 0.6f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = scheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(18.dp)
                    )
                }
            }
        }

        SoundPill(
            backgroundSound = sound,
            isEnabled = soundOn,
            isRunning = soundPlaying,
            onClick = onToggleSound,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
