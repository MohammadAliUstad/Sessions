package com.yugentech.sessions.ui.dash.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.components.common.ToastMessage
import com.yugentech.sessions.ui.dash.components.homeScreen.ActionButton
import com.yugentech.sessions.ui.dash.components.homeScreen.DurationControl
import com.yugentech.sessions.ui.dash.components.homeScreen.SessionHeader
import com.yugentech.sessions.ui.dash.components.homeScreen.TimerDisplay
import com.yugentech.sessions.viewModels.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userId: String
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    // Local state for UI-specific errors (like "Session too short")
    var localToastMessage by remember { mutableStateOf<String?>(null) }

    val availableDurations = remember { listOf(25, 50) }
    val scrollState = rememberScrollState()
    val view = LocalView.current
    val errorSessionTooShort = stringResource(R.string.please_focus_for_at_least_1_minute_to_save_a_session)

    // Determine which error to show: ViewModel error takes priority
    val activeErrorMessage = uiState.errorMessage ?: localToastMessage

    val displayTime = if (uiState.isRunning || uiState.currentTime > 0) {
        uiState.currentTime
    } else {
        uiState.selectedDuration
    }

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
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                SessionHeader(isRunning = uiState.isRunning)

                TimerDisplay(
                    displayTime = displayTime,
                    selectedDuration = uiState.selectedDuration,
                    isStudying = uiState.isRunning
                )

                DurationControl(
                    selectedDuration = uiState.selectedDuration,
                    availableDurations = availableDurations,
                    isSessionActive = uiState.isRunning,
                    onDurationSelected = { homeViewModel.updateSelectedDuration(it) },
                    onStop = {
                        homeViewModel.stopAndDiscardSession(view)
                    },
                    onSave = {
                        val elapsed = homeViewModel.getElapsedTime()
                        if (elapsed < 60) {
                            homeViewModel.stopAndDiscardSession(view)
                            // Set the local error
                            localToastMessage = errorSessionTooShort
                        } else {
                            homeViewModel.stopAndSaveSession(view)
                        }
                    },
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.m)
                )

                AnimatedVisibility(
                    visible = !uiState.isRunning || uiState.currentTime > 0,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    ActionButton(
                        isStudying = uiState.isRunning,
                        onPlayPause = {
                            if (uiState.isRunning) {
                                homeViewModel.stopTimer(view)
                            } else {
                                homeViewModel.startTimer(view)
                            }
                        }
                    )
                }
            }

            // Updated Toast Logic
            ToastMessage(
                message = activeErrorMessage,
                onDismiss = {
                    // Clear ViewModel error if it exists
                    if (uiState.errorMessage != null) {
                        homeViewModel.clearError()
                    }
                    // Clear local error if it exists
                    localToastMessage = null
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}