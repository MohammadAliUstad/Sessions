package com.yugentech.sessions.ui.dash.settingsScreen

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.yugentech.sessions.auth.guest.GuestBackupViewModel
import com.yugentech.sessions.ui.dash.homeScreen.components.BackupResultDialog
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.yugentech.sessions.localization.AppLocale
import com.yugentech.sessions.notification.viewmodel.NotificationViewModel
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.homeScreen.components.ExitConfirmationDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.LogoutConfirmationDialog
import com.yugentech.sessions.ui.dash.homeScreen.components.ExitGuestDialog
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import com.yugentech.sessions.alerts.viewmodel.AlertsViewModel
import com.yugentech.sessions.ui.config.aboutScreen.MoreFromUsSheet
import com.yugentech.sessions.ui.dash.settingsScreen.components.AlarmPermissionDialog
import com.yugentech.sessions.ui.dash.settingsScreen.components.NotificationPermissionDialog
import com.yugentech.sessions.ui.dash.settingsScreen.components.areNotificationsAllowed
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
    isGuest: Boolean = false,
    onAbout: () -> Unit,
    onWhatsNew: () -> Unit,
    onMoreApps: () -> Unit,
    onAppearance: () -> Unit,
    onHomeCustomization: () -> Unit = {},
    onLanguage: () -> Unit = {},
    onReplayOnboarding: () -> Unit = {},
    guestBackupViewModel: GuestBackupViewModel = koinViewModel()
) {
    val alertsConfiguration by alertsViewModel.alertConfigurations.collectAsState()
    val notificationConfig by notificationViewModel.notificationConfig.collectAsState()
    val showPermissionDialog by notificationViewModel.showExactAlarmDialog.collectAsStateWithLifecycle()

    var showTimePickerDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Whether Android lets Sessions post notifications. The switch only shows on when both this
    // and the saved setting are on, so a denied permission never looks enabled.
    var notificationsAllowed by remember { mutableStateOf(areNotificationsAllowed(context)) }
    var showNotificationPermissionDialog by rememberSaveable { mutableStateOf(false) }
    // Set when the user goes to the system settings to allow notifications, so coming back with
    // them allowed finishes turning the switch on.
    var awaitingNotificationPermission by rememberSaveable { mutableStateOf(false) }
    val notificationsOn = notificationConfig.notificationsEnabled && notificationsAllowed

    val haptic = LocalHapticFeedback.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationsAllowed = areNotificationsAllowed(context)
        if (granted && notificationsAllowed) {
            notificationViewModel.setNotificationsEnabled(true)
            haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
        } else {
            // Denied, or Android won't ask any more: only the settings screen can allow it now
            showNotificationPermissionDialog = true
        }
    }

    // Returning from the exact-alarm settings screen with the permission granted picks up
    // where the user left off (opens the time picker) instead of needing another tap.
    // Notifications may also have been allowed or blocked from the system settings meanwhile.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        notificationsAllowed = areNotificationsAllowed(context)
        if (awaitingNotificationPermission) {
            awaitingNotificationPermission = false
            if (notificationsAllowed) notificationViewModel.setNotificationsEnabled(true)
        }
        if (notificationViewModel.onReturnedFromSettings()) {
            showTimePickerDialog = true
        }
    }

    // A guest's backup file: saved where they choose, and read back from any file they pick
    // (file managers don't all call .json files JSON, hence the wider list)
    val backupState by guestBackupViewModel.state.collectAsStateWithLifecycle()
    // Set by "Export and exit" in the exit-guest dialog: once the file is saved, guest mode ends.
    // Cancelling the save or a failed export keeps them in guest mode. Plain remember, not
    // rememberSaveable: the tabs restore saved state, so leaving Settings mid-export would bring
    // this back later and sign them out by surprise. Dropping it there just shows "Backup saved".
    var exitAfterExport by remember { mutableStateOf(false) }
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) guestBackupViewModel.export(uri) else exitAfterExport = false
    }
    LaunchedEffect(backupState.result, exitAfterExport) {
        if (!exitAfterExport) return@LaunchedEffect
        when (backupState.result) {
            GuestBackupViewModel.Result.Exported -> {
                exitAfterExport = false
                guestBackupViewModel.dismissResult()
                onSignOut()
            }
            GuestBackupViewModel.Result.ExportFailed -> exitAfterExport = false
            else -> {}
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let(guestBackupViewModel::import) }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showMoreApps by rememberSaveable { mutableStateOf(false) }

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
        // A guest's sessions live only on this phone, so the way to keep them comes first:
        // a backup file they can export and import
        if (isGuest) {
            item {
                SectionHeader(
                    icon = Icons.Default.Backup,
                    title = stringResource(R.string.guest_backup_section)
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.guest_export_title),
                    subtitle = stringResource(R.string.guest_export_subtitle),
                    index = 0,
                    totalCount = 2,
                    onClick = {
                        if (!backupState.busy) {
                            val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                            exportLauncher.launch("sessions-backup-$date.json")
                        }
                    }
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.guest_import_title),
                    subtitle = stringResource(R.string.guest_import_subtitle),
                    index = 1,
                    totalCount = 2,
                    onClick = {
                        if (!backupState.busy) {
                            importLauncher.launch(arrayOf("application/json", "text/plain", "application/octet-stream"))
                        }
                    }
                )
            }
        }
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
                checked = notificationsOn,
                checkedIcon = Icons.Default.Notifications,
                index = 0,
                totalCount = 3,
                onCheckedChange = { enabled ->
                    when {
                        !enabled -> {
                            notificationViewModel.setNotificationsEnabled(false)
                            haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                        }
                        notificationsAllowed -> {
                            notificationViewModel.setNotificationsEnabled(true)
                            haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                        }
                        // Android 13+ can still ask; if it won't, the result shows the settings dialog
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        // Older versions have no prompt: notifications were turned off in the settings
                        else -> showNotificationPermissionDialog = true
                    }
                }
            )
        }
        item {
            SettingsSwitchItem(
                title = stringResource(R.string.settings_daily_reminder),
                subtitle = notificationViewModel.formatReminderTime(context),
                checked = notificationConfig.dailyReminderEnabled,
                enabled = notificationsOn,
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
                    if (notificationsOn && notificationViewModel.canEnableReminders()) {
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
                enabled = notificationsOn,
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

        // A signed-in user can bring in a backup they saved as a guest; it then syncs to their account
        if (!isGuest) {
            item {
                SectionHeader(
                    icon = Icons.Default.Backup,
                    title = stringResource(R.string.guest_backup_section)
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.guest_import_title),
                    subtitle = stringResource(R.string.account_import_subtitle),
                    index = 0,
                    totalCount = 1,
                    onClick = {
                        if (!backupState.busy) {
                            importLauncher.launch(arrayOf("application/json", "text/plain", "application/octet-stream"))
                        }
                    }
                )
            }
        }

        item {
            SectionHeader(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = stringResource(R.string.settings_section_session)
            )
        }
        // For a guest, signing out becomes leaving guest mode, which says what it costs
        val sessionCount = 2
        item {
            SettingsListItem(
                title = stringResource(R.string.settings_exit),
                subtitle = stringResource(R.string.settings_exit_subtitle),
                index = 0,
                totalCount = sessionCount,
                onClick = { showExitDialog = true }
            )
        }
        item {
            SettingsListItem(
                title = stringResource(if (isGuest) R.string.guest_exit_title else R.string.settings_sign_out),
                subtitle = stringResource(if (isGuest) R.string.guest_exit_subtitle else R.string.settings_sign_out_subtitle),
                index = sessionCount - 1,
                totalCount = sessionCount,
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

    if (showLogoutDialog && isGuest) {
        ExitGuestDialog(
            onConfirm = {
                showLogoutDialog = false
                onSignOut()
            },
            onExportAndExit = {
                showLogoutDialog = false
                if (!backupState.busy) {
                    exitAfterExport = true
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                    exportLauncher.launch("sessions-backup-$date.json")
                }
            },
            onDismiss = { showLogoutDialog = false }
        )
    } else if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = onSignOut,
            onDismiss = { showLogoutDialog = false }
        )
    }

    // After "Export and exit" a successful save leads straight out, so no "Backup saved" first
    backupState.result?.takeUnless { exitAfterExport && it == GuestBackupViewModel.Result.Exported }?.let { result ->
        val dismiss = { guestBackupViewModel.dismissResult() }
        when (result) {
            GuestBackupViewModel.Result.Exported -> BackupResultDialog(
                success = true,
                title = stringResource(R.string.guest_export_done_title),
                message = stringResource(R.string.guest_export_done_message),
                onDismiss = dismiss
            )
            GuestBackupViewModel.Result.ExportFailed -> BackupResultDialog(
                success = false,
                title = stringResource(R.string.guest_export_failed_title),
                message = stringResource(R.string.guest_export_failed_message),
                onDismiss = dismiss
            )
            is GuestBackupViewModel.Result.Imported -> BackupResultDialog(
                success = true,
                title = stringResource(R.string.guest_import_done_title),
                message = stringResource(R.string.guest_import_done_message, result.sessions, result.templates),
                onDismiss = dismiss
            )
            GuestBackupViewModel.Result.ImportFailed -> BackupResultDialog(
                success = false,
                title = stringResource(R.string.guest_import_failed_title),
                message = stringResource(R.string.guest_import_failed_message),
                onDismiss = dismiss
            )
        }
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

    if (showNotificationPermissionDialog) {
        NotificationPermissionDialog(
            onDismiss = { showNotificationPermissionDialog = false },
            onGoToSettings = { awaitingNotificationPermission = true }
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