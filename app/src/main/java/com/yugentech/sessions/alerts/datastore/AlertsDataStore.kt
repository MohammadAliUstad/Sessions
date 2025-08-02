package com.yugentech.sessions.alerts.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.yugentech.sessions.alerts.model.AlertsConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

// Manages persistent storage for sound and haptic settings
class AlertsDataStore(
    private val dataStore: DataStore<Preferences>
) {
    // Exposes current settings as a stream, defaulting to true if errors occur
    val alertConfiguration: Flow<AlertsConfiguration> = dataStore.data
        .catch {
            Timber.e(it, "Error reading alert preferences")
            emit(emptyPreferences())
        }
        .map { preferences ->
            AlertsConfiguration(
                soundEnabled = preferences[SOUND_ENABLED] ?: true,
                hapticsEnabled = preferences[HAPTICS_ENABLED] ?: true
            )
        }

    // Saves the user's sound preference setting
    suspend fun setSoundEnabled(enabled: Boolean) {
        Timber.d("Updating sound preference: $enabled")
        dataStore.edit { it[SOUND_ENABLED] = enabled }
    }

    // Saves the user's haptic preference setting
    suspend fun setHapticsEnabled(enabled: Boolean) {
        Timber.d("Updating haptics preference: $enabled")
        dataStore.edit { it[HAPTICS_ENABLED] = enabled }
    }

    companion object {
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
    }
}