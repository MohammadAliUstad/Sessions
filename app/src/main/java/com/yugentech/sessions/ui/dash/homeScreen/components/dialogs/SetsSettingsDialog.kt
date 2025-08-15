package com.yugentech.sessions.ui.dash.homeScreen.components.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetsSettingsSheet(
    currentSets: Int,
    currentLongBreak: Int,
    currentSetsPerLongBreak: Int,
    currentLongBreakEnabled: Boolean,
    onDismiss: (Int, Int, Int, Boolean) -> Unit,
    onHaptic: () -> Unit
) {
    var sets by remember { mutableIntStateOf(currentSets) }
    var longBreak by remember { mutableFloatStateOf(currentLongBreak.toFloat()) }
    var setsPerLongBreak by remember {
        mutableIntStateOf(currentSetsPerLongBreak.coerceIn(1, (currentSets - 1).coerceAtLeast(1)))
    }
    var longBreakEnabled by remember { mutableStateOf(currentLongBreakEnabled) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val cornerRadius by animateDpAsState(
        targetValue = if (sheetState.targetValue == SheetValue.Expanded) 0.dp else 28.dp,
        label = "sheetCornerRadius"
    )

    ModalBottomSheet(
        onDismissRequest = { onDismiss(sets, longBreak.roundToInt(), setsPerLongBreak, longBreakEnabled) },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
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
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                Text(
                    text = "Session Goals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose number of focus sets and break intervals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SettingSliderRow(
                label = "Target Sets",
                valueDisplay = "$sets",
                icon = Icons.Outlined.Flag,
                value = sets.toFloat(),
                valueRange = 1f..12f,
                steps = 10,
                onValueChange = {
                    val newSets = it.roundToInt()
                    if (newSets != sets) onHaptic()
                    sets = newSets
                    setsPerLongBreak = setsPerLongBreak.coerceIn(1, (sets - 1).coerceAtLeast(1))
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessMediumLow,
                            dampingRatio = Spring.DampingRatioNoBouncy
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.icons.mediumSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))
                        Text(
                            text = "Long Break",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Switch(
                        checked = longBreakEnabled,
                        onCheckedChange = {
                            onHaptic()
                            longBreakEnabled = it
                        },
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
                                imageVector = if (longBreakEnabled) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize)
                            )
                        }
                    )
                }

                AnimatedVisibility(
                    visible = longBreakEnabled,
                    enter = fadeIn(tween(250)),
                    exit = fadeOut(tween(200))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        SettingSliderRow(
                            label = "Duration",
                            valueDisplay = "${longBreak.roundToInt()}m",
                            icon = Icons.Outlined.HourglassEmpty,
                            value = longBreak,
                            valueRange = 15f..45f,
                            steps = 5,
                            onValueChange = {
                                val newLongBreak = (it / 5).roundToInt() * 5f
                                if (newLongBreak != longBreak) onHaptic()
                                longBreak = newLongBreak
                            }
                        )

                        LongBreakPatternControl(
                            sets = sets,
                            setsPerLongBreak = setsPerLongBreak,
                            onValueChange = { setsPerLongBreak = it },
                            onHaptic = onHaptic
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LongBreakPatternControl(
    sets: Int,
    setsPerLongBreak: Int,
    onValueChange: (Int) -> Unit,
    onHaptic: () -> Unit
) {
    val maxValue = (sets - 1).coerceAtLeast(1)
    val smSize = MaterialTheme.spacing.sm
    val xsSmallSize = MaterialTheme.spacing.xsSmall
    val errorColor = MaterialTheme.colorScheme.error
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = MaterialTheme.spacing.s)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Repeat,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.icons.mediumSmall),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))
                Text(
                    text = "Interval",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.basicMarquee()
                )
            }
            Text(
                text = if (setsPerLongBreak == 1) "1 set" else "$setsPerLongBreak sets",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }

        AnimatedContent(
            targetState = sets,
            transitionSpec = {
                fadeIn(tween(durationMillis = 220)) togetherWith fadeOut(tween(durationMillis = 160))
            },
            label = "stripSetsAnimation"
        ) { targetSets ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                for (i in 1..targetSets) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(MaterialTheme.spacing.xsSmall)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                    )
                    if (i < targetSets) {
                        val isLongBreak = i % setsPerLongBreak == 0

                        val breakSize by animateDpAsState(
                            targetValue = if (isLongBreak) smSize else xsSmallSize,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "breakSize_$i"
                        )
                        val breakColor by animateColorAsState(
                            targetValue = if (isLongBreak) errorColor else tertiaryColor.copy(alpha = 0.3f),
                            animationSpec = tween(durationMillis = 250),
                            label = "breakColor_$i"
                        )

                        Box(
                            modifier = Modifier.size(smSize),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(breakSize)
                                    .clip(CircleShape)
                                    .background(breakColor)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(
                onClick = {
                    onHaptic()
                    onValueChange((setsPerLongBreak - 1).coerceAtLeast(1))
                },
                enabled = setsPerLongBreak > 1
            ) {
                Icon(imageVector = Icons.Rounded.Remove, contentDescription = "Decrease")
            }
            Text(
                text = if (setsPerLongBreak == 1) "every set" else "every $setsPerLongBreak sets",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FilledTonalIconButton(
                onClick = {
                    onHaptic()
                    onValueChange((setsPerLongBreak + 1).coerceAtMost(maxValue))
                },
                enabled = setsPerLongBreak < maxValue
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Increase")
            }
        }
    }
}

@Composable
private fun SettingSliderRow(
    label: String,
    valueDisplay: String,
    icon: ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = MaterialTheme.spacing.s)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.icons.mediumSmall),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.basicMarquee()
                )
            }
            Text(
                text = valueDisplay,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
