package com.yugentech.sessions.ui.config.screens

import android.util.Log
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.notifications.NotificationsViewModel
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
    // Get state from ViewModels
    val alertConfig by settingsViewModel.alertConfig.collectAsState()
    val notificationConfig by notificationsViewModel.notificationConfig.collectAsState()

    // UI state for dialog
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val view = LocalView.current

    // Add in SettingsScreen near where the focus reminder toggle is rendered
    LaunchedEffect(notificationConfig) {
        Log.d("SettingsScreen", "Notification config updated: $notificationConfig")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Notifications Section
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

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsToggleItemWithTimePicker(
                        title = "Focus Reminders",
                        subtitle = notificationsViewModel.formatReminderTime(),
                        checked = notificationConfig.focusRemindersEnabled,
                        enabled = notificationConfig.notificationsEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                showTimePickerDialog = true
                            } else {
                                notificationsViewModel.setFocusRemindersEnabled(false)
                                settingsViewModel.performHaptic(view)
                            }
                        },
                        onTitleClick = {
                            if (notificationConfig.notificationsEnabled) {
                                showTimePickerDialog = true
                            }
                        }
                    )
                }
            }

            // Audio & Haptics Section
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

                    Spacer(modifier = Modifier.height(16.dp))

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

            // Appearance Section
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

        // Time Picker Dialog
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
    }
}