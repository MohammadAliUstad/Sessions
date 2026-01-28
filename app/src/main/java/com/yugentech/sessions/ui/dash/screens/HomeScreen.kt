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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.timer.TimerViewModel
import com.yugentech.sessions.ui.dash.common.ToastMessage
import com.yugentech.sessions.ui.dash.components.homeScreen.bottomRow.SessionControlBar
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.DurationPickerDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.SetsSettingsDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.SoundSelectionDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.dialogs.TaskInputDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionConfigCard
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionProgressCard
import com.yugentech.sessions.ui.dash.components.homeScreen.middle.TimerDisplay
import com.yugentech.sessions.ui.dash.components.homeScreen.topRow.SessionHeader
import com.yugentech.sessions.ui.dash.models.ActiveDialog
import com.yugentech.sessions.viewModels.HomeViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    timerViewModel: TimerViewModel,
    userId: String
) {
    val timerState by timerViewModel.timerState.collectAsStateWithLifecycle()
    val dashboardState by timerViewModel.dashboardState.collectAsStateWithLifecycle()
    val errorMessage by timerViewModel.errorMessage.collectAsStateWithLifecycle()

    val config = timerState.timerConfig
    val isSessionActive = !timerState.isIdle

    val view = LocalView.current
    val scrollState = rememberScrollState()
    var activeDialog by remember { mutableStateOf(ActiveDialog.None) }

    LaunchedEffect(userId) {
        homeViewModel.fetchUserOnce(userId)
        homeViewModel.fetchSessionsOnce(userId)
        homeViewModel.syncPendingSessions(userId)
//        homeViewModel.injectDummyData()
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
                    .padding(bottom = MaterialTheme.components.bottomNavHeight),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SessionHeader(
                    isRunning = timerState.isTimerRunning,
                    sessionTask = config.sessionTask,
                    onTaskClick = { activeDialog = ActiveDialog.Task },
                    activeBackgroundSoundId = config.activeBackgroundSoundId
                )

                TimerDisplay(
                    displayTime = timerState.currentTime.toInt(),
                    selectedDuration = timerState.totalTime.toInt(),
                    isStudying = timerState.isTimerRunning,
                    currentMode = timerState.currentMode,
                )

                AnimatedContent(
                    targetState = isSessionActive,
                    label = "dashboard_swap"
                ) { showProgress ->
                    if (showProgress) {
                        SessionProgressCard(
                            state = dashboardState,
                            targetSets = config.targetSets,
                            isTimerRunning = timerState.isTimerRunning,
                            onSkipToNext = { timerViewModel.skipToNextMode(view) }
                        )
                    } else {
                        SessionConfigCard(
                            focusDurationMinutes = config.focusDuration,
                            shortBreakDurationMinutes = config.shortBreakDuration,
                            onFocusClick = { activeDialog = ActiveDialog.Focus },
                            onShortBreakClick = { activeDialog = ActiveDialog.ShortBreak }
                        )
                    }
                }

                SessionControlBar(
                    isStudying = timerState.isTimerRunning,
                    isSessionActive = isSessionActive,
                    onStartStop = {
                        if (timerState.isTimerRunning) {
                            timerViewModel.stopTimer(view)
                        } else {
                            timerViewModel.startTimer(view)
                        }
                    },
                    onSoundClick = { activeDialog = ActiveDialog.Sound },
                    onSetsClick = { activeDialog = ActiveDialog.SetsSettings },
                    onStopDiscard = { timerViewModel.stopAndDiscardSession(view) },
                    onStopSave = { timerViewModel.stopAndSaveSession(view) }
                )
            }

            if (activeDialog != ActiveDialog.None) {
                val closeDialog = { activeDialog = ActiveDialog.None }

                val currentFocus = config.focusDuration
                val currentShort = config.shortBreakDuration
                val currentLong = config.longBreakDuration

                when (activeDialog) {
                    ActiveDialog.Focus -> {
                        DurationPickerDialog(
                            title = "Focus Duration",
                            description = "Choose how long you want to focus before taking a break.",
                            initialValue = currentFocus,
                            range = 15..120,
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
                            range = 5..30,
                            step = 5,
                            onDismiss = closeDialog,
                            onConfirm = { newMins ->
                                timerViewModel.updateShortBreakDuration(newMins)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.SetsSettings -> {
                        SetsSettingsDialog(
                            currentSets = config.targetSets,
                            currentLongBreak = currentLong,
                            focusDuration = currentFocus,
                            setsPerLongBreak = config.setsPerLongBreak,
                            onDismiss = closeDialog,
                            onConfirm = { newSets, newLongBreak ->
                                timerViewModel.updateLongBreakAndTargetSets(newSets, newLongBreak)
                                closeDialog()
                            }
                        )
                    }

                    ActiveDialog.Sound -> {
                        SoundSelectionDialog(
                            currentSoundId = config.activeBackgroundSoundId,
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
                            currentTask = config.sessionTask,
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
                onDismiss = { timerViewModel.clearErrorMessage() },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}