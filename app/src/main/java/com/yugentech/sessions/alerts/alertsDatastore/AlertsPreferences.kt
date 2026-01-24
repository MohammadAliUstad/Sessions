package com.yugentech.sessions.alerts.alertsDatastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.yugentech.sessions.alerts.models.AlertsConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

class AlertsPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private val soundKey = booleanPreferencesKey("sound_enabled")
    private val hapticsKey = booleanPreferencesKey("haptics_enabled")

    val alertConfiguration: Flow<AlertsConfiguration> = dataStore.data
        .catch {
            Timber.e(it, "Error reading alert preferences")
            emit(emptyPreferences())
        }
        .map { preferences ->
            AlertsConfiguration(
                soundEnabled = preferences[soundKey] ?: true,
                hapticsEnabled = preferences[hapticsKey] ?: true
            )
        }

    suspend fun setSoundEnabled(enabled: Boolean) {
        Timber.d("Updating sound preference: $enabled")
        dataStore.edit { it[soundKey] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        Timber.d("Updating haptics preference: $enabled")
        dataStore.edit { it[hapticsKey] = enabled }
    }
}