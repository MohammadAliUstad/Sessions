package com.yugentech.sessions.soundEffects.alertDatastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class AlertRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val soundKey = booleanPreferencesKey("sound_enabled")
    private val hapticsKey = booleanPreferencesKey("haptics_enabled")

    val alertConfiguration: Flow<AlertConfiguration> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            AlertConfiguration(
                soundEnabled = it[soundKey] ?: true,
                hapticsEnabled = it[hapticsKey] ?: true
            )
        }

    suspend fun setConfig(config: AlertConfiguration) {
        dataStore.edit {
            it[soundKey] = config.soundEnabled
            it[hapticsKey] = config.hapticsEnabled
        }
    }
}