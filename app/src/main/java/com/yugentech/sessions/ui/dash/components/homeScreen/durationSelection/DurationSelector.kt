package com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun DurationControl(
    selectedDuration: Int,
    availableDurations: List<Int>,
    isSessionActive: Boolean,
    onDurationSelected: (Int) -> Unit,
    onStop: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSessionActive)
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(durationMillis = 400),
        label = "container-color"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isSessionActive)
            MaterialTheme.colorScheme.error
        else
            MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 400),
        label = "icon-tint"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(MaterialTheme.corners.large)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = if (isSessionActive) "Session Controls" else "Session Duration",
                    tint = iconTint,
                    modifier = Modifier.size(MaterialTheme.icons.mediumSmall)
                )

                Text(
                    text = if (isSessionActive) "Session Controls" else "Duration",
                    // Standard M3 Role for small section headers
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            AnimatedContent(
                targetState = isSessionActive,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400, delayMillis = 200)) +
                            scaleIn(initialScale = 0.92f, animationSpec = tween(400, delayMillis = 200)))
                        .togetherWith(
                            fadeOut(animationSpec = tween(200)) +
                                    scaleOut(targetScale = 0.92f, animationSpec = tween(200))
                        )
                },
                label = "button-content"
            ) { isActive ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(MaterialTheme.corners.medium)
                        )
                        .padding(MaterialTheme.spacing.xs),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    if (!isActive) {
                        availableDurations.forEach { min ->
                            DurationButton(
                                duration = min,
                                isSelected = selectedDuration == (min * 60),
                                onClick = { onDurationSelected(min) }
                            )
                        }
                    } else {
                        ControlButton(
                            label = "Stop",
                            icon = Icons.Default.Stop,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            onClick = onStop
                        )

                        ControlButton(
                            label = "Save",
                            icon = Icons.Default.Save,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            onClick = onSave
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DurationButton(
    duration: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 300),
        label = "duration-bg-$duration"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 300),
        label = "duration-text-$duration"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .height(MaterialTheme.components.buttonSmall)
            .clip(RoundedCornerShape(MaterialTheme.corners.smallMedium))
            .background(color = backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$duration min",
            // Standard M3 Role for Buttons
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
private fun RowScope.ControlButton(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(MaterialTheme.components.buttonSmall)
            .clip(RoundedCornerShape(MaterialTheme.corners.smallMedium))
            .background(color = containerColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(MaterialTheme.icons.smallMedium)
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xsSmall))

            Text(
                text = label,
                // Standard M3 Role for Buttons
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}