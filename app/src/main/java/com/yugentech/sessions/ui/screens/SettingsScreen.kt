package com.yugentech.sessions.ui.screens

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
import androidx.compose.material3.rememberTimePickerState
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
import com.yugentech.sessions.ui.components.settingsScreen.SettingsCard
import com.yugentech.sessions.ui.components.settingsScreen.SettingsNavigationItem
import com.yugentech.sessions.ui.components.settingsScreen.SettingsSectionHeader
import com.yugentech.sessions.ui.components.settingsScreen.SettingsToggleItem
import com.yugentech.sessions.ui.components.settingsScreen.SettingsToggleItemWithTimePicker
import com.yugentech.sessions.ui.components.settingsScreen.TimePickerDialog
import com.yugentech.sessions.viewModels.SettingsViewModel
import java.util.Calendar
import java.util.Locale

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
    var notificationsEnabled by remember { mutableStateOf(true) }
    var focusRemindersEnabled by remember { mutableStateOf(false) }
    var selectedReminderTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val view = LocalView.current

    val timePickerState = rememberTimePickerState(
        initialHour = selectedReminderTime?.first ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        initialMinute = selectedReminderTime?.second ?: Calendar.getInstance().get(Calendar.MINUTE),
        is24Hour = true
    )

    val ekminute = 1

    fun formatTime(hour: Int, minute: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

    fun calculateDelayMinutes(selectedTime: Pair<Int, Int>?): Long {
        if (selectedTime == null) return 0L

        val (hour, minute) = selectedTime
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        val targetCalendar = Calendar.getInstance()
        targetCalendar.set(Calendar.HOUR_OF_DAY, hour)
        targetCalendar.set(Calendar.MINUTE, minute)
        targetCalendar.set(Calendar.SECOND, 0)
        targetCalendar.set(Calendar.MILLISECOND, 0)

        // If the time has already passed today, schedule for tomorrow
        if (targetCalendar.timeInMillis <= currentTime) {
            targetCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return (targetCalendar.timeInMillis - currentTime) / (1000 * 60) // Convert to minutes
    }

    // Manage scheduling/cancelling reminder
    LaunchedEffect(focusRemindersEnabled, selectedReminderTime, notificationsEnabled) {
        if (notificationsEnabled && focusRemindersEnabled && selectedReminderTime != null) {
            val delayMinutes = calculateDelayMinutes(selectedReminderTime)
            Log.d("SettingsScreen", "Scheduling reminder for $ekminute minutes")
            notificationsViewModel.scheduleReminder(
                message = "Focus Reminder",
                delayMinutes = ekminute.toLong()
            )
        } else {
            notificationsViewModel.cancelAllReminders()
        }
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
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            notificationsEnabled = it
                            settingsViewModel.performHaptic(view)
                            // If turning off notifications, also disable focus reminders
                            if (!it) {
                                focusRemindersEnabled = false
                                selectedReminderTime = null
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsToggleItemWithTimePicker(
                        title = "Focus Reminders",
                        subtitle = if (focusRemindersEnabled && selectedReminderTime != null) {
                            "Reminder is set to ${
                                formatTime(
                                    selectedReminderTime!!.first,
                                    selectedReminderTime!!.second
                                )
                            }"
                        } else {
                            "Get notified to start your focus sessions"
                        },
                        checked = focusRemindersEnabled,
                        enabled = notificationsEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                showTimePickerDialog = true
                            } else {
                                focusRemindersEnabled = false
                                selectedReminderTime = null
                                settingsViewModel.performHaptic(view)
                            }
                        },
                        onTitleClick = {
                            if (focusRemindersEnabled && notificationsEnabled) {
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
                        onCheckedChange = { it: Boolean ->
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
                timePickerState = timePickerState,
                onConfirm = {
                    selectedReminderTime = Pair(timePickerState.hour, timePickerState.minute)
                    focusRemindersEnabled = true
                    showTimePickerDialog = false
                    settingsViewModel.performHaptic(view)
                },
                onDismiss = {
                    showTimePickerDialog = false
                }
            )
        }
    }
}