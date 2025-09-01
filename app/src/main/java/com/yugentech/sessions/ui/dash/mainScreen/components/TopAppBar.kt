package com.yugentech.sessions.ui.dash.components.mainScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.yugentech.sessions.navigation.screen.BottomBarScreen
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    currentScreen: BottomBarScreen,
    onLogout: () -> Unit,
    onSettings: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
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
        scrollBehavior = scrollBehavior
    )
}