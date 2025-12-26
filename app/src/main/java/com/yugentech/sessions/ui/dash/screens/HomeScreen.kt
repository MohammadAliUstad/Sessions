package com.yugentech.sessions.ui.dash.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.timer.TimerConfig
import com.yugentech.sessions.timer.TimerMode
import com.yugentech.sessions.ui.dash.components.common.ToastMessage
import com.yugentech.sessions.ui.dash.components.homeScreen.ActionButton
import com.yugentech.sessions.ui.dash.components.homeScreen.SessionHeader
import com.yugentech.sessions.ui.dash.components.homeScreen.TimerDisplay
import com.yugentech.sessions.viewModels.HomeViewModel

// Added 'Task' to the enum
private enum class ActiveDialog {
    None, Focus, ShortBreak, Sets, Sound, Task
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userId: String
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val view = LocalView.current

    // Local state to manage which dialog is visible
    var activeDialog by remember { mutableStateOf(ActiveDialog.None) }

    LaunchedEffect(userId) {
        homeViewModel.setUserId(userId)
        homeViewModel.fetchUserOnce(userId)
        homeViewModel.fetchSessionsOnce(userId)
        homeViewModel.syncPendingSessions(userId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(vertical = MaterialTheme.spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // 1. Header (Task Selector)
                SessionHeader(
                    isRunning = uiState.isRunning,
                    sessionTask = uiState.sessionTask,
                    onTaskClick = { activeDialog = ActiveDialog.Task }
                )

                // 2. The Main Timer Area
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ModeTag(mode = uiState.timerMode)

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))

                    TimerDisplay(
                        displayTime = uiState.currentTime,
                        selectedDuration = uiState.selectedDuration,
                        isStudying = uiState.isRunning
                    )
                }

                // 3. The Dashboard (Config vs Progress)
                AnimatedContent(
                    targetState = uiState.isRunning,
                    label = "dashboard_swap"
                ) { isRunning ->
                    if (isRunning) {
                        SessionProgressCard(
                            timerMode = uiState.timerMode,
                            completedRounds = uiState.completedRounds,
                            totalRounds = uiState.timerConfig.roundsBeforeLongBreak,
                            longBreakDuration = uiState.timerConfig.longBreakDuration
                        )
                    } else {
                        SessionConfigCard(
                            config = uiState.timerConfig,
                            onFocusClick = { activeDialog = ActiveDialog.Focus },
                            onShortBreakClick = { activeDialog = ActiveDialog.ShortBreak }
                        )
                    }
                }

