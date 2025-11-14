package com.yugentech.sessions.ui.dash.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.components.common.ToastMessage
import com.yugentech.sessions.ui.dash.components.homeScreen.DurationSelector
import com.yugentech.sessions.ui.dash.components.homeScreen.SessionActionButtons
import com.yugentech.sessions.ui.dash.components.homeScreen.StudyingControlButtons
import com.yugentech.sessions.ui.dash.components.homeScreen.TimerDisplay
import com.yugentech.sessions.viewModels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userId: String,
    notificationsViewModel: NotificationsViewModel
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val displayTime = if (uiState.isRunning || uiState.currentTime > 0) {
        uiState.currentTime
    } else {
        uiState.selectedDuration
    }

    val availableDurations = remember { listOf(25, 50) }

    LaunchedEffect(userId) {
        homeViewModel.setUserId(userId)
        homeViewModel.updateSelectedDuration(25)
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
                    .padding(MaterialTheme.spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Header Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (uiState.isRunning) "Focus" else "Ready to Start?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.isRunning)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(MaterialTheme.corners.smallMedium)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(
                                    horizontal = MaterialTheme.spacing.m,
                                    vertical = MaterialTheme.spacing.s
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(MaterialTheme.components.dotSize)
                                    .background(
                                        color = if (uiState.isRunning)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                            )

                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))

                            Text(
                                text = if (uiState.isRunning) "In Session" else "Idle",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (uiState.isRunning)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Timer Display Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TimerDisplay(
                        displayTime = displayTime,
                        selectedDuration = uiState.selectedDuration,
                        isStudying = uiState.isRunning
                    )
                }

                // Controls Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    AnimatedVisibility(
                        visible = !uiState.isRunning,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        DurationSelector(
                            selectedDuration = uiState.selectedDuration,
                            availableDurations = availableDurations,
                            onDurationSelected = { homeViewModel.updateSelectedDuration(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))

                    AnimatedVisibility(
                        visible = !uiState.isRunning || uiState.currentTime > 0,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        val view = LocalView.current
                        SessionActionButtons(
                            isStudying = uiState.isRunning,
                            onPlayPause = {
                                if (uiState.isRunning) {
                                    homeViewModel.stopTimer(view)
                                    notificationsViewModel.stopActiveSession()
                                } else {
                                    homeViewModel.startTimer(view)
                                    notificationsViewModel.startActiveSession(
                                        Notification(
                                            id = 1001,
                                            title = "Focus",
                                            message = "Session in progress",
                                            type = NotificationType.ACTIVE,
                                            isOngoing = true,
                                            remainingSeconds = displayTime
                                        )
                                    )
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))

                    AnimatedVisibility(
                        visible = uiState.isRunning,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        val view = LocalView.current
                        StudyingControlButtons(
                            onStop = {
                                homeViewModel.stopAndDiscardSession(view)
                                notificationsViewModel.stopActiveSession()
                            },
                            onSave = {
                                val elapsed = homeViewModel.getElapsedTime()
                                if (elapsed < 60) {
                                    homeViewModel.stopAndDiscardSession(view)
                                    notificationsViewModel.stopActiveSession()
                                    toastMessage =
                                        "Please focus for at least 1 minute to save a session"
                                } else {
                                    homeViewModel.stopAndSaveSession(view)
                                    notificationsViewModel.stopActiveSession()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))
                }
            }

            // Toast overlay
            ToastMessage(
                message = toastMessage,
                onDismiss = { toastMessage = null },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}