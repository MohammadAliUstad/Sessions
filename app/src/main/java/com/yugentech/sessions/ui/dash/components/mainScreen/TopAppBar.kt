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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.yugentech.sessions.navigation.AppScreens
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    currentScreen: AppScreens,
    isRunning: Boolean = false,
    onLogout: () -> Unit,
    onSettings: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val idleMessage = rememberSaveable {
        listOf(
            "Ready when you are!",
            "Let's get to work.",
            "Make it count.",
            "One step at a time.",
            "Focus on the now."
        ).random()
    }

    // Determine Icon and Text based on screen & state
    val (titleText, screenIcon) = when (currentScreen) {
        AppScreens.Home -> {
            val text = if (isRunning) "Stay Focused" else idleMessage
            text to Icons.Default.Timer
        }
        AppScreens.Profile -> "Profile" to Icons.Default.Person
    }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // RESTORED: The Icon
                Icon(
                    imageVector = screenIcon,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.icons.mediumLarge),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))

                // RESTORED: HeadlineMedium style
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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