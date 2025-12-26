package com.yugentech.sessions.ui.dash.components.homeScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    // Using Surface instead of FloatingActionButton to remove elevation/shadow
    Surface(
        onClick = onPlayPause,
        modifier = modifier
            .size(MaterialTheme.components.fabSize) // Ensure this token exists or use e.g. 56.dp
            .padding(MaterialTheme.spacing.xsSmall), // Optional padding from parent
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor
    ) {
        // Box ensures the icon is perfectly centered
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isStudying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isStudying) "Pause" else "Start",
                modifier = Modifier.size(MaterialTheme.icons.mediumLarge) // Ensure token exists or use e.g. 32.dp
            )
        }
    }
}