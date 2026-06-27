package com.yugentech.sessions.ui.dash.profileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.user.model.UserData
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import com.yugentech.sessions.ui.dash.profileScreen.components.EmptySessionsCard
import com.yugentech.sessions.ui.dash.profileScreen.components.ProfileCard
import com.yugentech.sessions.ui.dash.profileScreen.components.SessionHistoryItem
import com.yugentech.sessions.ui.dash.util.dateHeader
import com.yugentech.sessions.ui.dash.util.formatTime
import com.yugentech.sessions.ui.dash.util.monthlyHeader
import com.yugentech.sessions.ui.dash.util.weeklyHeader
import com.yugentech.sessions.utils.AppConstants.EMPTY
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SessionSortOption
import com.yugentech.sessions.alerts.viewmodel.AlertsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileScreen(
    userId: String,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel,
    alertsViewModel: AlertsViewModel = koinViewModel(),
    onEditProfile: () -> Unit = {},
    onViewInsights: () -> Unit = {}
) {
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var sessionToDeleteId by remember { mutableStateOf<String?>(null) }

    var showGroupDeleteDialog by remember { mutableStateOf(false) }
    var groupDeleteTitle by remember { mutableStateOf("") }
    var sessionsToGroupDelete by remember { mutableStateOf<List<String>>(emptyList()) }

    val layoutDirection = LocalLayoutDirection.current
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val view = LocalView.current

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }

    val groupedSessions = remember(profileUiState.sessions, profileUiState.sortOption) {
        profileUiState.sessions.groupBy { session ->
            when (profileUiState.sortOption) {
                SessionSortOption.DAILY -> dateHeader(session.timestamp)
                SessionSortOption.WEEKLY -> weeklyHeader(session.timestamp)
                SessionSortOption.MONTHLY -> monthlyHeader(session.timestamp)
            }
        }
    }

    if (profileUiState.sessions.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    start = MaterialTheme.spacing.m,
                    end = MaterialTheme.spacing.m,
                    bottom = MaterialTheme.spacing.s
                )
        ) {
            SectionHeader(
                title = "My Account",
                icon = Icons.Filled.Person
            )

            ProfileCard(
                userData = profileUiState.user ?: UserData(name = EMPTY),
                onEditProfile = onEditProfile,
                onViewInsights = onViewInsights,
                streakCount = profileUiState.streakCount
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                EmptySessionsCard()
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = MaterialTheme.spacing.m,
                end = MaterialTheme.spacing.m,
                bottom = MaterialTheme.spacing.s
            ),
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
                    userData = profileUiState.user ?: UserData(name = EMPTY),
                    onEditProfile = onEditProfile,
                    onViewInsights = onViewInsights,
                    streakCount = profileUiState.streakCount
                )
            }

            groupedSessions.entries.forEachIndexed { groupIndex, entry ->
                val (dateHeader, sessionsInGroup) = entry
                val totalDuration = sessionsInGroup.sumOf { it.duration.toLong() }

                item(key = "header_$groupIndex") {
                    SectionHeader(
                        title = dateHeader,
                        icon = Icons.Default.DateRange,
                        trailingText = formatTime(totalDuration),
                        onDelete = {
                            groupDeleteTitle = dateHeader
                            sessionsToGroupDelete = sessionsInGroup.map { it.sessionId }
                            showGroupDeleteDialog = true
                            alertsViewModel.performHaptic(view)
                        }
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
    if (showGroupDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showGroupDeleteDialog = false
                sessionsToGroupDelete = emptyList()
            },
            title = {
                val groupType = when (profileUiState.sortOption) {
                    SessionSortOption.DAILY -> "Day"
                    SessionSortOption.WEEKLY -> "Week"
                    SessionSortOption.MONTHLY -> "Month"
                }
                Text("Delete $groupType?", style = MaterialTheme.typography.headlineSmall)
            },
            text = {
                Text(
                    "This will delete all sessions for \"$groupDeleteTitle\". This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = RoundedCornerShape(MaterialTheme.corners.large),
            confirmButton = {
                TextButton(
                    onClick = {
                        profileViewModel.deleteSessions(sessionsToGroupDelete)
                        alertsViewModel.performHaptic(view)
                        showGroupDeleteDialog = false
                        sessionsToGroupDelete = emptyList()
                    }
                ) {
                    Text("Delete All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showGroupDeleteDialog = false
                        sessionsToGroupDelete = emptyList()
                    }
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    }

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
                        alertsViewModel.performHaptic(view)
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