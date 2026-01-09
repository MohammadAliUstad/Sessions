package com.yugentech.sessions.ui.dash.components.homeScreen.bottomRow

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    val cornerPercent by animateIntAsState(
        targetValue = if (isStudying) 50 else 30,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "shapeMorph"
    )
    val currentSideShape = RoundedCornerShape(cornerPercent)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Left Action (Sound / Discard)
        AnimatedContent(
            targetState = isSessionActive,
            transitionSpec = { fadeIn(tween(100)) togetherWith fadeOut(tween(100)) },
            label = "LeftButton"
        ) { active ->
            if (active) {
                SecondaryActionButton(
                    icon = Icons.Outlined.Close,
                    label = "Discard",
                    onClick = onStopDiscard,
                    shape = currentSideShape
                )
            } else {
                SecondaryActionButton(
                    icon = Icons.Outlined.GraphicEq,
                    label = "Sound",
                    onClick = onSoundClick,
                    shape = currentSideShape
                )
            }
        }

        // Main Action (Play/Pause)
        ActionButton(
            isStudying = isStudying,
            onPlayPause = onStartStop
        )

        // Right Action (Sets / Finish)
        AnimatedContent(
            targetState = isSessionActive,
            transitionSpec = { fadeIn(tween(100)) togetherWith fadeOut(tween(100)) },
            label = "RightButton"
        ) { active ->
            if (active) {
                SecondaryActionButton(
                    icon = Icons.Outlined.Check,
                    label = "Finish",
                    onClick = onStopSave,
                    isActive = true,
                    shape = currentSideShape
                )
            } else {
                SecondaryActionButton(
                    icon = Icons.Outlined.Layers,
                    label = "Sets",
                    onClick = onSetsClick,
                    shape = currentSideShape
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
    isActive: Boolean = false,
    shape: Shape
) {
    val colors = if (isActive) {
        IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    } else {
        IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(72.dp), // Increased from 64.dp
        shape = shape,
        colors = colors
    ) {
        // Vertical stack for Icon + Label
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), // Slightly increased font size
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}