package com.yugentech.sessions.ui.dash.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.yugentech.sessions.R
import com.yugentech.sessions.notification.viewmodel.NotificationViewModel
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.homeScreen.components.ExitConfirmationDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.LogoutConfirmationDialog
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import com.yugentech.sessions.alerts.viewmodel.AlertsViewModel
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.ui.config.aboutScreen.MoreFromUsSheet
import com.yugentech.sessions.ui.dash.settingsScreen.components.AlarmPermissionDialog
import com.yugentech.sessions.ui.dash.settingsScreen.components.SettingsListItem
import com.yugentech.sessions.ui.dash.settingsScreen.components.SettingsSwitchItem
import com.yugentech.sessions.ui.dash.settingsScreen.components.TimePickerDialog

@Composable
fun SettingsScreen(
    alertsViewModel: AlertsViewModel,
    notificationViewModel: NotificationViewModel,
    onSignOut: () -> Unit,
    onExit: () -> Unit,
    onAbout: () -> Unit,
    onWhatsNew: () -> Unit,
    onMoreApps: () -> Unit,
    onAppearance: () -> Unit
) {
    val alertsConfiguration by alertsViewModel.alertConfigurations.collectAsState()
    val notificationConfig by notificationViewModel.notificationConfig.collectAsState()
    val showPermissionDialog by notificationViewModel.showExactAlarmDialog.collectAsStateWithLifecycle()

    var showTimePickerDialog by remember { mutableStateOf(false) }

    // Returning from the exact-alarm settings screen with the permission granted picks up
    // where the user left off (opens the time picker) instead of needing another tap.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (notificationViewModel.onReturnedFromSettings()) {
            showTimePickerDialog = true
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showMoreApps by rememberSaveable { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = MaterialTheme.spacing.m,
            end = MaterialTheme.spacing.m,
            bottom = MaterialTheme.spacing.s
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
    ) {
        item {
            SectionHeader(
                icon = Icons.Default.Notifications,
                title = "Notifications"
            )
        }
        item {
            SettingsSwitchItem(
                title = "Enable Notifications",
                subtitle = "Allow Sessions to send you notifications",
                checked = notificationConfig.notificationsEnabled,
                index = 0,
                totalCount = 3,
                onCheckedChange = { enabled ->
                    notificationViewModel.setNotificationsEnabled(enabled)
                    haptic.performHapticFeedback(if (enabled) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                }
            )
        }
        item {
            SettingsSwitchItem(
                title = "Daily Focus Reminder",
                subtitle = notificationViewModel.formatReminderTime(),
                checked = notificationConfig.dailyReminderEnabled,
                enabled = notificationConfig.notificationsEnabled,
                index = 1,
                totalCount = 3,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        if (notificationViewModel.canEnableReminders()) {
                            showTimePickerDialog = true
                        }
                    } else {
                        notificationViewModel.setDailyReminderEnabled(false)
                        haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                    }
                },
                onClick = {
                    if (notificationConfig.notificationsEnabled && notificationViewModel.canEnableReminders()) {
                        showTimePickerDialog = true
                    }
                }
            )
        }
        item {
            SettingsSwitchItem(
                title = "Playful Reminders",
                subtitle = "Friendly nudges now and then",
                checked = notificationConfig.playfulRemindersEnabled,
                enabled = notificationConfig.notificationsEnabled,
                index = 2,
                totalCount = 3,
                onCheckedChange = { enabled ->
                    notificationViewModel.setPlayfulRemindersEnabled(enabled)
                    haptic.performHapticFeedback(if (enabled) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                }
            )
        }

        item {
            SectionHeader(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                title = "Audio & Haptics"
            )
        }
        item {
            SettingsSwitchItem(
                title = "Sound Effects",
                subtitle = "Play sounds for timer events",
                checked = alertsConfiguration.soundEnabled,
                index = 0,
                totalCount = 2,
                onCheckedChange = {
                    alertsViewModel.setSoundEnabled(!alertsConfiguration.soundEnabled)
                    haptic.performHapticFeedback(
                        if (!alertsConfiguration.soundEnabled) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                    )
                }
            )
        }
        item {
            SettingsSwitchItem(
                title = "Haptic Feedback",
                subtitle = "Feel vibrations throughout the app",
                checked = alertsConfiguration.hapticsEnabled,
                index = 1,
                totalCount = 2,
                onCheckedChange = {
                    alertsViewModel.setHapticsEnabled(!alertsConfiguration.hapticsEnabled)
                    haptic.performHapticFeedback(
                        if (!alertsConfiguration.hapticsEnabled) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                    )
                }
            )
        }

        item {
            SectionHeader(
                icon = Icons.Default.Palette,
                title = "Appearance"
            )
        }
        item {
            SettingsListItem(
                title = "Theme & Colors",
                subtitle = "Customize your app's look and feel",
                index = 0,
                totalCount = 1,
                onClick = onAppearance
            )
        }

        item {
            SectionHeader(
                icon = Icons.Default.Info,
                title = "About"
            )
        }
        item {
            SettingsListItem(
                title = "About Sessions",
                subtitle = stringResource(R.string.version),
                index = 0,
                totalCount = 3,
                onClick = onAbout
            )
        }
        item {
            SettingsListItem(
                title = "What's New",
                subtitle = "See the latest changes",
                index = 1,
                totalCount = 3,
                onClick = onWhatsNew
            )
        }
        item {
            SettingsListItem(
                title = "More from us",
                subtitle = "Explore our other apps",
                index = 2,
                totalCount = 3,
                onClick = { showMoreApps = true }
            )
        }

        item {
            SectionHeader(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Session"
            )
        }
        item {
            SettingsListItem(
                title = "Exit",
                subtitle = "Close the Sessions app",
                index = 0,
                totalCount = 2,
                onClick = { showExitDialog = true }
            )
        }
        item {
            SettingsListItem(
                title = "Sign Out",
                subtitle = "Log out of your current account",
                index = 1,
                totalCount = 2,
                onClick = { showLogoutDialog = true }
            )
        }
    }

    if (showTimePickerDialog) {
        TimePickerDialog(
            initialHour = notificationConfig.reminderHour,
            initialMinute = notificationConfig.reminderMinute,
            onTimeSelected = { hour, minute ->
                notificationViewModel.setReminderTime(hour, minute)
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = onSignOut,
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showMoreApps) {
        MoreFromUsSheet(
            onDismiss = { showMoreApps = false },
            onSeeAll = onMoreApps
        )
    }

    if (showExitDialog) {
        ExitConfirmationDialog(
            onConfirm = onExit,
            onDismiss = { showExitDialog = false }
        )
    }

    if (showPermissionDialog) {
        AlarmPermissionDialog(
            context = context,
            onDismiss = { notificationViewModel.cancelPermissionRequest() },
            onConfirm = { notificationViewModel.dismissPermissionDialog() }
        )
    }
}