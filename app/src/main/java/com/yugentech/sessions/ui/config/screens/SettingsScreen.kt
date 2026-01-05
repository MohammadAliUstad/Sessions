package com.yugentech.sessions.ui.config.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsListItem
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsSectionHeader
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsSwitchItem
import com.yugentech.sessions.ui.config.components.settingsScreen.TimePickerDialog
import com.yugentech.sessions.viewModels.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    notificationsViewModel: NotificationsViewModel,
    onSignOut: () -> Unit,
    onAbout: () -> Unit,
    onAppearance: () -> Unit,
) {
    val alertConfig by settingsViewModel.alertConfig.collectAsState()
    val notificationConfig by notificationsViewModel.notificationConfig.collectAsState()
    val showPermissionDialog by notificationsViewModel.showExactAlarmDialog.collectAsStateWithLifecycle()

    var showTimePickerDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val view = LocalView.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = MaterialTheme.spacing.s,
            start = MaterialTheme.spacing.m,
            end = MaterialTheme.spacing.m,
            bottom = 112.dp
        ),
        // This 2.dp spacing combined with the custom shapes creates the "Grouped" look
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // --- Notifications Group (2 Items) ---
        item {
            SettingsSectionHeader(
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
                checked = notificationConfig.focusRemindersEnabled,
                enabled = notificationConfig.notificationsEnabled,
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
                    if (notificationConfig.notificationsEnabled && notificationsViewModel.canEnableReminders()) {
                        showTimePickerDialog = true
                    }
                }
            )
        }

        // Space between groups
        item { Spacer(Modifier.height(16.dp)) }

        // --- Audio Group (2 Items) ---
        item {
            SettingsSectionHeader(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                title = "Audio & Haptics"
            )
        }
        item {
            SettingsSwitchItem(
                title = "Sound Effects",
                subtitle = "Play sounds for timer events",
                checked = alertConfig.soundEnabled,
                index = 0,
                totalCount = 2,
                onCheckedChange = {
                    settingsViewModel.setSoundEnabled(!alertConfig.soundEnabled)
                    settingsViewModel.performHaptic(view)
                }
            )
        }
        item {
            SettingsSwitchItem(
                title = "Haptic Feedback",
                subtitle = "Feel vibrations for timer events",
                checked = alertConfig.hapticsEnabled,
                index = 1,
                totalCount = 2,
                onCheckedChange = {
                    settingsViewModel.setHapticsEnabled(!alertConfig.hapticsEnabled)
                    settingsViewModel.performHaptic(view)
                }
            )
        }

        // Space between groups
        item { Spacer(Modifier.height(16.dp)) }

        // --- Appearance Group (1 Item) ---
        item {
            SettingsSectionHeader(
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

        // Space between groups
        item { Spacer(Modifier.height(16.dp)) }

        // --- About Group (2 Items) ---
        item {
            SettingsSectionHeader(
                icon = Icons.Default.Info,
                title = "About"
            )
        }
        item {
            SettingsListItem(
                title = "About Sessions",
                subtitle = "App version 1.0.0",
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

    // --- Dialogs (Unchanged) ---
    if (showTimePickerDialog) {
        TimePickerDialog(
            initialHour = notificationConfig.reminderTimeHour,
            initialMinute = notificationConfig.reminderTimeMinute,
            onTimeSelected = { hour, minute ->
                notificationsViewModel.setReminderTime(hour, minute)
                settingsViewModel.performHaptic(view)
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onSignOut()
                    }
                ) { Text("Sign Out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { notificationsViewModel.dismissDialog() },
            title = { Text(text = "Permission Required") },
            text = {
                Text(
                    text = "To ensure your study reminder rings at the exact time you set, please allow the permission 'Alarms & Reminders' in the next screen.",
                    textAlign = TextAlign.Start
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        notificationsViewModel.dismissDialog()
                        val intent = Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            "package:${context.packageName}".toUri()
                        )
                        context.startActivity(intent)
                    }
                ) { Text(text = "Go to Settings") }
            },
            dismissButton = {
                TextButton(onClick = { notificationsViewModel.dismissDialog() }) {
                    Text(text = "Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}