package com.yugentech.sessions.ui.dash.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.components.profileScreen.ProfileInfoItem
import com.yugentech.sessions.ui.dash.components.profileScreen.ProfileSectionHeader
import com.yugentech.sessions.ui.dash.components.profileScreen.SessionHistoryItem
import com.yugentech.sessions.viewModels.ProfileViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileScreen(
    userId: String,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel,
    onEditProfile: () -> Unit = {}
) {
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    // State for delete dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var sessionToDeleteId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }

    if (profileUiState.user == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularWavyProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            // 1. Content Padding for floating nav bar
            contentPadding = PaddingValues(
                top = MaterialTheme.spacing.s,
                start = MaterialTheme.spacing.m,
                end = MaterialTheme.spacing.m,
                bottom = 80.dp
            ),
            // 2. The "Grouped List" spacing
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // --- GROUP 1: User Profile ---
            item {
                ProfileSectionHeader(title = "Profile")
            }
            item {
                ProfileInfoItem(
                    userData = profileUiState.user ?: UserData(name = AppConstants.EMPTY_STRING),
                    totalTime = profileUiState.totalTime,
                    onEditProfile = onEditProfile
                )
            }

            // --- GROUP 2: Session History ---
            // If there are sessions, show the header and list
            if (profileUiState.sessions.isNotEmpty()) {
                item {
                    // Spacer for separation between groups
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))

                    ProfileSectionHeader(
                        title = "Session History",
                        countLabel = "${profileUiState.sessions.size} sessions"
                    )
                }

                // The Session Items
                itemsIndexed(
                    items = profileUiState.sessions,
                    key = { _, session -> session.sessionId }
                ) { index, session ->
                    // Using animateItem for smooth reordering/deletion (M3 feature)
                    Box(modifier = Modifier.animateItem()) {
                        SessionHistoryItem(
                            session = session,
                            index = index,
                            totalCount = profileUiState.sessions.size,
                            onDelete = {
                                sessionToDeleteId = session.sessionId
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // --- Delete Confirmation Dialog ---
    if (showDeleteDialog && sessionToDeleteId != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                sessionToDeleteId = null
            },
            title = {
                Text(
                    "Delete Session?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "This action cannot be undone. The session data will be permanently removed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileViewModel.deleteSession(userId, sessionToDeleteId!!)
                        showDeleteDialog = false
                        sessionToDeleteId = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        sessionToDeleteId = null
                    }
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}