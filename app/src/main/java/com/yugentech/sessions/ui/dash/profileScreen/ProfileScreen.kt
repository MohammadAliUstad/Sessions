package com.yugentech.sessions.ui.dash.profileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.user.model.UserData
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import com.yugentech.sessions.ui.dash.profileScreen.components.EmptySessionsCard
import com.yugentech.sessions.ui.dash.profileScreen.components.ProfileCard
import com.yugentech.sessions.ui.dash.profileScreen.components.SessionHistoryItem
import com.yugentech.sessions.ui.dash.util.dateHeader
import com.yugentech.sessions.utils.AppConstants.EMPTY_STRING
import com.yugentech.sessions.viewModels.ProfileViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileScreen(
    userId: String,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel,
    onEditProfile: () -> Unit = {},
    onViewInsights: () -> Unit = {}
) {
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var sessionToDeleteId by remember { mutableStateOf<String?>(null) }

    val layoutDirection = LocalLayoutDirection.current
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    val screenContentPadding = PaddingValues(
        start = MaterialTheme.spacing.m + navBarPadding.calculateStartPadding(layoutDirection),
        end = MaterialTheme.spacing.m + navBarPadding.calculateEndPadding(layoutDirection),
        bottom = MaterialTheme.spacing.edge
    )

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }

    val groupedSessions = remember(profileUiState.sessions) {
        profileUiState.sessions.groupBy { session ->
            dateHeader(session.timestamp)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = screenContentPadding,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
    ) {
        item(key = "profile_header") {
            SectionHeader(
                title = "My Account",
                icon = Icons.Filled.Person
            )
        }

        item(key = "profile_card") {
            ProfileCard(
                userData = profileUiState.user ?: UserData(name = EMPTY_STRING),
                onEditProfile = onEditProfile,
                onViewInsights = onViewInsights,
                streakCount = profileUiState.streakCount
            )
        }

        if (profileUiState.sessions.isEmpty()) {
            item(key = "empty_state") {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight(0.4f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptySessionsCard()
                }
            }
        } else {
            groupedSessions.forEach { (dateHeader, sessionsInGroup) ->
                item(key = "header_$dateHeader") {
                    SectionHeader(
                        title = dateHeader,
                        icon = Icons.Default.DateRange
                    )
                }

                itemsIndexed(
                    items = sessionsInGroup,
                    key = { _, session -> session.sessionId }
                ) { index, session ->
                    Box(modifier = Modifier.animateItem()) {
                        SessionHistoryItem(
                            session = session,
                            index = index,
                            totalCount = sessionsInGroup.size,
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

    // --- Dialogs ---
    if (showDeleteDialog && sessionToDeleteId != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                sessionToDeleteId = null
            },
            title = {
                Text("Delete Session?", style = MaterialTheme.typography.headlineSmall)
            },
            text = {
                Text(
                    "This action cannot be undone. The session data will be permanently removed.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = RoundedCornerShape(MaterialTheme.corners.large),
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
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    }
}