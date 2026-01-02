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
import com.yugentech.sessions.ui.dash.components.homeScreen.bottomRow.SetsConfigDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.bottomRow.SoundSelectionDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.DurationPickerDialog
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionConfigCard
import com.yugentech.sessions.ui.dash.components.homeScreen.durationSelection.SessionProgressCard
import com.yugentech.sessions.ui.dash.components.homeScreen.middle.ModeTag
import com.yugentech.sessions.ui.dash.components.homeScreen.middle.TimerDisplay
import com.yugentech.sessions.ui.dash.components.homeScreen.topRow.SessionHeader
import com.yugentech.sessions.ui.dash.components.homeScreen.topRow.TaskInputDialog
import com.yugentech.sessions.viewModels.HomeViewModel

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
                        isStudying = uiState.isRunning,
                        idleLabel = "Press the play button\nto start."
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
                            totalRounds = uiState.timerConfig.roundsBeforeLongBreak
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
                SessionControlBar()
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
                            onDismiss = closeDialog,
                            onConfirm = { rounds ->
                                homeViewModel.updateFullConfig(
                                    config.copy(
                                        roundsBeforeLongBreak = rounds
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