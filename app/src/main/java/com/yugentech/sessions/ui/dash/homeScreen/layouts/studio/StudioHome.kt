package com.yugentech.sessions.ui.dash.homeScreen.layouts.studio

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.yugentech.sessions.alerts.model.BackgroundSound
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.HomeActions
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.HomeUi
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.modeColor
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable
import com.yugentech.sessions.ui.dash.mainScreen.components.topBarInset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StudioHeader(
                task = config.sessionTask,
                canEdit = !ui.isSessionActive,
                soundId = config.activeBackgroundSoundId,
                soundOn = config.isAmbientEnabled,
                soundPlaying = ui.isRunning,
                accent = accent,
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
                mode = ui.mode,
                onStartStop = actions.onStartStop,
                onSound = actions.onSoundClick,
                onSets = actions.onSetsClick,
                onDiscard = actions.onDiscard,
                onFinish = actions.onFinish
            )
        }
    }
}

/** Room between the header text and its tap area's rounded edge. */
private val HeaderInset = 10.dp

/** Today's date as a quiet overline, the task as the heading, and the sound as a small round toggle. */
@Composable
private fun StudioHeader(
    task: String,
    canEdit: Boolean,
    soundId: String?,
    soundOn: Boolean,
    soundPlaying: Boolean,
    accent: Color,
    onTask: () -> Unit,
    onToggleSound: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val today = remember { SimpleDateFormat("EEEE · d MMMM", Locale.getDefault()).format(Date()).uppercase() }
    val sound = remember(soundId) { BackgroundSound.fromId(soundId) }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        // The tap area's rounded corners would shave the first letter if the text sat flush
        // against them, so the text gets room inside it and the whole block shifts back by the
        // same amount to stay aligned with everything below
        Column(
            modifier = Modifier
                .weight(1f)
                .offset(x = -HeaderInset)
                .pressClickable(
                    shape = RoundedCornerShape(16.dp),
                    enabled = canEdit,
                    onClickLabel = "Change task",
                    onClick = onTask
                )
                .padding(horizontal = HeaderInset, vertical = 4.dp)
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
                        text = t.ifEmpty { "What are you working on?" },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = if (t.isEmpty()) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (t.isEmpty()) scheme.onSurfaceVariant.copy(alpha = 0.6f) else scheme.onSurface,
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

        // Only there when a sound is chosen; tap to mute or unmute it
        AnimatedVisibility(visible = sound != BackgroundSound.NONE, enter = fadeIn(), exit = fadeOut()) {
            // The same colours as Refined's sound pill: a proper secondary container while the
            // sound plays, a neutral tone otherwise, and muted dimmed
            val live = soundOn && soundPlaying
            val container by animateColorAsState(
                if (live) scheme.secondaryContainer else scheme.surfaceContainerHigh,
                label = "studioSoundContainer"
            )
            val tint by animateColorAsState(
                when {
                    !soundOn -> scheme.onSurfaceVariant.copy(alpha = 0.5f)
                    live -> scheme.onSecondaryContainer
                    else -> scheme.onSurfaceVariant
                },
                label = "studioSoundTint"
            )
            Icon(
                imageVector = if (soundOn) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                contentDescription = if (soundOn) "Mute sound" else "Unmute sound",
                tint = tint,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .pressClickable(shape = CircleShape, onClick = onToggleSound)
                    .background(container, CircleShape)
                    .padding(12.dp)
                    .size(22.dp)
            )
        }
    }
}
