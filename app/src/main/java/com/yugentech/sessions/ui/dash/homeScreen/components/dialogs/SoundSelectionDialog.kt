package com.yugentech.sessions.ui.dash.homeScreen.components.dialogs

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Water
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.alerts.model.BackgroundSound
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.util.models.SoundOption

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SoundSelectionSheet(
    currentSoundId: String?,
    onConfirm: (String?) -> Unit,
    onDismiss: () -> Unit,
    onPreview: (String?) -> Unit,
    onHaptic: () -> Unit
) {
    var selectedOption by remember {
        mutableStateOf(currentSoundId ?: BackgroundSound.NONE.id)
    }
    // Remembers the last real sound so turning sounds back on restores it instead of a blank pick.
    var lastSound by remember {
        mutableStateOf(
            currentSoundId?.takeIf { it != BackgroundSound.NONE.id } ?: BackgroundSound.RAIN.id
        )
    }
    val isSoundOn = selectedOption != BackgroundSound.NONE.id
    val scope = rememberCoroutineScope()

    val soundOptions = listOf(
        SoundOption("Forest", BackgroundSound.FOREST.id, Icons.Rounded.Forest),
        SoundOption("Rain", BackgroundSound.RAIN.id, Icons.Rounded.WaterDrop),
        SoundOption("Brown Noise", BackgroundSound.BROWN_NOISE.id, Icons.Rounded.BlurOn),
        SoundOption("Fireplace", BackgroundSound.FIREPLACE.id, Icons.Rounded.LocalFireDepartment),
        SoundOption("Library", BackgroundSound.LIBRARY.id, Icons.AutoMirrored.Rounded.LibraryBooks),
        SoundOption("Riverside", BackgroundSound.RIVERSIDE.id, Icons.Rounded.Water)
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .padding(vertical = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 6.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                Text(
                    text = "Ambient Sounds",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose a soothing ambience to stay focused during your session.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SoundSwitchRow(
                title = "Turn On Sounds",
                subtitle = "Enable to preview and pick a sound below",
                checked = isSoundOn,
                onCheckedChange = { checked ->
                    onHaptic()
                    if (checked) {
                        selectedOption = lastSound
                        onPreview(lastSound)
                    } else {
                        selectedOption = BackgroundSound.NONE.id
                        onPreview(null)
                    }
                }
            )

            // Sounds are only pickable while sounds are on; while off the grid stays visible
            // (dimmed) so the user can still see which sound comes back when they turn it on.
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
            ) {
                soundOptions.chunked(2).forEach { rowOptions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
                    ) {
                        rowOptions.forEach { soundOption ->
                            SoundToggleCard(
                                soundOption = soundOption,
                                isSelected = lastSound == soundOption.id,
                                enabled = isSoundOn,
                                onClick = {
                                    selectedOption = soundOption.id
                                    lastSound = soundOption.id
                                    onPreview(selectedOption)
                                    onHaptic()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    onHaptic()
                    onConfirm(selectedOption)
                    scope.launch { sheetState.hide() }
                }
            ) {
                Text("Save")
            }
        }
    }
}

// Plain title/subtitle + switch row (mirrors Quill's reader sound sheet), deliberately not the
// settings-screen SettingsSwitchItem, which carries grouped-list card styling.
@Composable
private fun SoundSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                checkedIconColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedIconColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            thumbContent = {
                Icon(
                    imageVector = if (checked) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize)
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SoundToggleCard(
    soundOption: SoundOption,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected && enabled) 1.3f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "icon_scale"
    )

    ToggleButton(
        checked = isSelected,
        onCheckedChange = { onClick() },
        modifier = modifier,
        enabled = enabled,
        shapes = ToggleButtonShapes(
            shape = ToggleButtonDefaults.squareShape,
            pressedShape = ToggleButtonDefaults.pressedShape,
            checkedShape = ToggleButtonDefaults.roundShape
        ),
        colors = ToggleButtonDefaults.toggleButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface,
            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.m),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = soundOption.icon,
                contentDescription = soundOption.label,
                modifier = Modifier.graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))

            Text(
                text = soundOption.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
