package com.yugentech.sessions.ui.screens.appScreens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.sessions.SessionsViewModel
import com.yugentech.sessions.ui.components.homeScreen.DurationSelector
import com.yugentech.sessions.ui.components.homeScreen.SessionActionButtons
import com.yugentech.sessions.ui.components.homeScreen.StudyingControlButtons
import com.yugentech.sessions.ui.components.homeScreen.TimerDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sessionsViewModel: SessionsViewModel,
    userId: String
) {
    val isStudying by sessionsViewModel.isStudying.collectAsStateWithLifecycle()
    val currentTime by sessionsViewModel.currentTime.collectAsStateWithLifecycle()
    val selectedDuration by sessionsViewModel.selectedDuration.collectAsStateWithLifecycle()
    val displayTime = if (isStudying || currentTime > 0) currentTime else selectedDuration
    val availableDurations = remember { listOf(25, 50) }

    fun startSession() = sessionsViewModel.startTimer()
    fun pauseSession() = sessionsViewModel.stopTimer()
    fun stopSession() = sessionsViewModel.stopAndDiscardSession()
    fun saveAndEndSession() = sessionsViewModel.stopAndSaveSession()
    fun handleDurationChange(minutes: Int) {
        sessionsViewModel.updateSelectedDuration(minutes)
        sessionsViewModel.resetTimer()
    }

    LaunchedEffect(userId) {
        sessionsViewModel.setUserId(userId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section - Clean and simple
            Text(
                text = if (isStudying) "Focus" else "Ready to Focus?",
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
                    containerColor = if (isStudying)
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
                                color = if (isStudying)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isStudying) "In Session" else "Idle",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isStudying)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                TimerDisplay(
                    displayTime = displayTime,
                    selectedDuration = selectedDuration,
                    isStudying = isStudying,
                    modifier = Modifier.padding()
                )
            }

            AnimatedVisibility(
                visible = !isStudying,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                DurationSelector(
                    selectedDuration = selectedDuration,
                    availableDurations = availableDurations,
                    onDurationSelected = { handleDurationChange(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = !isStudying || currentTime > 0,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {

                SessionActionButtons(
                    isStudying = isStudying,
                    onPlayPause = {
                        if (isStudying) {
                            pauseSession()
                        } else {
                            startSession()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = isStudying,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

                StudyingControlButtons(
                    onStop = { stopSession() },
                    onSave = { saveAndEndSession() }
                )
            }
        }
    }
}