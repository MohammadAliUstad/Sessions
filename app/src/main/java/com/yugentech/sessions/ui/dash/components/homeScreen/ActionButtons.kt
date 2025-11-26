package com.yugentech.sessions.ui.dash.components.homeScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@Composable
fun ActionButton(
    isStudying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isStudying)
            MaterialTheme.colorScheme.tertiaryContainer
        else
            MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 400),
        label = "button-container-color"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isStudying)
            MaterialTheme.colorScheme.onTertiaryContainer
        else
            MaterialTheme.colorScheme.onPrimary,
        animationSpec = tween(durationMillis = 400),
        label = "button-content-color"
    )

    FloatingActionButton(
        onClick = onPlayPause,
        modifier = modifier
            .size(MaterialTheme.components.fabSize)
            .padding(MaterialTheme.spacing.xsSmall),
        shape = CircleShape,
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        Icon(
            imageVector = if (isStudying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isStudying) "Pause" else "Start",
            modifier = Modifier.size(MaterialTheme.icons.mediumLarge)
        )
    }
}