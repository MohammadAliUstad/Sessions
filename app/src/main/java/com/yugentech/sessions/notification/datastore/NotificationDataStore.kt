package com.yugentech.sessions.notification.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yugentech.sessions.notification.model.NotificationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.Locale

// Manages persistent storage for notification preferences and scheduled times
class NotificationDataStore(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val FOCUS_REMINDER_TIME = stringPreferencesKey("focus_reminder_time")
        private val FOCUS_REMINDERS_ENABLED = booleanPreferencesKey("focus_reminders_enabled")
        private val SMART_REMINDERS_ENABLED = booleanPreferencesKey("smart_reminders_enabled")
    }

    // Fetches the current configuration synchronously (useful for one-off checks like boot)
    suspend fun getInitialConfig(): NotificationConfig {
        return try {
            val prefs = dataStore.data.first()
            mapPreferencesToConfig(prefs)
        } catch (e: Exception) {
            Timber.e(e, "Failed to read initial notification config")
            NotificationConfig()
        }
    }

    // Exposes configuration as a stream for reactive UI updates
    val notificationConfigFlow: Flow<NotificationConfig> = dataStore.data
        .catch {
            Timber.e(it, "Error in notification config flow")
            // Emit default config on error to keep UI stable
            emit(emptyPreferences())
        }
        .map { prefs -> mapPreferencesToConfig(prefs) }

    // Toggles master switch for all notifications
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        Timber.d("Setting global notifications enabled: $enabled")
        dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
            if (!enabled) {
                // If global notifications are off, disable specific reminders too
                prefs[FOCUS_REMINDERS_ENABLED] = false
                prefs[SMART_REMINDERS_ENABLED] = false
            }
        }
    }

    // Toggles the specific daily reminder feature
    suspend fun setFocusRemindersEnabled(enabled: Boolean) {
        Timber.d("Setting focus reminders enabled: $enabled")
        dataStore.edit { prefs ->
            prefs[FOCUS_REMINDERS_ENABLED] = enabled
        }
    }

    // Toggles the smart random reminders feature
    suspend fun setSmartRemindersEnabled(enabled: Boolean) {
        Timber.d("Setting smart reminders enabled: $enabled")
        dataStore.edit { prefs ->
            prefs[SMART_REMINDERS_ENABLED] = enabled
        }
    }

    // Saves the specific hour and minute for the daily reminder
    suspend fun setFocusReminderTime(hour: Int, minute: Int) {
        Timber.d("Saving reminder time: $hour:$minute")
        val timeString = String.format(Locale.US, "%02d:%02d", hour, minute)
        dataStore.edit { prefs ->
            prefs[FOCUS_REMINDER_TIME] = timeString
        }
    }

    // Disables reminders effectively by turning off the flag
    suspend fun clearFocusReminderTime() {
        dataStore.edit { prefs ->
            prefs[FOCUS_REMINDERS_ENABLED] = false
        }
    }

    // Helper to convert raw DataStore preferences into a clean config object
    private fun mapPreferencesToConfig(prefs: Preferences): NotificationConfig {
        val notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true
        val focusRemindersEnabled = prefs[FOCUS_REMINDERS_ENABLED] ?: false
        val smartRemindersEnabled = prefs[SMART_REMINDERS_ENABLED] ?: false
        val timeString = prefs[FOCUS_REMINDER_TIME]

        val (hour, minute) = if (timeString != null) {
            val parts = timeString.split(":")
            try {
                Pair(parts[0].toInt(), parts[1].toInt())
            } catch (e: Exception) {
                Timber.w(e, "Failed to parse time string: $timeString")
                Pair(8, 0)
            }
        } else {
            Pair(8, 0)
        }

        return NotificationConfig(
            notificationsEnabled = notificationsEnabled,
            focusRemindersEnabled = focusRemindersEnabled,
            smartRemindersEnabled = smartRemindersEnabled,
            reminderTimeHour = hour,
            reminderTimeMinute = minute
        )
    }
}