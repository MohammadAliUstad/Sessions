package com.yugentech.sessions.ui.dash.components.homeScreen.bottomRow

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
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
    val interactionSources = remember { List(3) { MutableInteractionSource() } }

    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides MaterialTheme.spacing.none) {
        ButtonGroup(
            overflowIndicator = { state ->
                ButtonGroupDefaults.OverflowIndicator(
                    state,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(),
                    modifier = Modifier.size(MaterialTheme.components.controlBarItemSize)
                )
            },
            modifier = modifier.padding(
                horizontal = MaterialTheme.spacing.xs,
                vertical = MaterialTheme.spacing.s
            )
        ) {
            customItem(
                {
                    FilledTonalIconButton(
                        onClick = {
                            if (isSessionActive) onStopDiscard() else onSoundClick()
                        },
                        colors = getLeftButtonColors(isSessionActive),
                        shapes = IconButtonDefaults.shapes(),
                        interactionSource = interactionSources[0],
                        modifier = Modifier
                            .size(MaterialTheme.components.controlBarItemSize)
                            .animateWidth(interactionSources[0])
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                if (isSessionActive) Icons.Outlined.Close else Icons.Outlined.GraphicEq,
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.icons.medium)
                            )
                            Text(
                                if (isSessionActive) "Discard" else "Sound",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = MaterialTheme.spacing.xxs)
                            )
                        }
                    }
                },
                { state ->
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                if (isSessionActive) Icons.Outlined.Close else Icons.Outlined.GraphicEq,
                                contentDescription = null
                            )
                        },
                        text = { Text(if (isSessionActive) "Discard" else "Sound") },
                        onClick = {
                            if (isSessionActive) onStopDiscard() else onSoundClick()
                            state.dismiss()
                        }
                    )
                }
            )

            customItem(
                {
                    FilledIconToggleButton(
                        onCheckedChange = { onStartStop() },
                        checked = isStudying,
                        colors = IconButtonDefaults.filledIconToggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            checkedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        shapes = IconToggleButtonShapes(
                            shape = CircleShape,
                            pressedShape = CircleShape,
                            checkedShape = RoundedCornerShape(MaterialTheme.corners.medium)
                        ),
                        interactionSource = interactionSources[1],
                        modifier = Modifier
                            .size(
                                width = MaterialTheme.components.controlBarItemWidthWide,
                                height = MaterialTheme.components.controlBarItemSize
                            )
                            .animateWidth(interactionSources[1])
                    ) {
                        Icon(
                            if (isStudying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.icons.xxl)
                        )
                    }
                },
                { state ->
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                if (isStudying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = null
                            )
                        },
                        text = { Text(if (isStudying) "Pause" else "Play") },
                        onClick = {
                            onStartStop()
                            state.dismiss()
                        }
                    )
                }
            )

            customItem(
                {
                    FilledTonalIconButton(
                        onClick = {
                            if (isSessionActive) onStopSave() else onSetsClick()
                        },
                        colors = getRightButtonColors(isSessionActive),
                        shapes = IconButtonDefaults.shapes(),
                        interactionSource = interactionSources[2],
                        modifier = Modifier
                            .size(MaterialTheme.components.controlBarItemSize)
                            .animateWidth(interactionSources[2])
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                if (isSessionActive) Icons.Filled.Check else Icons.Filled.Layers,
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.icons.medium)
                            )
                            Text(
                                if (isSessionActive) "Finish" else "Sets",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = MaterialTheme.spacing.xxs)
                            )
                        }
                    }
                },
                { state ->
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                if (isSessionActive) Icons.Filled.Check else Icons.Filled.Layers,
                                contentDescription = null
                            )
                        },
                        text = { Text(if (isSessionActive) "Finish" else "Sets") },
                        onClick = {
                            if (isSessionActive) onStopSave() else onSetsClick()
                            state.dismiss()
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun getLeftButtonColors(isSessionActive: Boolean) =
    if (isSessionActive) {
        IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    } else {
        IconButtonDefaults.filledTonalIconButtonColors()
    }

@Composable
private fun getRightButtonColors(isSessionActive: Boolean) =
    if (isSessionActive) {
        IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    } else {
        IconButtonDefaults.filledTonalIconButtonColors()
    }