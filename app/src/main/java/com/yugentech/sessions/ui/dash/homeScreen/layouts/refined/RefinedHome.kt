package com.yugentech.sessions.ui.dash.homeScreen.layouts.refined

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.ui.dash.homeScreen.components.bottomRow.SessionControlBar
import com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection.SessionProgressCard
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.HomeActions
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.HomeUi

/** The calmer take: a status line over the task, the ring in the mode's colour, and a single plan card. */
@Composable
fun RefinedHome(ui: HomeUi, actions: HomeActions, screenHeight: Dp) {
    val config = ui.config
    val fadeThrough = AppAnimations.Transitions.rememberExpressiveFadeThrough()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = screenHeight - 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        RefinedHeader(
            isRunning = ui.isRunning,
            isSessionActive = ui.isSessionActive,
            sessionTask = config.sessionTask,
            onTaskClick = actions.onTaskClick,
            onSoundBadgeClick = actions.onToggleAmbient,
            mode = ui.mode,
            isAmbientEnabled = config.isAmbientEnabled,
            activeBackgroundSoundId = config.activeBackgroundSoundId
        )

        RefinedTimer(
            displayTime = ui.timer.currentTime.toInt(),
            selectedDuration = ui.timer.totalTime.toInt(),
            isStudying = ui.isRunning,
            currentMode = ui.mode,
            isSessionActive = ui.isSessionActive,
            errorMessage = ui.errorMessage
        )

        AnimatedContent(
            targetState = ui.isSessionActive,
            transitionSpec = { fadeThrough() },
            label = "dashboard_swap"
        ) { showProgress ->
            if (showProgress) {
                SessionProgressCard(
                    state = ui.dashboard,
                    targetSets = config.targetSets,
                    blockProgress = ui.blockProgress,
                    isTimerRunning = ui.isRunning,
                    onSkipToNext = actions.onSkip,
                    cardStyle = ui.cardStyle,
                    animationStyle = ui.cardAnimationStyle,
                    pulse = ui.pulse
                )
            } else {
                RefinedPlanCard(
                    focusDurationMinutes = config.focusDuration,
                    shortBreakDurationMinutes = config.shortBreakDuration,
                    onFocusClick = actions.onFocusClick,
                    onShortBreakClick = actions.onShortBreakClick,
                    targetSets = config.targetSets,
                    longBreakDurationMinutes = config.longBreakDuration,
                    setsPerLongBreak = config.setsPerLongBreak,
                    longBreakEnabled = config.longBreakEnabled
                )
            }
        }

        SessionControlBar(
            isStudying = ui.isRunning,
            isSessionActive = ui.isSessionActive,
            onStartStop = actions.onStartStop,
            onSoundClick = actions.onSoundClick,
            onSetsClick = actions.onSetsClick,
            onStopDiscard = actions.onDiscard,
            onStopSave = actions.onFinish,
            // Quiet while a session runs: play in a soft secondary tone, Discard and Finish on a
            // plain surface told apart by their icon colours; no tertiary
            playingContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            playingContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            quietSideButtons = true
        )
    }
}
