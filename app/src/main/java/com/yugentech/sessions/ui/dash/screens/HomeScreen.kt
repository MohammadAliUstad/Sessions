package com.yugentech.sessions.ui.dash.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
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
    val sessionConfig by timerViewModel.sessionConfig.collectAsStateWithLifecycle()
    val sessionStatus by timerViewModel.sessionStatus.collectAsStateWithLifecycle()
    val errorMessage by timerViewModel.errorMessage.collectAsStateWithLifecycle()

    val dashboardState by timerViewModel.dashboardState.collectAsStateWithLifecycle()

    val isSessionActive = !sessionStatus.isIdle

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
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val screenHeight = maxHeight

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .heightIn(min = screenHeight)
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SessionHeader(
                    isRunning = sessionStatus.isRunning,
                    sessionTask = sessionConfig.sessionTask,
                    onTaskClick = { activeDialog = ActiveDialog.Task }
                )

                TimerDisplay(
                    displayTime = sessionStatus.currentTime.toInt(),
                    selectedDuration = sessionStatus.totalTime.toInt(),
                    isStudying = sessionStatus.isRunning,
                    currentMode = sessionStatus.currentMode,
                )

                AnimatedContent(
                    targetState = isSessionActive,
                    label = "dashboard_swap"
                ) { showProgress ->
                    if (showProgress) {
                        SessionProgressCard(
                            state = dashboardState,
                            targetSets = sessionConfig.targetSets
                        )
                    } else {
                        SessionConfigCard(
                            focusDurationMinutes = sessionConfig.focusDurationMinutes,
                            shortBreakDurationMinutes = sessionConfig.shortBreakDurationMinutes,
                            onFocusClick = { activeDialog = ActiveDialog.Focus },
                            onShortBreakClick = { activeDialog = ActiveDialog.ShortBreak }
                        )
                    }
                }

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

            if (activeDialog != ActiveDialog.None) {
                val closeDialog = { activeDialog = ActiveDialog.None }

                val currentFocus = sessionConfig.focusDurationMinutes
                val currentShort = sessionConfig.shortBreakDurationMinutes
                val currentLong = sessionConfig.longBreakDurationMinutes

                when (activeDialog) {
                    ActiveDialog.Focus -> {
                        DurationPickerDialog(
                            title = "Focus Duration",
                            description = "Choose how long you want to focus before taking a break.",
                            initialValue = currentFocus,
                            range = 25..60,
                            step = 5,
                            onDismiss = closeDialog,
                            onConfirm = { newMins ->
                                timerViewModel.updateFocusDuration(newMins)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.ShortBreak -> {
                        DurationPickerDialog(
                            title = "Short Break",
                            description = "Choose the duration of your break between sessions.",
                            initialValue = currentShort,
                            range = 5..15,
                            step = 1,
                            onDismiss = closeDialog,
                            onConfirm = { newMins ->
                                timerViewModel.updateShortBreakDuration(newMins)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.SetsSettings -> {
                        SetsSettingsDialog(
                            currentSets = sessionConfig.targetSets,
                            currentLongBreak = currentLong,
                            focusDuration = currentFocus,
                            setsPerLongBreak = sessionConfig.setsPerLongBreak,
                            onDismiss = closeDialog,
                            onConfirm = { newSets, newLongBreak ->
                                timerViewModel.updateTargetSets(newSets)
                                timerViewModel.updateLongBreakDuration(newLongBreak)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.Sound -> {
                        SoundSelectionDialog(
                            currentSoundId = sessionConfig.activeBackgroundSoundId,
                            onPreview = { previewId ->
                                timerViewModel.playPreview(previewId)
                            },
                            onConfirm = { newSoundId ->
                                timerViewModel.stopPreview()
                                timerViewModel.updateBackgroundSound(newSoundId)
                            },
                            onDismiss = {
                                timerViewModel.stopPreview()
                                closeDialog()
                            }
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
            }

            ToastMessage(
                message = errorMessage,
                onDismiss = { TODO() },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}