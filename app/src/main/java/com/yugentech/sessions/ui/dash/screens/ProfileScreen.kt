package com.yugentech.sessions.ui.dash.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.ui.dash.components.avatar.AvatarImage
import com.yugentech.sessions.ui.dash.components.avatar.AvatarRepository
import com.yugentech.sessions.ui.dash.components.profileScreen.EmptySessionsCard
import com.yugentech.sessions.ui.dash.components.profileScreen.SessionCard
import com.yugentech.sessions.ui.dash.components.profileScreen.StudyTimeSection
import com.yugentech.sessions.user.UserViewModel
import com.yugentech.sessions.utils.formatTime
import com.yugentech.sessions.viewModels.ProfileViewModel

@Composable
fun ProfileScreen(
    userId: String,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel,
    userViewModel: UserViewModel,
    onEditProfile: () -> Unit = {}
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
        userViewModel.loadUser(userId)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProfileCard(
                userData = uiState.user ?: UserData(name = "User"),
                totalTime = uiState.totalTime,
                onEditProfile = onEditProfile
            )
        }

        item {
            SectionHeader(sessionCount = uiState.sessions.size)
        }

        if (uiState.sessions.isEmpty()) {
            item { EmptySessionsCard() }
        } else {
            items(
                items = uiState.sessions,
                key = { it.sessionId }
            ) { session ->
                SessionCard(
                    session = session,
                    onDelete = { sessionId -> profileViewModel.deleteSession(sessionId) }
                )
            }
        }
    }
}

@Composable
private fun ProfileCard(
    userData: UserData,
    totalTime: Long,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEditProfile,
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            ProfileContent(userData = userData, totalTime = totalTime)
        }
    }
}

@Composable
private fun ProfileContent(
    userData: UserData,
    totalTime: Long
) {
    Text(
        text = userData.name ?: "User",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    AvatarImage(
        avatarId = userData.avatarId,
        size = 120.dp,
        contentDescription = "Profile Avatar"
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = AvatarRepository.getAvatarName(userData.avatarId) ?: "Explorer",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    StudyTimeSection(
        formattedTime = formatTime(totalTime)
    )
}

@Composable
private fun SectionHeader(sessionCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Study Sessions",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (sessionCount > 0) {
            Text(
                text = "$sessionCount session${if (sessionCount != 1) "s" else ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}