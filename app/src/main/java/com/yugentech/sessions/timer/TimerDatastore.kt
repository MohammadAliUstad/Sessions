package com.yugentech.sessions.timer

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yugentech.sessions.timer.states.TimerConfig
import com.yugentech.sessions.utils.AppConstants.EMPTY_STRING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TimerDatastore(
    private val dataStore: DataStore<Preferences>
) {

    // 1. Define Keys
    private object Keys {
        val FOCUS_DURATION = intPreferencesKey("focus_duration")
        val SHORT_BREAK = intPreferencesKey("short_break_duration")
        val LONG_BREAK = intPreferencesKey("long_break_duration")
        val TARGET_SETS = intPreferencesKey("target_sets")
        val SESSION_TASK = stringPreferencesKey("session_task")
    }

    val timerConfig: Flow<TimerConfig> = dataStore.data.map { prefs ->
        TimerConfig(
            focusDuration = prefs[Keys.FOCUS_DURATION] ?: 25,
            shortBreakDuration = prefs[Keys.SHORT_BREAK] ?: 5,
            longBreakDuration = prefs[Keys.LONG_BREAK] ?: 15,
            targetSets = prefs[Keys.TARGET_SETS] ?: 1,
            sessionTask = prefs[Keys.SESSION_TASK] ?: EMPTY_STRING,
        )
    }

    suspend fun updateFocusDuration(minutes: Int) {
        dataStore.edit { prefs -> prefs[Keys.FOCUS_DURATION] = minutes }
    }

    suspend fun updateShortBreakDuration(minutes: Int) {
        dataStore.edit { prefs -> prefs[Keys.SHORT_BREAK] = minutes }
    }

    suspend fun updateLongBreakDuration(minutes: Int) {
        dataStore.edit { prefs -> prefs[Keys.LONG_BREAK] = minutes }
    }

    suspend fun updateTargetSets(sets: Int) {
        dataStore.edit { prefs -> prefs[Keys.TARGET_SETS] = sets }
    }

    suspend fun updateSessionTask(task: String) {
        dataStore.edit { prefs -> prefs[Keys.SESSION_TASK] = task }
    }

    suspend fun saveFullConfig(config: TimerConfig) {
        dataStore.edit { prefs ->
            prefs[Keys.FOCUS_DURATION] = config.focusDuration
            prefs[Keys.SHORT_BREAK] = config.shortBreakDuration
            prefs[Keys.LONG_BREAK] = config.longBreakDuration
            prefs[Keys.TARGET_SETS] = config.targetSets
            prefs[Keys.SESSION_TASK] = config.sessionTask
        }
    }
}