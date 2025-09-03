package com.yugentech.sessions.alerts.alertsDatastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class AlertsManager(
    private val dataStore: DataStore<Preferences>
) {
    private val soundKey = booleanPreferencesKey("sound_enabled")
    private val hapticsKey = booleanPreferencesKey("haptics_enabled")

    val alertConfiguration: Flow<AlertsConfiguration> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            AlertsConfiguration(
                soundEnabled = it[soundKey] ?: true,
                hapticsEnabled = it[hapticsKey] ?: true
            )
        }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit {
            it[soundKey] = enabled
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        dataStore.edit {
            it[hapticsKey] = enabled
        }
    }
}