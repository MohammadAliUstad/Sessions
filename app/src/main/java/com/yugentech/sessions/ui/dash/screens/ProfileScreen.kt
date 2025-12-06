package com.yugentech.sessions.ui.dash.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.components.profileScreen.EmptySessionsCard
import com.yugentech.sessions.ui.dash.components.profileScreen.ProfileCard
import com.yugentech.sessions.ui.dash.components.profileScreen.SectionHeader
import com.yugentech.sessions.ui.dash.components.profileScreen.SessionCard
import com.yugentech.sessions.viewModels.ProfileViewModel

@Composable
fun ProfileScreen(
    userId: String,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel,
    onEditProfile: () -> Unit = {}
) {
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(MaterialTheme.spacing.m),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
    ) {
        item {
            ProfileCard(
                userData = profileUiState.user ?: UserData(name = AppConstants.EMPTY_STRING),
                totalTime = profileUiState.totalTime,
                onEditProfile = onEditProfile
            )
        }

        item {
            SectionHeader(sessionCount = profileUiState.sessions.size)
        }

        if (profileUiState.sessions.isEmpty()) {
            item { EmptySessionsCard() }
        } else {
            items(
                items = profileUiState.sessions,
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