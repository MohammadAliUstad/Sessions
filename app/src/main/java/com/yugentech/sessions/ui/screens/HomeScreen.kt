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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import com.yugentech.sessions.notifications.NotificationsViewModel
// Removed this import
// import com.yugentech.sessions.ui.ResponsiveDimensions
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

    val displayTime = if (uiState.isRunning || uiState.currentTime > 0) {
        uiState.currentTime
    } else {
        uiState.selectedDuration
    }

    val availableDurations = remember { listOf(25, 50) }

    // Removed the ResponsiveDimensions variables

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
                .verticalScroll(rememberScrollState())
                // Replaced variables with standard 16.dp padding
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (uiState.isRunning) "Focus" else "Ready to Focus?",
                    // Replaced titleSize with 28.sp
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // Replaced spacing.small with 8.dp
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    // Replaced spacing.medium with 16.dp
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isRunning)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    // Replaced cornerRadius with 12.dp
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            // Replaced spacing.medium and spacing.small
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (uiState.isRunning)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                        )

                        // Replaced spacing.small with 8.dp
                        Spacer(modifier = Modifier.width(8.dp))

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

            // Replaced spacing.large with 24.dp
            Spacer(modifier = Modifier.height(24.dp))

            // Timer Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // Replaced spacing.medium with 16.dp
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TimerDisplay(
                    displayTime = displayTime,
                    selectedDuration = uiState.selectedDuration,
                    isStudying = uiState.isRunning,
                    modifier = Modifier
                )
            }

            // Replaced spacing.large with 24.dp
            Spacer(modifier = Modifier.height(24.dp))

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
                        onDurationSelected = {
                            homeViewModel.updateSelectedDuration(it)
                        }
                    )
                }

                // Replaced spacing.large with 24.dp
                Spacer(modifier = Modifier.height(24.dp))

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

                // Replaced spacing.large with 24.dp
                Spacer(modifier = Modifier.height(24.dp))

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

                // Replaced spacing.large with 24.dp
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}