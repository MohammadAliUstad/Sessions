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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.timer.TimerViewModel
import com.yugentech.sessions.ui.dash.components.common.ToastMessage
import com.yugentech.sessions.ui.dash.components.homeScreen.bottomRow.SessionControlBar
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.ActiveDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.DurationPickerDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.SetsSettingsDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.SoundSelectionDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.TaskInputDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionConfigCard
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionProgressCard
import com.yugentech.sessions.ui.dash.components.homeScreen.middle.ModeTag
import com.yugentech.sessions.ui.dash.components.homeScreen.middle.TimerDisplay
import com.yugentech.sessions.ui.dash.components.homeScreen.topRow.SessionHeader
import com.yugentech.sessions.viewModels.HomeViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    timerViewModel: TimerViewModel,
    userId: String
) {
    val uiState by timerViewModel.uiState.collectAsStateWithLifecycle()
    val sessionStatus = uiState.status
    val sessionConfig = uiState.config

    val isSessionActive = sessionStatus.currentTime != sessionStatus.totalTime

    val view = LocalView.current
    val scrollState = rememberScrollState()
    var activeDialog by remember { mutableStateOf(ActiveDialog.None) }

    LaunchedEffect(userId) {
        timerViewModel.setUserId(userId)
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
                    // CRITICAL FIX: Asymmetric padding to clear the bottom nav bar
                    .padding(
                        top = MaterialTheme.spacing.m,
                        bottom = 112.dp // Ensure content scrolls up past the floating buttons
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // 1. Header (Task Name)
                SessionHeader(
                    isRunning = sessionStatus.isRunning,
                    sessionTask = sessionConfig.sessionTask,
                    onTaskClick = { activeDialog = ActiveDialog.Task }
                )

                // 2. Timer Display Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ModeTag(
                        mode = sessionStatus.currentMode
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))

                    TimerDisplay(
                        displayTime = (sessionStatus.currentTime / 1000).toInt(),
                        selectedDuration = (sessionStatus.totalTime / 1000).toInt(),
                        isStudying = sessionStatus.isRunning,
                        idleLabel = "Press the play button\nto start."
                    )
                }

                // 3. Progress / Config Card
                AnimatedContent(
                    targetState = sessionStatus.isRunning,
                    label = "dashboard_swap"
                ) { isRunning ->
                    if (isRunning) {
                        SessionProgressCard(
                            timerMode = sessionStatus.currentMode,
                            completedSets = sessionStatus.completedSets,
                            targetSets = sessionConfig.targetSets,
                            longBreakDurationMillis = sessionConfig.longBreakDuration
                        )
                    } else {
                        SessionConfigCard(
                            focusDurationMillis = sessionConfig.focusDuration,
                            shortBreakDurationMillis = sessionConfig.shortBreakDuration,
                            onFocusClick = { activeDialog = ActiveDialog.Focus },
                            onShortBreakClick = { activeDialog = ActiveDialog.ShortBreak }
                        )
                    }
                }

                // 4. Bottom Controls
                SessionControlBar(
                    isStudying = sessionStatus.isRunning,
                    isSessionActive = isSessionActive,
                    onStartStop = { timerViewModel.toggleTimer(view) },
                    onSoundClick = { activeDialog = ActiveDialog.Sound },
                    onSetsClick = { activeDialog = ActiveDialog.SetsSettings },
                    onStopDiscard = { timerViewModel.stopAndDiscardSession(view) },
                    onStopSave = { timerViewModel.stopAndSaveSession(view) }
                )
            }

            // Dialog Management
            if (activeDialog != ActiveDialog.None) {
                val closeDialog = { activeDialog = ActiveDialog.None }
                val currentFocus = (sessionConfig.focusDuration / 60000).toInt()
                val currentShort = (sessionConfig.shortBreakDuration / 60000).toInt()
                val currentLong = (sessionConfig.longBreakDuration / 60000).toInt()

                when (activeDialog) {
                    ActiveDialog.Focus -> {
                        DurationPickerDialog(
                            title = "Focus Duration",
                            initialValue = currentFocus,
                            range = 1..60,
                            step = 1,
                            onDismiss = closeDialog,
                            onConfirm = { newMins ->
                                timerViewModel.updateDurations(newMins, currentShort, currentLong)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.ShortBreak -> {
                        DurationPickerDialog(
                            title = "Short Break",
                            initialValue = currentShort,
                            range = 1..10,
                            step = 1,
                            onDismiss = closeDialog,
                            onConfirm = { newMins ->
                                timerViewModel.updateDurations(currentFocus, newMins, currentLong)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.SetsSettings -> {
                        SetsSettingsDialog(
                            currentSets = sessionConfig.targetSets,
                            currentLongBreak = currentLong,
                            onDismiss = closeDialog,
                            onConfirm = { newSets, newLongBreak ->
                                timerViewModel.updateTargetSets(newSets)
                                timerViewModel.updateDurations(
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
                            currentSoundId = sessionConfig.soundId,
                            onDismiss = closeDialog,
                            onConfirm = { homeViewModel.clearError() }
                        )
                    }

                    ActiveDialog.Task -> {
                        TaskInputDialog(
                            currentTask = sessionConfig.sessionTask,
                            onDismiss = closeDialog,
                            onConfirm = { newTask ->
                                timerViewModel.updateSessionTask(newTask)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.None -> Unit
                }

                ToastMessage(
                    message = uiState.errorMessage,
                    onDismiss = { homeViewModel.clearError() },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}