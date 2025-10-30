package com.yugentech.sessions.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.ui.Tokens // Import your tokens
import com.yugentech.sessions.ui.components.homeScreen.DurationSelector
import com.yugentech.sessions.ui.components.homeScreen.SessionActionButtons
import com.yugentech.sessions.ui.components.homeScreen.StudyingControlButtons
import com.yugentech.sessions.ui.components.homeScreen.TimerDisplay
import com.yugentech.sessions.viewModels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userId: String,
    notificationsViewModel: NotificationsViewModel
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val tokens = Tokens // Initialize your tokens

    val displayTime = if (uiState.isRunning || uiState.currentTime > 0) {
        uiState.currentTime
    } else {
        uiState.selectedDuration
    }

    val availableDurations = remember { listOf(25, 50) }


    LaunchedEffect(userId) {
        homeViewModel.setUserId(userId)
        homeViewModel.fetchUserOnce(userId)
        homeViewModel.fetchSessionsOnce(userId)
        homeViewModel.syncPendingSessions(userId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Removed .verticalScroll()
                .padding(tokens.spacing.m), // Use token for screen padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (uiState.isRunning) "Focus" else "Ready to Focus?",
                    fontSize = tokens.typography.headline.sp, // Use token
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(tokens.spacing.s)) // Use token

                Card(
                    modifier = Modifier.padding(horizontal = tokens.spacing.m), // Use token
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isRunning)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(tokens.corners.smallMedium) // Use token
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = tokens.spacing.m, // Use token
                            vertical = tokens.spacing.s   // Use token
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(tokens.components.dotSize) // Use token
                                .background(
                                    color = if (uiState.isRunning)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(tokens.spacing.s)) // Use token

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

            // KEY LAYOUT FIX:
            // This Column takes up all the available space (weight = 1f)
            // and centers the TimerDisplay within it.
            // This replaces the old Spacer + Box + Spacer structure.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // This makes the timer area flexible
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // This centers the timer vertically
            ) {
                TimerDisplay(
                    displayTime = displayTime,
                    selectedDuration = uiState.selectedDuration,
                    isStudying = uiState.isRunning,
                    modifier = Modifier
                )
            }

            // Controls Section
            // This section is now naturally pushed to the bottom
            // by the weighted Column above.
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
                        onDurationSelected = {
                            homeViewModel.updateSelectedDuration(it)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(tokens.spacing.l)) // Use token

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

                Spacer(modifier = Modifier.height(tokens.spacing.l)) // Use token

                AnimatedVisibility(
                    visible = uiState.isRunning,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    val context = LocalContext.current
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
                                Toast.makeText(
                                    context,
                                    "Please focus for at least 1 minute to save a session",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                homeViewModel.stopAndSaveSession(view)
                                notificationsViewModel.stopActiveSession()
                            }
                        }
                    )
                }

                // This spacer gives some final padding at the bottom
                Spacer(modifier = Modifier.height(tokens.spacing.l)) // Use token
            }
        }
    }
}