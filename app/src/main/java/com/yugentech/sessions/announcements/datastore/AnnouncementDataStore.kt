package com.yugentech.sessions.announcements.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Persists which announcements have been seen and the last version code acknowledged.
 */
class AnnouncementDataStore(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val SEEN_ANNOUNCEMENT_IDS = stringSetPreferencesKey("seen_announcement_ids")
        private val LAST_ACKNOWLEDGED_VERSION = intPreferencesKey("last_acknowledged_version")
    }

    val seenAnnouncementIds: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            Timber.e(exception, "Error reading seen announcement IDs")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[SEEN_ANNOUNCEMENT_IDS] ?: emptySet()
        }

    val lastAcknowledgedVersion: Flow<Int> = dataStore.data
        .catch { exception ->
            Timber.e(exception, "Error reading last acknowledged version")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[LAST_ACKNOWLEDGED_VERSION] ?: 0
        }

    suspend fun markSeen(id: String) {
        dataStore.edit { preferences ->
            val current = preferences[SEEN_ANNOUNCEMENT_IDS] ?: emptySet()
            preferences[SEEN_ANNOUNCEMENT_IDS] = current + id
        }
    }

    suspend fun markAllSeen(ids: Collection<String>, currentVersion: Int) {
        dataStore.edit { preferences ->
            val current = preferences[SEEN_ANNOUNCEMENT_IDS] ?: emptySet()
            preferences[SEEN_ANNOUNCEMENT_IDS] = current + ids
            preferences[LAST_ACKNOWLEDGED_VERSION] = currentVersion
        }
    }

    suspend fun updateLastAcknowledgedVersion(version: Int) {
        dataStore.edit { preferences ->
            preferences[LAST_ACKNOWLEDGED_VERSION] = version
        }
    }

    /** Useful for testing/debugging to reset state. */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.remove(SEEN_ANNOUNCEMENT_IDS)
            preferences.remove(LAST_ACKNOWLEDGED_VERSION)
        }
    }
}
