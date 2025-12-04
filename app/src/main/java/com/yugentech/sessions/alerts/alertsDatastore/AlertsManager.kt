package com.yugentech.sessions.alerts.alertsDatastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

// Manages the persistence of alert settings using DataStore
class AlertsManager(
    private val dataStore: DataStore<Preferences>
) {
    private val soundKey = booleanPreferencesKey("sound_enabled")
    private val hapticsKey = booleanPreferencesKey("haptics_enabled")

    // Exposes configuration flow with default values if DataStore is empty
    val alertConfiguration: Flow<AlertsConfiguration> = dataStore.data
        .catch {
            Timber.e(it, "Error reading alert preferences")
            emit(emptyPreferences())
        }
        .map {
            AlertsConfiguration(
                soundEnabled = it[soundKey] ?: true,
                hapticsEnabled = it[hapticsKey] ?: true
            )
        }

    // Updates the sound preference and logs the change
    suspend fun setSoundEnabled(enabled: Boolean) {
        Timber.d("Updating sound preference: $enabled")
        dataStore.edit {
            it[soundKey] = enabled
        }
    }

    // Updates the haptics preference and logs the change
    suspend fun setHapticsEnabled(enabled: Boolean) {
        Timber.d("Updating haptics preference: $enabled")
        dataStore.edit {
            it[hapticsKey] = enabled
        }
    }
}