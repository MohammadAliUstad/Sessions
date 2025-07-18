package com.yugentech.sessions.ui.screens.appScreens

import android.app.ActivityManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.session.SessionViewModel
import com.yugentech.sessions.status.StatusViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sessionViewModel: SessionViewModel,
    statusViewModel: StatusViewModel,
    userId: String
) {
    val isStudying by sessionViewModel.isStudying.collectAsStateWithLifecycle()
    val currentTime by sessionViewModel.currentTime.collectAsStateWithLifecycle()
    val selectedDuration by sessionViewModel.selectedDuration.collectAsStateWithLifecycle()
    val displayTime = if (isStudying || currentTime > 0) currentTime else selectedDuration
    val context = LocalContext.current

    val safeProgress = remember(displayTime, selectedDuration) {
        if (selectedDuration > 0)
            (1f - (displayTime / selectedDuration.toFloat())).coerceIn(0f, 1f)
        else 0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = safeProgress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "TimerProgress"
    )

    LaunchedEffect(isStudying) {
        statusViewModel.setUserStatus(userId, isStudying)


    }

    DisposableEffect(Unit) {
        onDispose {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (!activityManager.appTasks.any { it.taskInfo != null }) {
                sessionViewModel.stopTimer()
                statusViewModel.setUserStatus(userId, false)
            }
        }
    }

    LaunchedEffect(userId) {
        sessionViewModel.setUserId(userId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            // Timer Section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background circle
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(320.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    strokeWidth = 12.dp,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round,
                )

                // Progress circle with gradient effect
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(320.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 12.dp,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round,
                )

                // Center content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format(
                            Locale.US, "%02d:%02d",
                            displayTime / 60, displayTime % 60
                        ),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-2).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isStudying) "time remaining" else "session duration",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            letterSpacing = 0.25.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Duration Selection (when not studying)
            AnimatedVisibility(
                visible = !isStudying,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Choose Duration",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf(25, 50).forEach { min ->
                                val isSelected = selectedDuration == min * 60

                                if (isSelected) {
                                    FilledTonalButton(
                                        onClick = {
                                            sessionViewModel.updateSelectedDuration(min)
                                            sessionViewModel.resetTimer()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Text(
                                            "$min min",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = {
                                            sessionViewModel.updateSelectedDuration(min)
                                            sessionViewModel.resetTimer()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text(
                                            "$min min",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Main Action Button
            AnimatedVisibility(
                visible = !isStudying || currentTime > 0,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        if (isStudying) sessionViewModel.stopTimer()
                        else sessionViewModel.startTimer()
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ),
                    containerColor = if (isStudying)
                        MaterialTheme.colorScheme.tertiaryContainer
                    else
                        MaterialTheme.colorScheme.primary,
                    contentColor = if (isStudying)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = if (isStudying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isStudying) "Pause" else "Start",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Session Control Buttons (when studying)
            AnimatedVisibility(
                visible = isStudying,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            sessionViewModel.stopTimer()
                            sessionViewModel.resetTimer()
                        },
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Stop",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    ExtendedFloatingActionButton(
                        onClick = {
                            sessionViewModel.stopTimer()
                            val elapsed = sessionViewModel.getElapsedTime()
                            if (elapsed > 0) {
                                sessionViewModel.saveSession(
                                    userId,
                                    Session(elapsed)
                                )
                                sessionViewModel.updateTotalTime(
                                    userId,
                                    elapsed
                                )
                            }
                            sessionViewModel.resetTimer()
                        },
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Save",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}