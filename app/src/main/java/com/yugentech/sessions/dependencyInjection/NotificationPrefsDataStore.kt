package com.yugentech.sessions.dependencyInjection

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yugentech.sessions.notifications.scheduled.NotificationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.util.Locale

class NotificationPrefsDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        // Existing key
        private val CHANNELS_CREATED = booleanPreferencesKey("channels_created")

        // New keys
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val FOCUS_REMINDER_TIME = stringPreferencesKey("focus_reminder_time")
        // Add explicit tracking of focus reminders enabled state
        private val FOCUS_REMINDERS_ENABLED = booleanPreferencesKey("focus_reminders_enabled")
    }

    // Existing functionality preserved
    suspend fun areChannelsCreated(): Boolean {
        val prefs = dataStore.data.first()
        return prefs[CHANNELS_CREATED] ?: false
    }

    suspend fun setChannelsCreated(created: Boolean) {
        dataStore.edit { prefs ->
            prefs[CHANNELS_CREATED] = created
        }
    }

    // Get the initial config synchronously (for initial value)
    suspend fun getInitialConfig(): NotificationConfig {
        val prefs = dataStore.data.first()

        val notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true
        val focusRemindersEnabled = prefs[FOCUS_REMINDERS_ENABLED] ?: false
        val timeString = prefs[FOCUS_REMINDER_TIME]

        val (hour, minute) = if (timeString != null) {
            val parts = timeString.split(":")
            try {
                Pair(parts[0].toInt(), parts[1].toInt())
            } catch (_: Exception) {
                Pair(8, 0)
            }
        } else {
            Pair(8, 0)
        }

        return NotificationConfig(
            notificationsEnabled = notificationsEnabled,
            focusRemindersEnabled = focusRemindersEnabled,
            reminderTimeHour = hour,
            reminderTimeMinute = minute
        )
    }

    // Get notification config as a Flow
    val notificationConfigFlow: Flow<NotificationConfig> = dataStore.data.map { prefs ->
        // Get notification enabled state (default to true)
        val notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true

        // Get focus reminder enabled state (default to false)
        val focusRemindersEnabled = prefs[FOCUS_REMINDERS_ENABLED] ?: false

        // Get focus reminder time if set
        val timeString = prefs[FOCUS_REMINDER_TIME]

        // Parse time or use default (8:00 AM)
        val (hour, minute) = if (timeString != null) {
            val parts = timeString.split(":")
            try {
                Pair(parts[0].toInt(), parts[1].toInt())
            } catch (_: Exception) {
                Pair(8, 0) // Fallback to default if parsing fails
            }
        } else {
            Pair(8, 0) // Default
        }

        // Create notification config
        NotificationConfig(
            notificationsEnabled = notificationsEnabled,
            focusRemindersEnabled = focusRemindersEnabled, // Use the explicit flag
            reminderTimeHour = hour,
            reminderTimeMinute = minute
        )
    }

    // Toggle all notifications
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled

            // If disabling all notifications, also disable focus reminders
            if (!enabled) {
                prefs[FOCUS_REMINDERS_ENABLED] = false
            }
        }
    }

    // Set focus reminders enabled/disabled explicitly
    suspend fun setFocusRemindersEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[FOCUS_REMINDERS_ENABLED] = enabled
        }
    }

    // Set focus reminder time
    suspend fun setFocusReminderTime(hour: Int, minute: Int) {
        val timeString = String.format(Locale.US, "%02d:%02d", hour, minute)
        dataStore.edit { prefs ->
            prefs[FOCUS_REMINDER_TIME] = timeString
            // Don't automatically enable reminders when setting time
            // Let the caller explicitly enable if needed
        }
    }

    // Clear focus reminder time to disable it
    suspend fun clearFocusReminderTime() {
        dataStore.edit { prefs ->
            // Don't remove the time, just disable the reminders
            // This preserves the last set time for better UX if they re-enable
            prefs[FOCUS_REMINDERS_ENABLED] = false
        }
    }
}