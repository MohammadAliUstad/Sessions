package com.yugentech.sessions.ui.dash.homeScreen

import android.app.Activity
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
import com.google.android.play.core.review.ReviewManagerFactory
import com.yugentech.sessions.alerts.viewmodel.AlertsViewModel
import com.yugentech.sessions.timer.viewmodel.TimerViewModel
import com.yugentech.sessions.ui.dash.homeScreen.components.FinishConfirmationDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.bottomRow.SessionControlBar
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.DurationPickerDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.GoalReachedDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.ReviewReminderDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.SetsSettingsDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.SoundSelectionDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection.SessionConfigCard
import com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection.SessionProgressCard
import com.yugentech.sessions.ui.dash.homeScreen.components.middle.TimerDisplay
import com.yugentech.sessions.ui.dash.homeScreen.components.topRow.SessionHeader
import com.yugentech.sessions.ui.dash.util.models.ActiveDialog
import com.yugentech.sessions.utils.AppConstants
import com.yugentech.sessions.viewModels.HomeViewModel
import org.koin.androidx.compose.koinViewModel

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
                            timerViewModel.stopAndDiscardSession(view)
                        },
                        onStopSave = {
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
                                range = 15..120,
                                step = 5,
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
                                range = 5..30,
                                step = 5,
                                onDismiss = closeDialog,
                                onConfirm = { newMins ->
                                    timerViewModel.updateShortBreakDuration(newMins)
                                    alertsViewModel.performHaptic(view)
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
                                    timerViewModel.updateLongBreakAndTargetSets(
                                        newSets,
                                        newLongBreak
                                    )
                                    alertsViewModel.performHaptic(view)
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
                                    alertsViewModel.performHaptic(view)
                                    closeDialog()
                                },
                                onDismiss = {
                                    timerViewModel.stopPreview()
                                    closeDialog()
                                }
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
                        launchReviewFlow(context)
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

private fun launchReviewFlow(context: Context) {
    val manager = ReviewManagerFactory.create(context)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val activity = context as? Activity
            if (activity != null) {
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished.
                    // Google recommends opening the Play Store regardless if we want to ensure they can review.
                    // But usually, if they saw the native dialog, we shouldn't redirect them immediately.
                    // However, the native dialog might NOT show up due to quota.
                    // So we can check if it actually showed up? No, the API doesn't provide that.
                    // A common trick is to ALWAYS open the Play Store if the user explicitly clicked "Rate".
                    openPlayStore(context)
                }
            } else {
                openPlayStore(context)
            }
        } else {
            openPlayStore(context)
        }
    }
}

private fun openPlayStore(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.MARKET_URL)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(AppConstants.PLAY_STORE_URL)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}