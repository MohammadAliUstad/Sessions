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
        private val CHANNELS_CREATED = booleanPreferencesKey("channels_created")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val FOCUS_REMINDER_TIME = stringPreferencesKey("focus_reminder_time")
        private val FOCUS_REMINDERS_ENABLED = booleanPreferencesKey("focus_reminders_enabled")
    }

    suspend fun areChannelsCreated(): Boolean {
        val prefs = dataStore.data.first()
        return prefs[CHANNELS_CREATED] ?: false
    }

    suspend fun setChannelsCreated(created: Boolean) {
        dataStore.edit { prefs ->
            prefs[CHANNELS_CREATED] = created
        }
    }

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

    val notificationConfigFlow: Flow<NotificationConfig> = dataStore.data.map { prefs ->
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

        NotificationConfig(
            notificationsEnabled = notificationsEnabled,
            focusRemindersEnabled = focusRemindersEnabled,
            reminderTimeHour = hour,
            reminderTimeMinute = minute
        )
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled

            if (!enabled) {
                prefs[FOCUS_REMINDERS_ENABLED] = false
            }
        }
    }

    suspend fun setFocusRemindersEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[FOCUS_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun setFocusReminderTime(hour: Int, minute: Int) {
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
}