package com.yugentech.sessions.ui.config.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsCard
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsNavigationItem
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsSectionHeader
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsToggleItem
import com.yugentech.sessions.ui.config.components.settingsScreen.SettingsToggleItemWithTimePicker
import com.yugentech.sessions.ui.config.components.settingsScreen.TimePickerDialog
import com.yugentech.sessions.viewModels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    notificationsViewModel: NotificationsViewModel,
    onNavigateBack: () -> Unit,
    onAbout: () -> Unit,
    onAppearance: () -> Unit,
) {
    val alertConfig by settingsViewModel.alertConfig.collectAsState()
    val notificationConfig by notificationsViewModel.notificationConfig.collectAsState()
    val showPermissionDialog by notificationsViewModel.showExactAlarmDialog.collectAsStateWithLifecycle()

    var showTimePickerDialog by remember { mutableStateOf(false) }
    val view = LocalView.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = MaterialTheme.spacing.m),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l),
            contentPadding = PaddingValues(vertical = MaterialTheme.spacing.l)
        ) {
            item {
                SettingsCard {
                    SettingsSectionHeader(
                        icon = Icons.Default.Notifications,
                        title = "Notifications"
                    )

                    SettingsToggleItem(
                        title = "Enable Notifications",
                        subtitle = "Allow Sessions to send you notifications",
                        checked = notificationConfig.notificationsEnabled,
                        onCheckedChange = { enabled ->
                            notificationsViewModel.setNotificationsEnabled(enabled)
                            settingsViewModel.performHaptic(view)
                        }
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

                    SettingsToggleItemWithTimePicker(
                        title = "Focus Reminders",
                        subtitle = notificationsViewModel.formatReminderTime(),
                        checked = notificationConfig.focusRemindersEnabled,
                        enabled = notificationConfig.notificationsEnabled,
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
                        onTitleClick = {
                            if (notificationConfig.notificationsEnabled) {
                                if (notificationsViewModel.canEnableReminders()) {
                                    showTimePickerDialog = true
                                }
                            }
                        }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsSectionHeader(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        title = "Audio & Haptics"
                    )

                    SettingsToggleItem(
                        title = "Sound Effects",
                        subtitle = "Play sounds for timer events and interactions",
                        checked = alertConfig.soundEnabled,
                        onCheckedChange = {
                            settingsViewModel.setSoundEnabled(!alertConfig.soundEnabled)
                            settingsViewModel.performHaptic(view)
                        }
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

                    SettingsToggleItem(
                        title = "Haptic Feedback",
                        subtitle = "Feel vibrations for timer events",
                        checked = alertConfig.hapticsEnabled,
                        onCheckedChange = {
                            settingsViewModel.setHapticsEnabled(!alertConfig.hapticsEnabled)
                            settingsViewModel.performHaptic(view)
                        }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsSectionHeader(
                        icon = Icons.Default.Palette,
                        title = "Appearance"
                    )

                    SettingsNavigationItem(
                        title = "Theme & Colors",
                        subtitle = "Customize your app's look and feel",
                        onClick = onAppearance
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsSectionHeader(
                        icon = Icons.Default.Info,
                        title = "About"
                    )

                    SettingsNavigationItem(
                        title = "About Sessions",
                        subtitle = "Learn more about the app and developer",
                        onClick = onAbout
                    )
                }
            }
        }

        if (showTimePickerDialog) {
            TimePickerDialog(
                initialHour = notificationConfig.reminderTimeHour,
                initialMinute = notificationConfig.reminderTimeMinute,
                onTimeSelected = { hour, minute ->
                    notificationsViewModel.setReminderTime(hour, minute)
                    settingsViewModel.performHaptic(view)
                    showTimePickerDialog = false
                },
                onDismiss = {
                    showTimePickerDialog = false
                }
            )
        }

        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { notificationsViewModel.dismissDialog() },
                title = {
                    Text(text = "Permission Required")
                },
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
                                android.net.Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        }
                    ) {
                        Text(text = "Go to Settings")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { notificationsViewModel.dismissDialog() }
                    ) {
                        Text(text = "Cancel")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}