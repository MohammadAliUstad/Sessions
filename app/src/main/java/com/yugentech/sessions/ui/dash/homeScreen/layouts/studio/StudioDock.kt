package com.yugentech.sessions.ui.dash.homeScreen.layouts.studio

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.ui.dash.mainScreen.components.pressClickable

private val DockShape = RoundedCornerShape(36.dp)
private val SideShape = RoundedCornerShape(26.dp)

/**
 * The controls as one floating dock: the main action as a wide pill in the middle that says what
 * it will do (Start focus, Pause, Resume), with a smaller action either side. Before a session
 * those are Sound and Sets; during one they become Discard and Finish.
 */
@Composable
fun StudioDock(
    isRunning: Boolean,
    isSessionActive: Boolean,
    onStartStop: () -> Unit,
    onSound: () -> Unit,
    onSets: () -> Unit,
    onDiscard: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(scheme.surfaceContainerHigh, DockShape)
            .border(1.dp, scheme.outlineVariant.copy(alpha = 0.4f), DockShape)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // The same colours as Refined's control buttons: neutral sides before a session; during
        // one, Discard stays neutral (a step brighter) and Finish takes the primary container
        SideAction(
            icon = if (isSessionActive) Icons.Rounded.Close else Icons.Rounded.GraphicEq,
            label = if (isSessionActive) stringResource(R.string.action_discard) else stringResource(R.string.action_sound),
            container = scheme.surfaceContainerHighest,
            content = if (isSessionActive) scheme.onSurface else scheme.onSurfaceVariant,
            onClick = if (isSessionActive) onDiscard else onSound
        )

        // Solid primary when it starts or resumes; the soft secondary container while running
        MainAction(
            isRunning = isRunning,
            isSessionActive = isSessionActive,
            container = if (isRunning) scheme.secondaryContainer else scheme.primary,
            content = if (isRunning) scheme.onSecondaryContainer else scheme.onPrimary,
            onClick = onStartStop,
            modifier = Modifier.weight(1f)
        )

        SideAction(
            icon = if (isSessionActive) Icons.Rounded.Check else Icons.Rounded.Layers,
            label = if (isSessionActive) stringResource(R.string.action_finish) else stringResource(R.string.action_sets),
            container = if (isSessionActive) scheme.primaryContainer else scheme.surfaceContainerHighest,
            content = if (isSessionActive) scheme.onPrimaryContainer else scheme.onSurfaceVariant,
            onClick = if (isSessionActive) onFinish else onSets
        )
    }
}

@Composable
private fun MainAction(
    isRunning: Boolean,
    isSessionActive: Boolean,
    container: Color,
    content: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Eases between solid and soft as the timer starts and stops, rather than switching
    val fill by animateColorAsState(container, tween(450), label = "dockMainContainer")
    val ink by animateColorAsState(content, tween(450), label = "dockMainContent")
    val label = when {
        isRunning -> stringResource(R.string.action_pause)
        isSessionActive -> stringResource(R.string.action_resume)
        else -> stringResource(R.string.action_start_focus)
    }

    Row(
        modifier = modifier
            .height(64.dp)
            .pressClickable(shape = DockShape, onClickLabel = label, onClick = onClick)
            .background(fill, DockShape)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AnimatedContent(
            targetState = isRunning,
            transitionSpec = {
                (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.6f))
                    .togetherWith(fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.6f))
            },
            label = "dockMainIcon"
        ) { running ->
            Icon(
                imageVector = if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = ink,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        AnimatedContent(
            targetState = label,
            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
            label = "dockMainLabel"
        ) { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = ink,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SideAction(
    icon: ImageVector,
    label: String,
    container: Color,
    content: Color,
    onClick: () -> Unit
) {
    // Both ease across when a session starts or ends, rather than switching
    val fill by animateColorAsState(container, tween(450), label = "dockSideContainer")
    val ink by animateColorAsState(content, tween(450), label = "dockSideContent")
    Column(
        modifier = Modifier
            .size(width = 72.dp, height = 64.dp)
            .pressClickable(shape = SideShape, onClickLabel = label, onClick = onClick)
            .background(fill, SideShape),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedContent(
            targetState = icon to label,
            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
            label = "dockSide"
        ) { (i, l) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = i, contentDescription = null, tint = ink, modifier = Modifier.size(22.dp))
                Text(
                    text = l,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = ink,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
