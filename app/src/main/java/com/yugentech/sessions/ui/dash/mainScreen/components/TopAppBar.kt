package com.yugentech.sessions.ui.dash.mainScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.FolderDelete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.navigation.screen.BottomBarScreen
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SessionSortOption
import com.yugentech.sessions.alerts.viewmodel.AlertsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    currentScreen: BottomBarScreen,
    onLogout: () -> Unit,
    onSettings: () -> Unit = {},
    profileViewModel: ProfileViewModel? = null,
    alertsViewModel: AlertsViewModel = koinViewModel(),
    onDeleteAllSessions: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    var showSortMenu by remember { mutableStateOf(false) }
    val view = LocalView.current

    val idleMessage = rememberSaveable {
        listOf(
            "Locked in!",
            "It's all you.",
            "Make it count.",
            "One step at a time.",
            "Focus on the now."
        ).random()
    }

    val (titleText, screenIcon) = when (currentScreen) {
        BottomBarScreen.Home -> idleMessage to Icons.Default.Timer
        BottomBarScreen.Profile -> "Profile" to Icons.Default.AccountCircle
        BottomBarScreen.Settings -> "Settings" to Icons.Default.Settings
    }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = screenIcon,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.icons.mediumLarge),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))

                Text(
                    text = titleText,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        windowInsets = WindowInsets.statusBars,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        actions = {
            if (currentScreen == BottomBarScreen.Profile && profileViewModel != null) {
                val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

                if (onDeleteAllSessions != null && uiState.sessions.isNotEmpty()) {
                    IconButton(onClick = {
                        onDeleteAllSessions()
                        alertsViewModel.performHaptic(view)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.FolderDelete,
                            contentDescription = "Delete All Sessions",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(onClick = { 
                    showSortMenu = true 
                    alertsViewModel.performHaptic(view)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Sort",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier.width(180.dp),
                    shape = RoundedCornerShape(24.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    SessionSortOption.entries.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                profileViewModel.updateSortOption(option)
                                alertsViewModel.performHaptic(view)
                                showSortMenu = false
                            },
                            trailingIcon = {
                                if (uiState.sortOption == option) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )

                        if (index < SessionSortOption.entries.size - 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}