package com.yugentech.sessions.ui.dash.homeScreen.components.topRow

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.yugentech.sessions.alerts.model.BackgroundSound
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun SessionHeader(
    isRunning: Boolean,
    sessionTask: String,
    onTaskChange: (String) -> Unit,
    onSoundBadgeClick: () -> Unit,
    isAmbientEnabled: Boolean = true,
    activeBackgroundSoundId: String? = null
) {
    var textFieldValue by remember { mutableStateOf(sessionTask) }

    LaunchedEffect(sessionTask) {
        if (textFieldValue != sessionTask) {
            textFieldValue = sessionTask
        }
    }

    val backgroundSound = remember(activeBackgroundSoundId) {
        BackgroundSound.fromId(activeBackgroundSoundId)
    }
    val focusManager = LocalFocusManager.current

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
                .clip(RoundedCornerShape(MaterialTheme.corners.medium)),
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
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue
                        onTaskChange(newValue)
                    },
                    enabled = !isRunning,
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    decorationBox = { innerTextField ->
                        if (sessionTask.isEmpty()) {
                            Text(
                                text = "Enter a name...",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                )

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

        SoundBadge(
            backgroundSound = backgroundSound,
            isEnabled = isAmbientEnabled,
            onClick = onSoundBadgeClick
        )
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
    backgroundSound: BackgroundSound,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val soundName = when (backgroundSound) {
        BackgroundSound.NONE -> "None"
        BackgroundSound.RAIN -> "Rain"
        BackgroundSound.BROWN_NOISE -> "Brown Noise"
        BackgroundSound.FIREPLACE -> "Fireplace"
        BackgroundSound.LIBRARY -> "Library"
        BackgroundSound.RIVERSIDE -> "Riverside"
        BackgroundSound.FOREST -> "Forest"
    }

    val isNone = backgroundSound == BackgroundSound.NONE
    val isMuted = !isEnabled && !isNone

    val containerColor by animateColorAsState(
        targetValue = if (isMuted)
            MaterialTheme.colorScheme.surfaceContainer
        else
            MaterialTheme.colorScheme.surfaceContainerHigh,
        label = "soundBadgeContainer"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (isMuted) 0.5f else 1f,
        label = "soundBadgeAlpha"
    )

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(MaterialTheme.corners.medium),
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.corners.medium))
            .clickable(enabled = !isNone) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.sm,
                vertical = MaterialTheme.spacing.m
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = isMuted,
                transitionSpec = {
                    fadeIn(
                        tween(AppAnimations.Durations.Standard)
                    ) togetherWith fadeOut(
                        tween(AppAnimations.Durations.Standard)
                    )
                },
                label = "soundIcon"
            ) { muted ->
                Icon(
                    imageVector = if (muted)
                        Icons.AutoMirrored.Rounded.VolumeOff
                    else
                        Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = "Sound",
                    modifier = Modifier.size(MaterialTheme.icons.smallMedium),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                )
            }

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))

            AnimatedContent(
                targetState = if (isMuted) "Muted" else soundName,
                transitionSpec = {
                    fadeIn(
                        tween(AppAnimations.Durations.Standard)
                    ) togetherWith fadeOut(
                        tween(AppAnimations.Durations.Standard)
                    )
                },
                label = "soundText"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                    maxLines = 1
                )
            }
        }
    }
}