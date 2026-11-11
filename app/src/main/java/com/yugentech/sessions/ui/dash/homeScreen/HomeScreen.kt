package com.yugentech.sessions.ui.dash.homeScreen

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.templates.viewmodel.TemplateViewModel
import com.yugentech.sessions.theme.config.HomeLayout
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.timer.viewmodel.TimerViewModel
import com.yugentech.sessions.ui.dash.homeScreen.components.FinishConfirmationDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.DurationPickerDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.GoalReachedDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.ReviewReminderDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.SetsSettingsSheet
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.SoundSelectionSheet
import com.yugentech.sessions.ui.dash.homeScreen.components.dialogs.TaskSelectionSheet
import com.yugentech.sessions.ui.dash.homeScreen.layouts.ClassicHome
import com.yugentech.sessions.ui.dash.homeScreen.layouts.refined.RefinedHome
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.AliveGradient
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.HomeActions
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.HomeUi
import com.yugentech.sessions.ui.dash.homeScreen.layouts.studio.StudioHome
import com.yugentech.sessions.ui.dash.mainScreen.components.bottomBarInset
import com.yugentech.sessions.ui.dash.mainScreen.components.topBarInset
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
    templateViewModel: TemplateViewModel = koinViewModel(),
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()
    val timerState by timerViewModel.timerState.collectAsStateWithLifecycle()
    val dashboardState by timerViewModel.dashboardState.collectAsStateWithLifecycle()
    val templates by templateViewModel.templates.collectAsStateWithLifecycle()
    val errorMessage by timerViewModel.errorMessage.collectAsStateWithLifecycle()
    val showGoalReached by timerViewModel.showGoalReachedDialog.collectAsStateWithLifecycle()
    val setsRemainingToConfirm by timerViewModel.showFinishConfirmation.collectAsStateWithLifecycle()
    val homeDataState by homeViewModel.dataState.collectAsStateWithLifecycle()

    val config = timerState.timerConfig
    val isSessionActive = !timerState.isIdle

    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
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
            val inset = topBarInset
            val bottomInset = bottomBarInset
            // The space between the top bar and the floating nav dock
            val screenHeight = maxHeight - inset - bottomInset

            // The newer layouts get a living glow across the top, up under the bar; Classic stays as it was
            if (themeConfig.homeLayout != HomeLayout.CLASSIC && themeConfig.showHomeGradient) {
                AliveGradient(
                    mode = timerState.currentMode,
                    running = timerState.isTimerRunning,
                    sessionActive = !timerState.isIdle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(maxHeight * 0.45f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = inset, bottom = bottomInset)
            ) {
                val ui = HomeUi(
                    timer = timerState,
                    dashboard = dashboardState,
                    errorMessage = errorMessage,
                    cardStyle = themeConfig.cardStyle,
                    cardAnimationStyle = themeConfig.cardAnimationStyle
                )
                val gesture = { haptic.performHapticFeedback(HapticFeedbackType.GestureEnd) }
                val actions = HomeActions(
                    onTaskClick = { activeDialog = ActiveDialog.TaskSelection; gesture() },
                    onToggleAmbient = { timerViewModel.toggleAmbientSound(); gesture() },
                    onFocusClick = { activeDialog = ActiveDialog.Focus; gesture() },
                    onShortBreakClick = { activeDialog = ActiveDialog.ShortBreak; gesture() },
                    onSoundClick = { activeDialog = ActiveDialog.Sound; gesture() },
                    onSetsClick = { activeDialog = ActiveDialog.SetsSettings; gesture() },
                    onStartStop = {
                        if (timerState.isTimerRunning) timerViewModel.stopTimer(view) else timerViewModel.startTimer(view)
                    },
                    onDiscard = { timerViewModel.stopAndDiscardSession(view) },
                    onFinish = { timerViewModel.stopAndSaveSession(view) },
                    onSkip = { timerViewModel.skipToNextMode(view) }
                )

                // The chosen design; every one draws the same session and opens the same sheets
                when (themeConfig.homeLayout) {
                    HomeLayout.CLASSIC -> ClassicHome(ui, actions, screenHeight)
                    HomeLayout.REFINED -> RefinedHome(ui, actions, screenHeight)
                    HomeLayout.STUDIO -> StudioHome(ui, actions, screenHeight)
                }

                if (activeDialog != ActiveDialog.None) {
                    val closeDialog = { activeDialog = ActiveDialog.None }

                    val currentFocus = config.focusDuration
                    val currentShort = config.shortBreakDuration
                    val currentLong = config.longBreakDuration

                    when (activeDialog) {
                        ActiveDialog.Focus -> {
                            DurationPickerDialog(
                                title = stringResource(R.string.duration_focus_title),
                                description = stringResource(R.string.duration_focus_subtitle),
                                initialValue = currentFocus,
                                range = 5..120,
                                step = 5,
                                onDismiss = closeDialog,
                                onConfirm = { newMins ->
                                    timerViewModel.updateFocusDuration(newMins)
                                    closeDialog()
                                },
                                onHaptic = { haptic.performHapticFeedback(HapticFeedbackType.GestureEnd) }
                            )
                        }

                        ActiveDialog.ShortBreak -> {
                            DurationPickerDialog(
                                title = stringResource(R.string.mode_short_break),
                                description = stringResource(R.string.duration_break_subtitle),
                                initialValue = currentShort,
                                range = 5..30,
                                step = 5,
                                onDismiss = closeDialog,
                                onConfirm = { newMins ->
                                    timerViewModel.updateShortBreakDuration(newMins)
                                    closeDialog()
                                },
                                onHaptic = { haptic.performHapticFeedback(HapticFeedbackType.GestureEnd) }
                            )
                        }

                        ActiveDialog.SetsSettings -> {
                            SetsSettingsSheet(
                                currentSets = config.targetSets,
                                currentLongBreak = currentLong,
                                currentSetsPerLongBreak = config.setsPerLongBreak,
                                currentLongBreakEnabled = config.longBreakEnabled,
                                onSave = { newSets, newLongBreak, newSetsPerLongBreak, newLongBreakEnabled ->
                                    timerViewModel.updateLongBreakAndTargetSets(newSets, newLongBreak)
                                    timerViewModel.updateSetsPerLongBreak(newSetsPerLongBreak)
                                    timerViewModel.updateLongBreakEnabled(newLongBreakEnabled)
                                    closeDialog()
                                },
                                onDismiss = closeDialog,
                                onHaptic = { haptic.performHapticFeedback(HapticFeedbackType.GestureEnd) }
                            )
                        }

                        ActiveDialog.TaskSelection -> {
                            TaskSelectionSheet(
                                currentTask = config.sessionTask,
                                templates = templates,
                                focusDuration = config.focusDuration,
                                shortBreakDuration = config.shortBreakDuration,
                                longBreakDuration = config.longBreakDuration,
                                targetSets = config.targetSets,
                                longBreakEnabled = config.longBreakEnabled,
                                setsPerLongBreak = config.setsPerLongBreak,
                                // Mid-session only the name can change: a template's timings would restart the timer
                                sessionActive = isSessionActive,
                                onSetTask = { newTask ->
                                    timerViewModel.updateSessionTask(newTask)
                                    closeDialog()
                                },
                                onApplyTemplate = { template ->
                                    timerViewModel.updateSessionTask(template.name)
                                    if (isSessionActive) {
                                        closeDialog()
                                        return@TaskSelectionSheet
                                    }
                                    timerViewModel.updateFocusDuration(template.focusDuration)
                                    timerViewModel.updateShortBreakDuration(template.shortBreakDuration)
                                    timerViewModel.updateLongBreakAndTargetSets(
                                        template.targetSets,
                                        template.longBreakDuration
                                    )
                                    timerViewModel.updateSetsPerLongBreak(template.setsPerLongBreak)
                                    timerViewModel.updateLongBreakEnabled(template.longBreakEnabled)
                                    closeDialog()
                                },
                                onSaveTemplate = { name ->
                                    templateViewModel.saveTemplate(name, config)
                                },
                                onDeleteTemplate = { id ->
                                    templateViewModel.deleteTemplate(id)
                                },
                                onDismiss = closeDialog,
                                onHaptic = { haptic.performHapticFeedback(HapticFeedbackType.GestureEnd) }
                            )
                        }

                        ActiveDialog.Sound -> {
                            SoundSelectionSheet(
                                currentSoundId = config.activeBackgroundSoundId,
                                onConfirm = { newSoundId ->
                                    timerViewModel.stopPreview()
                                    timerViewModel.updateBackgroundSound(newSoundId)
                                    closeDialog()
                                },
                                onDismiss = {
                                    timerViewModel.stopPreview()
                                    closeDialog()
                                },
                                onPreview = { previewId ->
                                    timerViewModel.playPreview(previewId)
                                },
                                onHaptic = { haptic.performHapticFeedback(HapticFeedbackType.GestureEnd) }
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