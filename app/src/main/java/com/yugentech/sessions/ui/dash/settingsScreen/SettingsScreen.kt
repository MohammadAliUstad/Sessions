package com.yugentech.sessions.ui.dash.settingsScreen

import com.yugentech.sessions.ui.dash.mainScreen.components.bottomBarInset
import com.yugentech.sessions.ui.dash.mainScreen.components.topBarInset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Vibration
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
import com.yugentech.sessions.localization.AppLocale
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
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    alertsViewModel: AlertsViewModel,
    notificationViewModel: NotificationViewModel,
    onSignOut: () -> Unit,
    onExit: () -> Unit,
    onAbout: () -> Unit,
    onWhatsNew: () -> Unit,
    onMoreApps: () -> Unit,
    onAppearance: () -> Unit,
    onHomeCustomization: () -> Unit = {},
    onLanguage: () -> Unit = {},
    onReplayOnboarding: () -> Unit = {}
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
            top = topBarInset,
            end = MaterialTheme.spacing.m,
            bottom = bottomBarInset + 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
    ) {
        item {
            SectionHeader(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.settings_section_notifications)
            )
        }
        item {
            SettingsSwitchItem(
                title = stringResource(R.string.settings_enable_notifications),
                subtitle = stringResource(R.string.settings_enable_notifications_subtitle),
                checked = notificationConfig.notificationsEnabled,
                checkedIcon = Icons.Default.Notifications,
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
                title = stringResource(R.string.settings_daily_reminder),
                subtitle = notificationViewModel.formatReminderTime(context),
                checked = notificationConfig.dailyReminderEnabled,
                enabled = notificationConfig.notificationsEnabled,
                checkedIcon = Icons.Default.Alarm,
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
                title = stringResource(R.string.settings_playful_reminders),
                subtitle = stringResource(R.string.settings_playful_reminders_subtitle),
                checked = notificationConfig.playfulRemindersEnabled,
                enabled = notificationConfig.notificationsEnabled,
                checkedIcon = Icons.Default.AutoAwesome,
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
                title = stringResource(R.string.settings_section_audio_haptics)
            )
        }
        item {
            SettingsSwitchItem(
                title = stringResource(R.string.settings_sound_effects),
                subtitle = stringResource(R.string.settings_sound_effects_subtitle),
                checked = alertsConfiguration.soundEnabled,
                checkedIcon = Icons.AutoMirrored.Filled.VolumeUp,
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
                title = stringResource(R.string.settings_haptic_feedback),
                subtitle = stringResource(R.string.settings_haptic_feedback_subtitle),
                checked = alertsConfiguration.hapticsEnabled,
                checkedIcon = Icons.Default.Vibration,
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
                title = stringResource(R.string.settings_section_appearance)
            )
        }
        item {
            SettingsListItem(
                title = stringResource(R.string.settings_theme_colors),
                subtitle = stringResource(R.string.settings_theme_colors_subtitle),
                index = 0,
                totalCount = 4,
                onClick = onAppearance
            )
        }
        item {
            SettingsListItem(
                title = stringResource(R.string.settings_home_cards),
                subtitle = stringResource(R.string.settings_home_cards_subtitle),
                index = 1,
                totalCount = 4,
                onClick = onHomeCustomization
            )
        }
        item {
            val context = LocalContext.current
            SettingsListItem(
                title = stringResource(R.string.language_title),
                subtitle = AppLocale.current(context)?.nativeName
                    ?: stringResource(R.string.language_system_default),
                index = 2,
                totalCount = 4,
                onClick = onLanguage
            )
        }
        item {
            SettingsSwitchItem(
                title = stringResource(R.string.settings_keep_screen_on),
                subtitle = stringResource(R.string.settings_keep_screen_on_subtitle),
                checked = alertsConfiguration.keepScreenOn,
                checkedIcon = Icons.Default.LightMode,
                index = 3,
                totalCount = 4,
                onCheckedChange = { enabled ->
                    alertsViewModel.setKeepScreenOn(enabled)
                    haptic.performHapticFeedback(
                        if (enabled) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                    )
                }
            )
        }

        item {
            SectionHeader(
                icon = Icons.Default.Info,
                title = stringResource(R.string.about)
            )
        }
        item {
            SettingsListItem(
                title = stringResource(R.string.settings_about_sessions),
                subtitle = stringResource(R.string.version),
                index = 0,
                totalCount = 4,
                onClick = onAbout
            )
        }
        item {
            SettingsListItem(
                title = stringResource(R.string.settings_whats_new),
                subtitle = stringResource(R.string.settings_whats_new_subtitle),
                index = 1,
                totalCount = 4,
                onClick = onWhatsNew
            )
        }
        item {
            SettingsListItem(
                title = stringResource(R.string.settings_replay_onboarding),
                subtitle = stringResource(R.string.settings_replay_onboarding_subtitle),
                index = 2,
                totalCount = 4,
                onClick = onReplayOnboarding
            )
        }
        item {
            SettingsListItem(
                title = stringResource(R.string.settings_more_from_us),
                subtitle = stringResource(R.string.settings_more_from_us_subtitle),
                index = 3,
                totalCount = 4,
                onClick = { showMoreApps = true }
            )
        }

        item {
            SectionHeader(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = stringResource(R.string.settings_section_session)
            )
        }
        item {
            SettingsListItem(
                title = stringResource(R.string.settings_exit),
                subtitle = stringResource(R.string.settings_exit_subtitle),
                index = 0,
                totalCount = 2,
                onClick = { showExitDialog = true }
            )
        }
        item {
            SettingsListItem(
                title = stringResource(R.string.settings_sign_out),
                subtitle = stringResource(R.string.settings_sign_out_subtitle),
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