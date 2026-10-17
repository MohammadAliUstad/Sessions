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
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.Locale

/** The saved reminder settings, the same in Quill and Sessions. */
class NotificationDataStore(
    private val dataStore: DataStore<Preferences>
) {

    val notificationConfigFlow: Flow<NotificationConfig> = dataStore.data
        .catch {
            Timber.e(it, "Error reading notification settings")
            emit(emptyPreferences())
        }
        .map { prefs ->
            val (hour, minute) = parseTime(prefs[REMINDER_TIME])
            NotificationConfig(
                notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true,
                dailyReminderEnabled = prefs[DAILY_REMINDER_ENABLED] ?: false,
                playfulRemindersEnabled = prefs[PLAYFUL_REMINDERS_ENABLED] ?: false,
                reminderHour = hour,
                reminderMinute = minute
            )
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setDailyReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[DAILY_REMINDER_ENABLED] = enabled }
    }

    suspend fun setPlayfulRemindersEnabled(enabled: Boolean) {
        dataStore.edit { it[PLAYFUL_REMINDERS_ENABLED] = enabled }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        dataStore.edit {
            it[REMINDER_TIME] = String.format(Locale.US, "%02d:%02d", hour, minute)
        }
    }

    // The time is saved as "HH:mm", as it always has been.
    private fun parseTime(time: String?): Pair<Int, Int> {
        if (time == null) return DEFAULT_REMINDER_HOUR to 0
        return try {
            val parts = time.split(":")
            parts[0].toInt() to parts[1].toInt()
        } catch (e: Exception) {
            Timber.w(e, "Failed to parse reminder time: $time")
            DEFAULT_REMINDER_HOUR to 0
        }
    }

    private companion object {
        // In the "notification" DataStore, under the names they've always had, so settings carry
        // over.
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("focus_reminders_enabled")
        val PLAYFUL_REMINDERS_ENABLED = booleanPreferencesKey("smart_reminders_enabled")
        val REMINDER_TIME = stringPreferencesKey("focus_reminder_time")

        const val DEFAULT_REMINDER_HOUR = 8
    }
}
