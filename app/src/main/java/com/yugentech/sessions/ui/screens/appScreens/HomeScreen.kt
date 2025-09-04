package com.yugentech.sessions.ui.screens.appScreens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.ui.components.homeScreen.DurationSelector
import com.yugentech.sessions.ui.components.homeScreen.SessionActionButtons
import com.yugentech.sessions.ui.components.homeScreen.StudyingControlButtons
import com.yugentech.sessions.ui.components.homeScreen.TimerDisplay
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.notifications.NotificationViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userId: String,
    notificationViewModel: NotificationViewModel = koinViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val notificationUiState by notificationViewModel.uiState.collectAsStateWithLifecycle()
    val notificationPermission by notificationViewModel.notificationPermissionGranted.collectAsStateWithLifecycle()

    val displayTime = if (uiState.isRunning || uiState.currentTime > 0) {
        uiState.currentTime
    } else {
        uiState.selectedDuration
    }

    val availableDurations = remember { listOf(25, 50) }

    // Initialize user ID
    LaunchedEffect(userId) {
        homeViewModel.setUserId(userId)
    }

    // Handle session state changes for notifications
    LaunchedEffect(uiState.isRunning) {
        if (uiState.isRunning) {
            // 🔧 FIXED: Calculate minutes and seconds correctly from milliseconds
            val seconds = (displayTime / 1000 / 60).toInt()  // Convert ms to minutes
            val minutes = uiState.selectedDuration  // This should already be in minutes

            notificationViewModel.startSessionNotification(
                sessionType = "Study",
                timeRemainingMinutes = seconds,
                totalMinutes = minutes
            )

            // 🔧 FIXED: Start the update loop AFTER initial notification
            while (displayTime > 0) {
                delay(10_000)  // Wait 10 seconds between updates

                // Check again after delay
                val updatedDisplayTime = uiState.currentTime

                val seconds = (updatedDisplayTime / 1000 / 60).toInt()
                val minutes = uiState.selectedDuration

                notificationViewModel.updateActiveSessionNotification(
                    timeRemainingMinutes = seconds,
                    totalMinutes = minutes
                )
            }
        } else {
            // Session ended - hide active notification
            notificationViewModel.hideActiveSessionNotification()
        }
    }

    // Handle session completion (when timer reaches 0)
    LaunchedEffect(uiState.currentTime) {
        // Check if timer just completed (was running but now at 0)
        if (uiState.currentTime == 0 && !uiState.isRunning) {
            notificationViewModel.endSessionNotification("Study")
        }
    }

    // Update notification permission when it changes
    LaunchedEffect(notificationPermission) {
        // This will be updated when user grants/denies permission
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.height(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (uiState.isRunning) "Focus" else "Ready to Focus?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isRunning)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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

                // Show notification status if there are issues
                if (!notificationPermission && uiState.isRunning) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠️ Notification permission needed",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Box(
                modifier = Modifier.height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                TimerDisplay(
                    displayTime = displayTime,
                    selectedDuration = uiState.selectedDuration,
                    isStudying = uiState.isRunning,
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier.height(200.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
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

                    Spacer(modifier = Modifier.height(16.dp))

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
                                    // Stop timer
                                    homeViewModel.stopTimer(view)
                                    // Notification will be handled by LaunchedEffect above
                                } else {
                                    // Start timer
                                    homeViewModel.startTimer(view)
                                    // Notification will be handled by LaunchedEffect above
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                                // Hide notification when session is discarded
                                notificationViewModel.hideActiveSessionNotification()
                            },
                            onSave = {
                                val elapsed = homeViewModel.getElapsedTime()
                                if (elapsed < 60) {
                                    homeViewModel.stopAndDiscardSession(view)
                                    notificationViewModel.hideActiveSessionNotification()
                                    Toast.makeText(
                                        context,
                                        "Please focus for at least 1 minute to save a session",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    homeViewModel.stopAndSaveSession(view)
                                    // Show completion notification
                                    notificationViewModel.endSessionNotification("Study")
                                }
                            }
                        )
                    }

                    // Show error messages from both ViewModels
                    val errorMessage = uiState.errorMessage ?: notificationUiState.error
                    errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}