package com.yugentech.sessions.ui.dash.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsSectionHeader
import com.yugentech.sessions.ui.dash.components.profileScreen.EmptySessionsCard
import com.yugentech.sessions.ui.dash.components.profileScreen.ProfileInfoItem
import com.yugentech.sessions.ui.dash.components.profileScreen.SessionHistoryItem
import com.yugentech.sessions.viewModels.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    val layoutDirection = LocalLayoutDirection.current
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    // Standardized padding
    val screenContentPadding = PaddingValues(
        top = MaterialTheme.spacing.m,
        start = MaterialTheme.spacing.m + navBarPadding.calculateStartPadding(layoutDirection),
        end = MaterialTheme.spacing.m + navBarPadding.calculateEndPadding(layoutDirection),
        bottom = navBarPadding.calculateBottomPadding() + MaterialTheme.spacing.l
    )

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }

    val groupedSessions = remember(profileUiState.sessions) {
        profileUiState.sessions.groupBy { session ->
            getSmartDateHeader(session.timestamp)
        }
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
        // UNIFIED LIST: Solves the "content cut off" issue by making everything scrollable
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = screenContentPadding,
            // Use 0.dp or small spacing here, handle specific spacing with Spacers
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp)
        ) {
            // --- SECTION 1: HEADER & PROFILE ---
            item {
                SettingsSectionHeader(
                    title = "My Account",
                    icon = Icons.Filled.Person
                )
            }

            item {
                ProfileInfoItem(
                    userData = profileUiState.user ?: UserData(name = AppConstants.EMPTY_STRING),
                    totalTime = profileUiState.totalTime,
                    onEditProfile = onEditProfile
                )
            }

            item {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))
            }

            // --- SECTION 2: CONTENT (Empty OR List) ---
            if (profileUiState.sessions.isEmpty()) {
                item {
                    // Empty State is now a scrollable item.
                    // fillParentMaxHeight(0.7f) makes it take up most of the screen
                    // so it looks "centered" but can still scroll if needed.
                    Box(
                        modifier = Modifier.fillParentMaxHeight(0.7f),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptySessionsCard()
                    }
                }
            } else {
                groupedSessions.forEach { (dateHeader, sessionsInGroup) ->
                    item(key = "header_$dateHeader") {
                        SettingsSectionHeader(
                            title = dateHeader,
                            icon = Icons.Default.DateRange
                        )
                    }

                    itemsIndexed(
                        items = sessionsInGroup,
                        key = { _, session -> session.sessionId }
                    ) { index, session ->
                        // animateItem() is standard in M3 LazyLists now
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

                    item {
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.s))
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
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

// ... Date helper functions remain the same ...
private fun getSmartDateHeader(timestamp: Long): String {
    val sessionCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    val todayCalendar = Calendar.getInstance()
    val yesterdayCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        isSameDay(sessionCalendar, todayCalendar) -> "Today"
        isSameDay(sessionCalendar, yesterdayCalendar) -> "Yesterday"
        else -> {
            val formatter = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
            formatter.format(Date(timestamp))
        }
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}