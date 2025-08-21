package com.yugentech.sessions.ui.dash.homeScreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.yugentech.sessions.alerts.viewmodel.AlertsViewModel
import com.yugentech.sessions.timer.viewmodel.TimerViewModel
import com.yugentech.sessions.ui.dash.homeScreen.components.FinishConfirmationDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.bottomRow.SessionControlBar
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.DurationPickerDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.GoalReachedDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.ReviewReminderDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.SetsSettingsSheet
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.SoundSelectionSheet
import com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection.SessionConfigCard
import com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection.SessionProgressCard
import com.yugentech.sessions.ui.dash.homeScreen.components.middle.TimerDisplay
import com.yugentech.sessions.ui.dash.homeScreen.components.topRow.SessionHeader
import com.yugentech.sessions.ui.dash.util.models.ActiveDialog
import com.yugentech.sessions.utils.AppConstants
import com.yugentech.sessions.viewModels.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    timerViewModel: TimerViewModel,
    userId: String,
    alertsViewModel: AlertsViewModel = koinViewModel()
) {
    val timerState by timerViewModel.timerState.collectAsStateWithLifecycle()
    val dashboardState by timerViewModel.dashboardState.collectAsStateWithLifecycle()
    val errorMessage by timerViewModel.errorMessage.collectAsStateWithLifecycle()
    val showGoalReached by timerViewModel.showGoalReachedDialog.collectAsStateWithLifecycle()
    val setsRemainingToConfirm by timerViewModel.showFinishConfirmation.collectAsStateWithLifecycle()
    val homeDataState by homeViewModel.dataState.collectAsStateWithLifecycle()

    val config = timerState.timerConfig
    val isSessionActive = !timerState.isIdle

    val view = LocalView.current
    val context = view.context
    val scrollState = rememberScrollState()
    var activeDialog by remember { mutableStateOf(ActiveDialog.None) }

    LaunchedEffect(userId) {
        homeViewModel.initUserData(userId)
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
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = screenHeight - 1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    SessionHeader(
                        isRunning = timerState.isTimerRunning,
                        sessionTask = config.sessionTask,
                        onTaskChange = { newTask ->
                            timerViewModel.updateSessionTask(newTask)
                        },
                        onSoundBadgeClick = {
                            timerViewModel.toggleAmbientSound()
                            alertsViewModel.performHaptic(view)
                        },
                        isAmbientEnabled = config.isAmbientEnabled,
                        activeBackgroundSoundId = config.activeBackgroundSoundId
                    )

                    TimerDisplay(
                        displayTime = timerState.currentTime.toInt(),
                        selectedDuration = timerState.totalTime.toInt(),
                        isStudying = timerState.isTimerRunning,
                        currentMode = timerState.currentMode,
                        idleLabel = errorMessage ?: "Press the play button\nto start."
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
                                onSkipToNext = {
                                    timerViewModel.skipToNextMode(view)
                                }
                            )
                        } else {
                            SessionConfigCard(
                                focusDurationMinutes = config.focusDuration,
                                shortBreakDurationMinutes = config.shortBreakDuration,
                                onFocusClick = {
                                    activeDialog = ActiveDialog.Focus
                                    alertsViewModel.performHaptic(view)
                                },
                                onShortBreakClick = {
                                    activeDialog = ActiveDialog.ShortBreak
                                    alertsViewModel.performHaptic(view)
                                }
                            )
                        }
                    }

                    SessionControlBar(
                        isStudying = timerState.isTimerRunning,
                        isSessionActive = isSessionActive,
                        onStartStop = {
                            if (timerState.isTimerRunning) {
                                alertsViewModel.performHaptic(view)
                                timerViewModel.stopTimer(view)
                            } else {
                                timerViewModel.startTimer(view)
                            }
                        },
                        onSoundClick = {
                            activeDialog = ActiveDialog.Sound
                            alertsViewModel.performHaptic(view)
                        },
                        onSetsClick = {
                            activeDialog = ActiveDialog.SetsSettings
                            alertsViewModel.performHaptic(view)
                        },
                        onStopDiscard = {
                            alertsViewModel.performHaptic(view)
                            timerViewModel.stopAndDiscardSession(view)
                        },
                        onStopSave = {
                            alertsViewModel.performHaptic(view)
                            timerViewModel.stopAndSaveSession(view)
                        }
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
                                range = 1..120,
                                step = 1,
                                onDismiss = closeDialog,
                                onConfirm = { newMins ->
                                    timerViewModel.updateFocusDuration(newMins)
                                    alertsViewModel.performHaptic(view)
                                    closeDialog()
                                }
                            )
                        }

                        ActiveDialog.ShortBreak -> {
                            DurationPickerDialog(
                                title = "Short Break",
                                description = "Choose the duration of your break between sessions.",
                                initialValue = currentShort,
                                range = 1..30,
                                step = 1,
                                onDismiss = closeDialog,
                                onConfirm = { newMins ->
                                    timerViewModel.updateShortBreakDuration(newMins)
                                    alertsViewModel.performHaptic(view)
                                    closeDialog()
                                }
                            )
                        }

                        ActiveDialog.SetsSettings -> {
                            SetsSettingsSheet(
                                currentSets = config.targetSets,
                                currentLongBreak = currentLong,
                                currentSetsPerLongBreak = config.setsPerLongBreak,
                                currentLongBreakEnabled = config.longBreakEnabled,
                                onDismiss = { newSets, newLongBreak, newSetsPerLongBreak, newLongBreakEnabled ->
                                    timerViewModel.updateLongBreakAndTargetSets(
                                        newSets,
                                        newLongBreak
                                    )
                                    timerViewModel.updateSetsPerLongBreak(newSetsPerLongBreak)
                                    timerViewModel.updateLongBreakEnabled(newLongBreakEnabled)
                                    closeDialog()
                                },
                                onHaptic = { alertsViewModel.performHaptic(view) }
                            )
                        }

                        ActiveDialog.Sound -> {
                            SoundSelectionSheet(
                                currentSoundId = config.activeBackgroundSoundId,
                                onPreview = { previewId ->
                                    timerViewModel.playPreview(previewId)
                                },
                                onConfirm = { newSoundId ->
                                    timerViewModel.stopPreview()
                                    timerViewModel.updateBackgroundSound(newSoundId)
                                    closeDialog()
                                },
                                onHaptic = { alertsViewModel.performHaptic(view) }
                            )
                        }

                        ActiveDialog.None -> Unit
                    }
                }


            }

            if (showGoalReached) {
                GoalReachedDialog(
                    onDismiss = {
                        timerViewModel.dismissGoalReachedDialog()
                        homeViewModel.triggerReviewPrompt()
                    }
                )
            }

            if (homeDataState.shouldShowReviewDialog) {
                ReviewReminderDialog(
                    onDismiss = { homeViewModel.onReviewDialogDismissed() },
                    onReviewClick = {
                        homeViewModel.onReviewPromptShown()
                        openPlayStore(context)
                    }
                )
            }

            setsRemainingToConfirm?.let { setsLeft ->
                FinishConfirmationDialog(
                    setsRemaining = setsLeft,
                    onConfirm = { timerViewModel.confirmFinishSession(view) },
                    onDismiss = { timerViewModel.dismissFinishConfirmation() }
                )
            }
        }
    }
}

private fun openPlayStore(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, AppConstants.MARKET_URL.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                AppConstants.PLAY_STORE_URL.toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}