package com.yugentech.sessions.ui.screens.appScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.sessions.SessionsViewModel
import com.yugentech.sessions.ui.components.profileScreen.EmptySessionsIllustration
import com.yugentech.sessions.ui.components.avatar.AvatarImage
import com.yugentech.sessions.ui.components.avatar.AvatarRepository
import com.yugentech.sessions.ui.components.profileScreen.SessionList
import com.yugentech.sessions.ui.components.profileScreen.StudyTimeSection
import com.yugentech.sessions.user.UserViewModel
import com.yugentech.sessions.utils.formatTime

@Composable
fun ProfileScreen(
    userId: String,
    modifier: Modifier = Modifier,
    sessionsViewModel: SessionsViewModel,
    userViewModel: UserViewModel,
    onEditProfile: () -> Unit = {}
) {
    val userData by userViewModel.userState.collectAsStateWithLifecycle()
    val userIsLoading by userViewModel.isLoading.collectAsStateWithLifecycle()
    val userErrorMessage by userViewModel.errorMessage.collectAsStateWithLifecycle()
    val sessions by sessionsViewModel.sessions.collectAsStateWithLifecycle()
    val sessionsIsLoading by sessionsViewModel.isLoading.collectAsStateWithLifecycle()
    val sessionsErrorMessage by sessionsViewModel.errorMessage.collectAsStateWithLifecycle()
    val totalTime by userViewModel.totalTime.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        userViewModel.loadUser(userId)
        sessionsViewModel.setUserId(userId)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ProfileCard(
                userData = userData,
                totalTime = totalTime,
                isLoading = userIsLoading,
                errorMessage = userErrorMessage,
                onEditProfile = onEditProfile
            )
        }

        item {
            SessionsCard(
                sessions = sessions,
                isLoading = sessionsIsLoading,
                errorMessage = sessionsErrorMessage
            )
        }
    }
}

@Composable
private fun ProfileCard(
    userData: UserData?,
    totalTime: Long,
    isLoading: Boolean,
    errorMessage: String?,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Edit Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEditProfile,
                    enabled = userData != null && !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when {
                isLoading -> {
                    ProfileLoadingState()
                }

                errorMessage != null -> {
                    ProfileErrorState(errorMessage = errorMessage)
                }

                userData != null -> {
                    ProfileContent(
                        userData = userData,
                        totalTime = totalTime
                    )
                }

                else -> {
                    ProfileEmptyState()
                }
            }
        }
    }
}

@Composable
private fun ProfileLoadingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading profile...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileErrorState(errorMessage: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "Failed to load profile",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProfileContent(
    userData: UserData,
    totalTime: Long
) {
    // User Name
    Text(
        text = userData.name ?: "User",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Avatar
    AvatarImage(
        avatarId = userData.avatarId,
        size = 160.dp,
        contentDescription = "Profile Avatar"
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Avatar Name
    Text(
        text = AvatarRepository.getAvatarName(userData.avatarId) ?: "Explorer",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Study Time Section
    StudyTimeSection(
        formattedTime = formatTime(totalTime)
    )
}

@Composable
private fun ProfileEmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "No profile data",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SessionsCard(
    sessions: List<Session>,
    isLoading: Boolean,
    errorMessage: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Section Header
            Text(
                text = "Recent Sessions",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            when {
                isLoading -> {
                    SessionsLoadingState()
                }

                errorMessage != null -> {
                    SessionsErrorState(errorMessage = errorMessage)
                }

                sessions.isEmpty() -> {
                    EmptySessionsIllustration(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    SessionList(
                        sessions = sessions,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionsLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading sessions...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SessionsErrorState(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Failed to load sessions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}