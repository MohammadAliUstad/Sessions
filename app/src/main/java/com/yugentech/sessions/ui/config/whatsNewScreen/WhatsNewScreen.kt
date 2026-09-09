package com.yugentech.sessions.ui.config.whatsNewScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.dimensions.AppAnimations
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.itemShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsNewScreen(
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current

    val v5Updates = listOf(
        UpdateItem(
            "Task Templates",
            "Save your current timer configuration as a named template and reapply it in one tap. Create, update, and delete templates directly from the task sheet.",
            Icons.Default.Bookmark
        ),
        UpdateItem(
            "Ambient Sounds & Active Badges",
            "Rebranded background audio controls with a dedicated Ambient Sounds bottom sheet. The home header sound badge now animates dynamically in tertiary colors during active sessions.",
            Icons.AutoMirrored.Filled.VolumeUp
        ),
        UpdateItem(
            "Fully Rounded Dialog Buttons",
            "All confirmation and reward dialogs now use fully pill-shaped buttons for a more consistent, polished look throughout the app.",
            Icons.Default.CheckCircle
        ),
        UpdateItem(
            "Connected Insights Cards",
            "The Insights screen top metrics (Total Focus, Primary Focus, and Peak Productivity) are now grouped into a connected Material 3 list layout with 2dp spacing.",
            Icons.AutoMirrored.Filled.ShowChart
        ),
        UpdateItem(
            "Redesigned Settings Sheets",
            "Background sound and session goal controls are now full bottom sheets with explicit Save buttons. Changes are only applied when you choose to save them.",
            Icons.Default.Tune
        ),
        UpdateItem(
            "Removed Swipe Navigation",
            "The swipe gesture to switch between main screens has been removed. It was causing unintended navigation and interfering with horizontal interactions.",
            Icons.Default.SwipeLeft
        ),
        UpdateItem(
            "Achievements Coming Soon",
            "A new Achievements section has been added to your profile screen. Earn badges as you build your study habits — launching in a future update.",
            Icons.Default.EmojiEvents
        ),
        UpdateItem(
            "Notification Always Opens Home Screen",
            "Tapping the session notification now always navigates you straight to the home screen, even if the app was open on a different screen.",
            Icons.Default.NotificationsActive
        ),
        UpdateItem(
            "Sound System Critical Fix",
            "Fixed a bug where ambient audio would continue playing in the background after a session ended. Background sounds now stop correctly when your session is over.",
            Icons.AutoMirrored.Filled.VolumeUp
        ),
        UpdateItem(
            "Color-Coded Notification Progress",
            "On Android 16 and above, the live session notification now shows a segmented progress bar with distinct colors for focus, short break, and long break phases.",
            Icons.Default.Flag
        )
    )

    val v4Updates = listOf(
        UpdateItem(
            "Smart Focus Reminders",
            "Stay consistent with our new intelligent notification system. Sessions now checks in if you've been away too long, helping you maintain your productivity streak.",
            Icons.Default.NotificationsActive
        ),
        UpdateItem(
            "Personalized Nudges",
            "Receive playful, unique reminders based on your actual focus habits and task history, making every nudge feel personal to you.",
            Icons.Default.AutoAwesome
        ),
        UpdateItem(
            "New Skip Action",
            "Enjoy better control during your sessions. You can now skip focus or break periods directly from the notification tray.",
            Icons.Default.SmartDisplay
        ),
        UpdateItem(
            "Smoother Animations",
            "Enjoy a more polished experience with new animated icons and refined transitions across the home, settings, and profile screens.",
            Icons.Default.Celebration
        ),
        UpdateItem(
            "Live Update Notifications",
            "Track your focus journey directly from your status bar and lock screen. Pause, finish, and see your progress at a glance.",
            Icons.Default.NotificationsActive
        ),
        UpdateItem(
            "Flexible History Sorting",
            "Organize your focus history by Day, Week, or Month. View cumulative time spent for each period to better track your long-term productivity.",
            Icons.Default.History
        ),
        UpdateItem(
            "Quick Audio Toggle",
            "Instantly mute or unmute your background ambience by tapping the sound badge in the home screen header.",
            Icons.AutoMirrored.Filled.VolumeUp
        ),
        UpdateItem(
            "Smart Finish Logic",
            "New confirmation dialogs for early session exits and automatic celebrations for reaching your goal.",
            Icons.Default.Celebration
        ),
        UpdateItem(
            "More from Yugen Tech",
            "Discover our other productivity tools directly from the new 'More from us' section in the About screen.",
            Icons.Default.Apps
        )
    )

    var v4Expanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("What's New")
                        Text(
                            text = "Current Version 5.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { scaffoldPadding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding()),
            contentPadding = PaddingValues(
                bottom = navBarPadding.calculateBottomPadding(),
                start = MaterialTheme.spacing.m + scaffoldPadding.calculateStartPadding(layoutDirection),
                end = MaterialTheme.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
        ) {
            itemsIndexed(v5Updates) { index, item ->
                UpdateCard(item, index, v5Updates.size)
            }

            // 4.0.0 collapsible section
            item {
                VersionSectionHeader(
                    version = "4.0.0",
                    isLatest = false,
                    expanded = v4Expanded,
                    onClick = { v4Expanded = !v4Expanded }
                )
            }

            item(key = "v4_collapsible_content") {
                AnimatedVisibility(
                    visible = v4Expanded,
                    enter = fadeIn(tween(AppAnimations.Durations.Standard)) + expandVertically(tween(AppAnimations.Durations.Standard)),
                    exit = fadeOut(tween(AppAnimations.Durations.Standard)) + shrinkVertically(tween(AppAnimations.Durations.Standard))
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                    ) {
                        v4Updates.forEachIndexed { index, item ->
                            UpdateCard(item, index, v4Updates.size)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VersionSectionHeader(
    version: String,
    isLatest: Boolean,
    expanded: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.corners.medium))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(
                horizontal = if (onClick != null) MaterialTheme.spacing.s else 0.dp,
                vertical = MaterialTheme.spacing.m
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Version $version",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isLatest) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer { rotationZ = chevronRotation }
            )
        }
    }
}

@Composable
private fun UpdateCard(item: UpdateItem, index: Int, totalCount: Int) {
    val shape = itemShape(index, totalCount)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(MaterialTheme.spacing.m)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class UpdateItem(
    val title: String,
    val description: String,
    val icon: ImageVector
)
