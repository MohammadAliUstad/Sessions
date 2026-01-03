package com.yugentech.sessions.ui.dash.components.homeScreen.bottomRow

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SessionControlBar(
    isStudying: Boolean,
    isSessionActive: Boolean,
    onStartStop: () -> Unit,
    onSoundClick: () -> Unit,
    onSetsClick: () -> Unit,
    onStopDiscard: () -> Unit,
    onStopSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AnimatedContent(
            targetState = isSessionActive,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
            label = "LeftButton"
        ) { active ->
            if (active) {
                SecondaryActionButton(
                    icon = Icons.Outlined.Close,
                    label = "Discard",
                    onClick = onStopDiscard
                )
            } else {
                SecondaryActionButton(
                    icon = Icons.Outlined.GraphicEq,
                    label = "Sound",
                    onClick = onSoundClick
                )
            }
        }

        ActionButton(
            isStudying = isStudying,
            onPlayPause = onStartStop
        )

        AnimatedContent(
            targetState = isSessionActive,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
            label = "RightButton"
        ) { active ->
            if (active) {
                SecondaryActionButton(
                    icon = Icons.Outlined.Check,
                    label = "Finish",
                    onClick = onStopSave,
                    isActive = true
                )
            } else {
                SecondaryActionButton(
                    icon = Icons.Outlined.Layers,
                    label = "Sets",
                    onClick = onSetsClick
                )
            }
        }
    }
}

@Composable
fun SecondaryActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false
) {

    val containerColor =
        if (isActive)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant

    val contentColor =
        if (isActive)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(50.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Icon(
                icon,
                contentDescription = label
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}