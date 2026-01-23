package com.yugentech.sessions.ui.dash.components.homeScreen.topRow

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.yugentech.sessions.alerts.models.BackgroundSound
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun SessionHeader(
    isRunning: Boolean,
    sessionTask: String,
    onTaskClick: () -> Unit,
    activeBackgroundSoundId: String? = null
) {
    val backgroundSound = remember(activeBackgroundSoundId) {
        BackgroundSound.fromId(activeBackgroundSoundId)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.m,
                vertical = MaterialTheme.spacing.s
            ),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(MaterialTheme.corners.medium))
                .clickable(
                    enabled = !isRunning,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onTaskClick() },
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(MaterialTheme.corners.medium)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.m,
                    vertical = MaterialTheme.spacing.m
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                if (sessionTask.isBlank()) {
                    Text(
                        text = "Enter a task...",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = sessionTask,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (!isRunning) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(MaterialTheme.icons.smallMedium),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        StatusBadge(isRunning = isRunning)

        SoundBadge(backgroundSound = backgroundSound)
    }
}

@Composable
fun StatusBadge(
    isRunning: Boolean
) {
    val containerColor by animateColorAsState(
        targetValue = if (isRunning)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceContainerHigh,
        label = "badgeContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isRunning)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        label = "badgeContent"
    )
    val dotColor by animateColorAsState(
        targetValue = if (isRunning)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        label = "dotColor"
    )

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(MaterialTheme.corners.medium)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.sm,
                vertical = MaterialTheme.spacing.m
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.components.dotSize)
                    .background(dotColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))

            AnimatedContent(
                targetState = isRunning,
                transitionSpec = {
                    fadeIn(
                        tween(AppAnimations.Durations.Standard)
                    ) togetherWith fadeOut(
                        tween(AppAnimations.Durations.Standard)
                    )
                },
                label = "statusText"
            ) { running ->
                Text(
                    text = if (running) "Active" else "Idle",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = contentColor,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SoundBadge(
    backgroundSound: BackgroundSound
) {
    val soundName = when (backgroundSound) {
        BackgroundSound.NONE -> "None"
        BackgroundSound.RAIN -> "Rain"
        BackgroundSound.BROWN_NOISE -> "Brown Noise"
        BackgroundSound.FIREPLACE -> "Fireplace"
        BackgroundSound.LIBRARY -> "Library"
        BackgroundSound.RIVERSIDE -> "Riverside"
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(MaterialTheme.corners.medium)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.sm,
                vertical = MaterialTheme.spacing.m
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = "Sound",
                modifier = Modifier.size(MaterialTheme.icons.smallMedium),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))

            Text(
                text = soundName,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}