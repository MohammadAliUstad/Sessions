package com.yugentech.sessions.timer.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yugentech.sessions.timer.config.TimerConfig
import com.yugentech.sessions.utils.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException

class TimerDatastore(
    private val dataStore: DataStore<Preferences>
) {
    // Define unique keys for storing each piece of timer configuration
    private object Keys {
        val FOCUS_DURATION = intPreferencesKey("focus_duration")
        val SHORT_BREAK = intPreferencesKey("short_break_duration")
        val LONG_BREAK = intPreferencesKey("long_break_duration")
        val TARGET_SETS = intPreferencesKey("target_sets")
        val SESSION_TASK = stringPreferencesKey("session_task")
        val ACTIVE_SOUND = stringPreferencesKey("active_background_sound_id")
        val IS_AMBIENT_ENABLED = booleanPreferencesKey("is_ambient_enabled")
    }

    // Continuously observe DataStore and emit the latest TimerConfig
    val timerConfig: Flow<TimerConfig> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception, "Error reading timer preferences")
            } else {
                throw exception
            }
        }
        .map { prefs ->
            // Map raw preferences to the domain object, using defaults where needed
            TimerConfig(
                focusDuration = prefs[Keys.FOCUS_DURATION] ?: 25,
                shortBreakDuration = prefs[Keys.SHORT_BREAK] ?: 5,
                longBreakDuration = prefs[Keys.LONG_BREAK] ?: 15,
                targetSets = prefs[Keys.TARGET_SETS] ?: 1,
                sessionTask = prefs[Keys.SESSION_TASK] ?: AppConstants.EMPTY,
                activeBackgroundSoundId = prefs[Keys.ACTIVE_SOUND],
                isAmbientEnabled = prefs[Keys.IS_AMBIENT_ENABLED] ?: true
            )
        }

    suspend fun updateSessionTask(task: String) {
        try {
            dataStore.edit { prefs ->
                prefs[Keys.SESSION_TASK] = task
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update session task")
        }
    }

    suspend fun updateFocusDuration(duration: Int) {
        try {
            dataStore.edit { prefs ->
                prefs[Keys.FOCUS_DURATION] = duration
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update focus duration")
        }
    }

    suspend fun updateShortBreakDuration(duration: Int) {
        try {
            dataStore.edit { prefs ->
                prefs[Keys.SHORT_BREAK] = duration
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update short break duration")
        }
    }

    suspend fun updateLongBreakAndTargetSets(duration: Int, sets: Int) {
        try {
            dataStore.edit { prefs ->
                prefs[Keys.LONG_BREAK] = duration
                prefs[Keys.TARGET_SETS] = sets
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update long break duration and target sets")
        }
    }

    suspend fun updateActiveBackgroundSound(soundId: String?) {
        try {
            dataStore.edit { prefs ->
                prefs[Keys.ACTIVE_SOUND] = soundId ?: AppConstants.EMPTY
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update active background sound")
        }
    }

    suspend fun toggleAmbientSound(enabled: Boolean) {
        try {
            dataStore.edit { prefs ->
                prefs[Keys.IS_AMBIENT_ENABLED] = enabled
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to toggle ambient sound")
        }
    }
}