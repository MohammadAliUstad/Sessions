package com.yugentech.sessions.ui.dash.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.components.common.ToastMessage
import com.yugentech.sessions.ui.dash.components.homeScreen.bottomRow.SessionControlBar
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.SetsSettingsDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.SoundSelectionDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.DurationPickerDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionConfigCard
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionProgressCard
import com.yugentech.sessions.ui.dash.components.homeScreen.middle.ModeTag
import com.yugentech.sessions.ui.dash.components.homeScreen.middle.TimerDisplay
import com.yugentech.sessions.ui.dash.components.homeScreen.topRow.SessionHeader
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.TaskInputDialog
import com.yugentech.sessions.viewModels.HomeViewModel

private enum class ActiveDialog {
    None, Focus, ShortBreak, SetsSettings, Sound, Task
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userId: String
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val status = uiState.status
    val config = uiState.config

    val scrollState = rememberScrollState()
    val view = LocalView.current
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
                // --- 1. HEADER ---
                SessionHeader(
                    isRunning = status.isRunning,
                    sessionTask = config.sessionTask,
                    onTaskClick = { activeDialog = ActiveDialog.Task }
                )

                // --- 2. TIMER DISPLAY ---
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ModeTag(mode = status.currentMode)
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))
                    TimerDisplay(
                        displayTime = (status.currentTime / 1000).toInt(),
                        selectedDuration = (status.totalTime / 1000).toInt(),
                        isStudying = status.isRunning,
                        idleLabel = "Press the play button\nto start."
                    )
                }

                // --- 3. DASHBOARD (Config vs Progress) ---
                AnimatedContent(
                    targetState = status.isRunning,
                    label = "dashboard_swap"
                ) { isRunning ->
                    if (isRunning) {
                        SessionProgressCard(
                            timerMode = status.currentMode,
                            completedSets = status.completedSets,
                            targetSets = config.targetSets,
                            longBreakDurationMillis = config.longBreakDuration
                        )
                    } else {
                        // REVERTED: Now uses the clean 2-button version
                        SessionConfigCard(
                            focusDurationMillis = config.focusDuration,
                            shortBreakDurationMillis = config.shortBreakDuration,
                            onFocusClick = { activeDialog = ActiveDialog.Focus },
                            onShortBreakClick = { activeDialog = ActiveDialog.ShortBreak }
                        )
                    }
                }

                // --- 4. CONTROL BAR ---
                val isSessionActive = status.currentTime != status.totalTime
                SessionControlBar(
                    isStudying = status.isRunning,
                    isSessionActive = isSessionActive,
                    onStartStop = { homeViewModel.toggleTimer(view) },
                    onSoundClick = { activeDialog = ActiveDialog.Sound },
                    onSetsClick = { activeDialog = ActiveDialog.SetsSettings }, // Open new dialog
                    onStopDiscard = { homeViewModel.stopAndDiscardSession(view) },
                    onStopSave = { homeViewModel.stopAndSaveSession(view) }
                )
            }

            ToastMessage(
                message = uiState.errorMessage,
                onDismiss = { homeViewModel.clearError() },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            if (activeDialog != ActiveDialog.None) {
                val closeDialog = { activeDialog = ActiveDialog.None }
                val currentFocus = (config.focusDuration / 60000).toInt()
                val currentShort = (config.shortBreakDuration / 60000).toInt()
                val currentLong = (config.longBreakDuration / 60000).toInt()

                when (activeDialog) {
                    ActiveDialog.Focus -> {
                        DurationPickerDialog(
                            title = "Focus Duration",
                            initialValue = currentFocus,
                            range = 5..120,
                            step = 5,
                            onDismiss = closeDialog,
                            onConfirm = { newMins ->
                                homeViewModel.updateDurations(newMins, currentShort, currentLong)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.ShortBreak -> {
                        DurationPickerDialog(
                            title = "Short Break",
                            initialValue = currentShort,
                            range = 1..30,
                            step = 1,
                            onDismiss = closeDialog,
                            onConfirm = { newMins ->
                                homeViewModel.updateDurations(currentFocus, newMins, currentLong)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.SetsSettings -> {
                        SetsSettingsDialog(
                            currentSets = config.targetSets,
                            currentLongBreak = currentLong,
                            onDismiss = closeDialog,
                            onConfirm = { newSets, newLongBreak ->
                                homeViewModel.updateTargetSets(newSets)
                                homeViewModel.updateDurations(
                                    currentFocus,
                                    currentShort,
                                    newLongBreak
                                )
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.Sound -> {
                        SoundSelectionDialog(
                            currentSoundId = config.soundId,
                            onDismiss = closeDialog,
                            onConfirm = { TODO() }
                        )
                    }

                    ActiveDialog.Task -> {
                        TaskInputDialog(
                            currentTask = config.sessionTask,
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