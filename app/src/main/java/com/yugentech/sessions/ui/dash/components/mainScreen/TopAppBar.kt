package com.yugentech.sessions.ui.dash.components.mainScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    currentScreen: AppScreens,
    onLogout: () -> Unit,
    onSettings: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Determine Icon and Text based on screen
                val (screenTitle, screenIcon) = when (currentScreen) {
                    AppScreens.Home -> "Sessions" to Icons.Default.Timer
                    AppScreens.Profile -> "Profile" to Icons.Default.Person
                }

                Icon(
                    imageVector = screenIcon,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.icons.mediumLarge),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))

                Text(
                    text = screenTitle,
                    // Increased size from HeadlineSmall to HeadlineMedium
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        },
        actions = {
            when (currentScreen) {
                AppScreens.Home -> {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            modifier = Modifier.size(MaterialTheme.icons.large)
                        )
                    }
                }

                AppScreens.Profile -> {
                    IconButton(onClick = onSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(MaterialTheme.icons.large)
                        )
                    }
                }
            }
        },
        windowInsets = WindowInsets.statusBars,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = scrollBehavior
    )
}