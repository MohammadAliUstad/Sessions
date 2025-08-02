package com.yugentech.sessions.ui.components.mainScreen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    currentScreen: AppScreens,
    onLogout: () -> Unit,
    onSettings: () -> Unit = {}, // ✅ Added settings callback
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Text(
                text = when (currentScreen) {
                    AppScreens.Home -> "Sessions"
                    AppScreens.Profile -> "Profile"
                    Screens.Settings -> TODO()
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            when (currentScreen) {
                AppScreens.Home -> {
                    // ✅ Logout button on Home screen
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                AppScreens.Profile -> {
                    // ✅ Settings button on Profile screen
                    IconButton(onClick = onSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Screens.Settings -> TODO()
            }
        },
        windowInsets = WindowInsets.statusBars,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        scrollBehavior = scrollBehavior
    )
}