                // 4. The Control Row
                SessionControlBar(
                    isStudying = uiState.isRunning,
                    onStartStop = { homeViewModel.toggleTimer(view) },
                    onSoundClick = { activeDialog = ActiveDialog.Sound },
                    onSetsClick = { activeDialog = ActiveDialog.Sets },
                    onStopDiscard = { homeViewModel.stopAndDiscardSession(view) },
                    onStopSave = { homeViewModel(view) }
                )
            }

            // Error Toasts
            ToastMessage(
                message = uiState.errorMessage,
                onDismiss = { homeViewModel.clearError() },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // --- DIALOG LOGIC ---
            if (activeDialog != ActiveDialog.None) {
                val config = uiState.timerConfig
                val closeDialog = { activeDialog = ActiveDialog.None }

                when (activeDialog) {
                    ActiveDialog.Focus -> {
                        DurationPickerDialog(
                            title = "Focus Duration",
                            initialValue = (config.focusDuration / 60000).toInt(),
                            range = 5..120,
                            step = 5,
                            onDismiss = closeDialog,
                            onConfirm = { mins ->
                                homeViewModel.updateFullConfig(config.copy(focusDuration = mins * 60000L))
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.ShortBreak -> {
                        DurationPickerDialog(
                            title = "Short Break",
                            initialValue = (config.shortBreakDuration / 60000).toInt(),
                            range = 1..30,
                            step = 1,
                            onDismiss = closeDialog,
                            onConfirm = { mins ->
                                homeViewModel.updateFullConfig(config.copy(shortBreakDuration = mins * 60000L))
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.Sets -> {
                        SetsConfigDialog(
                            currentRounds = config.roundsBeforeLongBreak,
                            currentLongBreak = (config.longBreakDuration / 60000).toInt(),
                            onDismiss = closeDialog,
                            onConfirm = { rounds, longBreakMins ->
                                homeViewModel.updateFullConfig(
                                    config.copy(
                                        roundsBeforeLongBreak = rounds,
                                        longBreakDuration = longBreakMins * 60000L
                                    )
                                )
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.Sound -> {
                        SoundSelectionDialog(
                            currentSoundId = config.backgroundSoundId,
                            onDismiss = closeDialog,
                            onConfirm = { soundId ->
                                homeViewModel.updateFullConfig(config.copy(backgroundSoundId = soundId))
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.Task -> {
                        TaskInputDialog(
                            currentTask = uiState.sessionTask,
                            onDismiss = closeDialog,
                            onConfirm = { newTask ->
                                homeViewModel.updateSessionTask(newTask)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.None -> Unit
                }
            }
        }
    }
}

// ==========================================
//               NEW COMPONENTS
// ==========================================

@Composable
fun TaskInputDialog(
    currentTask: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentTask) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Task") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("What are you working on?") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }) {
                Text("Set")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Include other helper components (SessionProgressCard, SessionConfigCard, etc.)
// if they are not already in separate files.
// Based on our flow, they are likely in this file or imported.
// Make sure ModeTag, TimerDisplay, SessionControlBar etc are available.

// ==========================================
//               COMPONENTS
// ==========================================

// --- 1. Mode Tag Indicator ---
@Composable
fun ModeTag(mode: TimerMode) {
    val (text, color) = when (mode) {
        TimerMode.Focus -> "Focus Time" to MaterialTheme.colorScheme.primary
        TimerMode.ShortBreak -> "Short Break" to MaterialTheme.colorScheme.tertiary
        TimerMode.LongBreak -> "Long Break" to MaterialTheme.colorScheme.secondary
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// --- 2. Session Progress Card (Active State) ---
// --- 2. Session ProgressCard (Active State) ---
@Composable
fun SessionProgressCard(
    timerMode: TimerMode,
    completedRounds: Int,
    totalRounds: Int,
    longBreakDuration: Long
) {
    val currentSet = completedRounds + 1
    val setsLeft = (totalRounds - currentSet).coerceAtLeast(0)

    val message = when {
        timerMode == TimerMode.LongBreak -> "Enjoy your downtime"
        setsLeft == 0 -> "Long break coming up!"
        else -> "$setsLeft more set${if (setsLeft > 1) "s" else ""} until Long Break"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp) // FIXED HEIGHT to match ConfigCard
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            // distinct color for "Active" state
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp) // Consistent padding
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Big Stats
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = if (timerMode == TimerMode.LongBreak) "Status" else "Set Progress",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (timerMode == TimerMode.LongBreak) "Long Break" else "$currentSet / $totalRounds",
                    style = MaterialTheme.typography.displaySmall, // Big and bold
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Right Side: Info Badge
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = "${longBreakDuration / 60000}m Break",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// --- 3. Config Card (Idle State) ---
@Composable
fun SessionConfigCard(
    config: TimerConfig,
    onFocusClick: () -> Unit,
    onShortBreakClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp) // FIXED HEIGHT to match ProgressCard
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            // More neutral background so the colored buttons inside pop
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp) // Slightly tighter padding to allow big buttons
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Focus Button - Uses PRIMARY Color
            ConfigItem(
                label = "Focus Duration",
                value = "${config.focusDuration / 60000}",
                onClick = onFocusClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            // Short Break Button - Uses TERTIARY Color
            ConfigItem(
                label = "Short Break",
                value = "${config.shortBreakDuration / 60000}",
                onClick = onShortBreakClick,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ConfigItem(
    label: String,
    value: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(), // Fill the height of the parent row
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        contentColor = contentColor
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = contentColor
            )

            Text(
                text = "min",
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.7f),
                modifier = Modifier.offset(y = (-4).dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor.copy(alpha = 0.9f),
                maxLines = 1
            )
        }
    }
}

// --- 4. Control Bar ---
// In HomeScreen.kt (or your components file)

@Composable
fun SessionControlBar(
    isStudying: Boolean,
    onStartStop: () -> Unit,
    onSoundClick: () -> Unit,
    onSetsClick: () -> Unit,
    onStopDiscard: () -> Unit, // New: Discard Session
    onStopSave: () -> Unit     // New: Save Session
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Adjusted padding
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly // Even spacing for 3 items
    ) {
        // --- LEFT BUTTON (Sound <-> Discard) ---
        AnimatedContent(
            targetState = isStudying,
            label = "left_button_swap",
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            }
        ) { studying ->
            if (studying) {
                // RUNNING: Show Stop (Discard)
                SecondaryActionButton(
                    icon = Icons.Default.Close,
                    label = "Discard",
                    onClick = onStopDiscard,
                    enabled = true
                )
            } else {
                // IDLE: Show Sound
                SecondaryActionButton(
                    icon = Icons.Outlined.GraphicEq,
                    label = "Sound",
                    onClick = onSoundClick,
                    enabled = true
                )
            }
        }

        // --- CENTER BUTTON (Play/Pause) ---
        // This stays physically in the middle
        ActionButton(
            isStudying = isStudying,
            onPlayPause = onStartStop
        )

        // --- RIGHT BUTTON (Sets <-> Save) ---
        AnimatedContent(
            targetState = isStudying,
            label = "right_button_swap",
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            }
        ) { studying ->
            if (studying) {
                // RUNNING: Show Save
                SecondaryActionButton(
                    icon = Icons.Default.Check,
                    label = "Finish",
                    onClick = onStopSave,
                    enabled = true
                )
            } else {
                // IDLE: Show Sets
                SecondaryActionButton(
                    icon = Icons.Outlined.Tune,
                    label = "Sets",
                    onClick = onSetsClick,
                    enabled = true
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
    enabled: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(50.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(icon, contentDescription = label)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else Color.LightGray
        )
    }
}

// ==========================================
//                 DIALOGS
// ==========================================

@Composable
fun DurationPickerDialog(
    title: String,
    initialValue: Int,
    range: IntRange,
    step: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var currentValue by remember { mutableIntStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set duration in minutes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                BigTimeStepper(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    range = range,
                    step = step
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(currentValue) }) { Text("Set Time") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SetsConfigDialog(
    currentRounds: Int,
    currentLongBreak: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var rounds by remember { mutableIntStateOf(currentRounds) }
    var longBreak by remember { mutableIntStateOf(currentLongBreak) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Long Break Settings") },
        text = {
            Column {
                Text(
                    "How many sets before a long break?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sets", style = MaterialTheme.typography.titleMedium)
                    SmallStepper(value = rounds, onValueChange = { rounds = it }, range = 1..10)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Long Break Duration",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Duration (m)", style = MaterialTheme.typography.titleMedium)
                    SmallStepper(
                        value = longBreak,
                        onValueChange = { longBreak = it },
                        range = 5..60,
                        step = 5
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(rounds, longBreak) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SoundSelectionDialog(
    currentSoundId: String?,
    onDismiss: () -> Unit,
    onConfirm: (String?) -> Unit
) {
    val options =
        listOf("None" to null, "Rain" to "rain", "White Noise" to "white_noise", "Lofi" to "lofi")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Background Sound") },
        text = {
            Column {
                options.forEach { (label, id) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfirm(id) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentSoundId == id),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

// ==========================================
//               STEPPERS
// ==========================================

@Composable
fun BigTimeStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    step: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { if (value - step >= range.first) onValueChange(value - step) },
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease")
        }

        Text(
            text = "$value",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        IconButton(
            onClick = { if (value + step <= range.last) onValueChange(value + step) },
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase")
        }
    }
}

@Composable
fun SmallStepper(value: Int, onValueChange: (Int) -> Unit, range: IntRange, step: Int = 1) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { if (value - step >= range.first) onValueChange(value - step) },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .size(32.dp)
        ) { Icon(Icons.Default.Remove, "Decrease", modifier = Modifier.size(16.dp)) }

        Text(
            text = "$value",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(
            onClick = { if (value + step <= range.last) onValueChange(value + step) },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .size(32.dp)
        ) { Icon(Icons.Default.Add, "Increase", modifier = Modifier.size(16.dp)) }
    }
}