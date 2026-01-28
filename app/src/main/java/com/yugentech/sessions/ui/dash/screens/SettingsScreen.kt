package com.yugentech.sessions.ui.dash.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.common.LogoutConfirmationDialog
import com.yugentech.sessions.ui.dash.common.SectionHeader
import com.yugentech.sessions.ui.dash.components.settingsScreen.AlarmPermissionDialog
import com.yugentech.sessions.ui.dash.components.settingsScreen.SettingsListItem
import com.yugentech.sessions.ui.dash.components.settingsScreen.SettingsSwitchItem
import com.yugentech.sessions.ui.dash.components.settingsScreen.TimePickerDialog
import com.yugentech.sessions.viewModels.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    notificationsViewModel: NotificationsViewModel,
    onSignOut: () -> Unit,
    onAbout: () -> Unit,
    onAppearance: () -> Unit
) {
    val alertsConfiguration by settingsViewModel.alertConfigurations.collectAsState()
    val notificationConfiguration by notificationsViewModel.notificationConfiguration.collectAsState()
    val showPermissionDialog by notificationsViewModel.showExactAlarmDialog.collectAsStateWithLifecycle()

    var showTimePickerDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val view = LocalView.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = MaterialTheme.spacing.m,
            end = MaterialTheme.spacing.m,
            bottom = MaterialTheme.components.bottomNavHeight
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
                checked = notificationConfiguration.notificationsEnabled,
                index = 0,
                totalCount = 2,
                onCheckedChange = { enabled ->
                    notificationsViewModel.setNotificationsEnabled(enabled)
                    settingsViewModel.performHaptic(view)
                }
            )
        }
        item {
            SettingsSwitchItem(
                title = "Focus Reminders",
                subtitle = notificationsViewModel.formatReminderTime(),
                checked = notificationConfiguration.focusRemindersEnabled,
                enabled = notificationConfiguration.notificationsEnabled,
                index = 1,
                totalCount = 2,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        if (notificationsViewModel.canEnableReminders()) {
                            showTimePickerDialog = true
                        }
                    } else {
                        notificationsViewModel.setFocusRemindersEnabled(false)
                        settingsViewModel.performHaptic(view)
                    }
                },
                onClick = {
                    if (notificationConfiguration.notificationsEnabled && notificationsViewModel.canEnableReminders()) {
                        showTimePickerDialog = true
                    }
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
                    settingsViewModel.setSoundEnabled(!alertsConfiguration.soundEnabled)
                    settingsViewModel.performHaptic(view)
                }
            )
        }
        item {
            SettingsSwitchItem(
                title = "Haptic Feedback",
                subtitle = "Feel vibrations for timer events",
                checked = alertsConfiguration.hapticsEnabled,
                index = 1,
                totalCount = 2,
                onCheckedChange = {
                    settingsViewModel.setHapticsEnabled(!alertsConfiguration.hapticsEnabled)
                    settingsViewModel.performHaptic(view)
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
                totalCount = 2,
                onClick = onAbout
            )
        }
        item {
            SettingsListItem(
                title = "Sign Out",
                subtitle = "Log out of your current session",
                index = 1,
                totalCount = 2,
                onClick = { showLogoutDialog = true }
            )
        }
    }

    if (showTimePickerDialog) {
        TimePickerDialog(
            initialHour = notificationConfiguration.reminderTimeHour,
            initialMinute = notificationConfiguration.reminderTimeMinute,
            onTimeSelected = { hour, minute ->
                notificationsViewModel.setReminderTime(hour, minute)
                settingsViewModel.performHaptic(view)
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

    if (showPermissionDialog) {
        AlarmPermissionDialog(
            context = context,
            onDismiss = { notificationsViewModel.dismissDialog() },
            onConfirm = { notificationsViewModel.dismissDialog() }
        )
    }
}