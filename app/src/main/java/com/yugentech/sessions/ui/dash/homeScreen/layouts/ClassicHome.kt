package com.yugentech.sessions.ui.dash.homeScreen.layouts

import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.ui.dash.homeScreen.components.bottomRow.SessionControlBar
import com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection.SessionConfigCard
import com.yugentech.sessions.ui.dash.homeScreen.components.durationSelection.SessionProgressCard
import com.yugentech.sessions.ui.dash.homeScreen.components.middle.TimerDisplay
import com.yugentech.sessions.ui.dash.homeScreen.components.topRow.SessionHeader
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.HomeActions
import com.yugentech.sessions.ui.dash.homeScreen.layouts.shared.HomeUi

/** The original Home, exactly as it was: header chips, wavy ring, plan or progress card, button group. */
@Composable
fun ClassicHome(ui: HomeUi, actions: HomeActions, screenHeight: Dp) {
    val config = ui.config
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = screenHeight - 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        SessionHeader(
            isRunning = ui.isRunning,
            isSessionActive = ui.isSessionActive,
            sessionTask = config.sessionTask,
            onTaskClick = actions.onTaskClick,
            onSoundBadgeClick = actions.onToggleAmbient,
            isAmbientEnabled = config.isAmbientEnabled,
            activeBackgroundSoundId = config.activeBackgroundSoundId
        )

        TimerDisplay(
            displayTime = ui.timer.currentTime.toInt(),
            selectedDuration = ui.timer.totalTime.toInt(),
            isStudying = ui.isRunning,
            currentMode = ui.mode,
            idleLabel = ui.errorMessage ?: stringResource(R.string.timer_idle_classic)
        )

        AnimatedContent(targetState = ui.isSessionActive, label = "dashboard_swap") { showProgress ->
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
                SessionConfigCard(
                    focusDurationMinutes = config.focusDuration,
                    shortBreakDurationMinutes = config.shortBreakDuration,
                    onFocusClick = actions.onFocusClick,
                    onShortBreakClick = actions.onShortBreakClick
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
            onStopSave = actions.onFinish
        )
    }
}
