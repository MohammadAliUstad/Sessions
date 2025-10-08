package com.yugentech.sessions.dependencyInjection

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

class NotificationPrefsDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val CHANNELS_CREATED = booleanPreferencesKey("channels_created")
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
}