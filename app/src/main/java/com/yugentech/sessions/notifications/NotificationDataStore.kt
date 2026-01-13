package com.yugentech.sessions.notifications

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yugentech.sessions.notifications.scheduled.NotificationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.Locale

// Manages persistence for notification settings and reminder times
class NotificationDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val FOCUS_REMINDER_TIME = stringPreferencesKey("focus_reminder_time")
        private val FOCUS_REMINDERS_ENABLED = booleanPreferencesKey("focus_reminders_enabled")
    }

    suspend fun getInitialConfig(): NotificationConfig {
        return try {
            val prefs = dataStore.data.first()
            mapPreferencesToConfig(prefs)
        } catch (e: Exception) {
            Timber.e(e, "Failed to read initial notification config")
            NotificationConfig()
        }
    }

    val notificationConfigFlow: Flow<NotificationConfig> = dataStore.data
        .catch {
            Timber.e(it, "Error in notification config flow")
            // Emit default config on error to keep UI stable
            emit(androidx.datastore.preferences.core.emptyPreferences())
        }
        .map { prefs -> mapPreferencesToConfig(prefs) }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        Timber.d("Setting global notifications enabled: $enabled")
        dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
            if (!enabled) {
                // If global notifications are off, disable specific reminders too
                prefs[FOCUS_REMINDERS_ENABLED] = false
            }
        }
    }

    suspend fun setFocusRemindersEnabled(enabled: Boolean) {
        Timber.d("Setting focus reminders enabled: $enabled")
        dataStore.edit { prefs ->
            prefs[FOCUS_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun setFocusReminderTime(hour: Int, minute: Int) {
        Timber.d("Saving reminder time: $hour:$minute")
        val timeString = String.format(Locale.US, "%02d:%02d", hour, minute)
        dataStore.edit { prefs ->
            prefs[FOCUS_REMINDER_TIME] = timeString
        }
    }

    suspend fun clearFocusReminderTime() {
        dataStore.edit { prefs ->
            prefs[FOCUS_REMINDERS_ENABLED] = false
        }
    }

    // Helper to parse preferences into a Config object
    private fun mapPreferencesToConfig(prefs: Preferences): NotificationConfig {
        val notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true
        val focusRemindersEnabled = prefs[FOCUS_REMINDERS_ENABLED] ?: false
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
            reminderTimeHour = hour,
            reminderTimeMinute = minute
        )
    }
}