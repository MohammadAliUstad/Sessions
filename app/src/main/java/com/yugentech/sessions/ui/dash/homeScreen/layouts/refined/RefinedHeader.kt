package com.yugentech.sessions.ui.dash.homeScreen.layouts.refined

import com.yugentech.sessions.localization.rememberTodayLabel
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.yugentech.sessions.alerts.model.BackgroundSound
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.TaskTapInset
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.taskTapArea
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable
import androidx.compose.ui.unit.sp

/**
 * The top of Home: the task as the heading (tap to change it while the timer isn't running), and the
 * background sound in a pill on the right (tap to mute or unmute).
 */
@Composable
fun RefinedHeader(
    isRunning: Boolean,
    sessionTask: String,
    onTaskClick: () -> Unit,
    onSoundBadgeClick: () -> Unit,
    isAmbientEnabled: Boolean = true,
    activeBackgroundSoundId: String? = null
) {
    val fadeThrough = AppAnimations.Transitions.rememberExpressiveFadeThrough()
    val backgroundSound = remember(activeBackgroundSoundId) {
        BackgroundSound.fromId(activeBackgroundSoundId)
    }
    val today = rememberTodayLabel()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Starts short of the usual margin by the tap area's own inset, so the text still lines
            // up with the content below while the ripple gets room around it
            .padding(
                start = MaterialTheme.spacing.m - TaskTapInset,
                end = MaterialTheme.spacing.m,
                top = MaterialTheme.spacing.s,
                bottom = MaterialTheme.spacing.s
            ),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .taskTapArea(enabled = !isRunning, onClick = onTaskClick)
        ) {
            // Today's date as a quiet overline, as Studio has it
            Text(
                text = today,
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.6.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(Modifier.height(MaterialTheme.spacing.xxs))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedContent(
                    targetState = sessionTask,
                    transitionSpec = { fadeThrough() },
                    label = "sessionTaskText",
                    modifier = Modifier.weight(1f, fill = false)
                ) { task ->
                    Text(
                        text = task.ifBlank { stringResource(R.string.task_placeholder_enter) },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = if (task.isBlank()) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (task.isBlank()) {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                AnimatedVisibility(
                    visible = !isRunning,
                    enter = fadeIn() + scaleIn(initialScale = 0.6f),
                    exit = fadeOut() + scaleOut(targetScale = 0.6f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = MaterialTheme.spacing.s)
                            .size(MaterialTheme.icons.small),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        SoundPill(
            backgroundSound = backgroundSound,
            isEnabled = isAmbientEnabled,
            isRunning = isRunning,
            onClick = onSoundBadgeClick
        )
    }
}

/** The background sound by name, muted or not; hidden behind a quiet "No sound" when none is chosen. */
@Composable
internal fun SoundPill(
    backgroundSound: BackgroundSound,
    isEnabled: Boolean,
    isRunning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val soundName = when (backgroundSound) {
        BackgroundSound.NONE -> stringResource(R.string.sound_no_sound)
        BackgroundSound.RAIN -> stringResource(R.string.sound_rain)
        BackgroundSound.BROWN_NOISE -> stringResource(R.string.sound_brown_noise)
        BackgroundSound.FIREPLACE -> stringResource(R.string.sound_fireplace)
        BackgroundSound.LIBRARY -> stringResource(R.string.sound_library)
        BackgroundSound.RIVERSIDE -> stringResource(R.string.sound_riverside)
        BackgroundSound.FOREST -> stringResource(R.string.sound_forest)
    }
    val isNone = backgroundSound == BackgroundSound.NONE
    val isMuted = !isEnabled && !isNone
    val playing = isRunning && !isMuted && !isNone

    val container by animateColorAsState(
        when {
            playing -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "soundPillContainer"
    )
    val content by animateColorAsState(
        when {
            playing -> MaterialTheme.colorScheme.onSecondaryContainer
            isMuted || isNone -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "soundPillContent"
    )
    val fadeThrough = AppAnimations.Transitions.rememberExpressiveFadeThrough()

    Row(
        modifier = modifier
            .pressClickable(
                shape = CircleShape,
                enabled = !isNone,
                onClickLabel = if (isMuted) stringResource(R.string.cd_unmute_sound) else stringResource(R.string.cd_mute_sound),
                onClick = onClick
            )
            .background(container, CircleShape)
            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(targetState = isMuted || isNone, transitionSpec = { fadeThrough() }, label = "soundIcon") { off ->
            Icon(
                imageVector = if (off) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.icons.small),
                tint = content
            )
        }
        Spacer(Modifier.width(MaterialTheme.spacing.xs))
        AnimatedContent(targetState = if (isMuted) stringResource(R.string.sound_muted) else soundName, transitionSpec = { fadeThrough() }, label = "soundText") { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = content,
                maxLines = 1
            )
        }
    }
}
