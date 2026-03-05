package com.yugentech.sessions.sessions.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber

// Manages persistent storage for data sync flags
class SyncDataStore(
    private val dataStore: DataStore<Preferences>
) {
    private val userFetchDoneKey = booleanPreferencesKey("user_fetch_done")
    private val sessionsFetchDoneKey = booleanPreferencesKey("sessions_fetch_done")

    // Stream checking if user profile data has been fetched
    val isUserFetchDone: Flow<Boolean> = dataStore.data
        .catch {
            Timber.e(it, "Error reading user fetch sync flag")
            emit(emptyPreferences())
        }
        .map { prefs -> prefs[userFetchDoneKey] ?: false }

    // Stream checking if session history has been fetched
    val isSessionsFetchDone: Flow<Boolean> = dataStore.data
        .catch {
            Timber.e(it, "Error reading sessions fetch sync flag")
            emit(emptyPreferences())
        }
        .map { prefs -> prefs[sessionsFetchDoneKey] ?: false }

    // Combined check to ensure all initial data syncing is complete
    val isAllInitialFetchDone: Flow<Boolean> = combine(
        isUserFetchDone,
        isSessionsFetchDone
    ) { userDone, sessionsDone -> userDone && sessionsDone }

    // Save flag indicating user profile fetch is complete
    suspend fun setUserFetchDone(done: Boolean) {
        Timber.d("Setting user fetch done: $done")
        dataStore.edit { it[userFetchDoneKey] = done }
    }

    // Save flag indicating session history fetch is complete
    suspend fun setSessionsFetchDone(done: Boolean) {
        Timber.d("Setting sessions fetch done: $done")
        dataStore.edit { it[sessionsFetchDoneKey] = done }
    }

    // Reset all flags to false (used on logout)
    suspend fun clearSyncFlags() {
        Timber.d("Clearing all sync flags due to logout")
        dataStore.edit { prefs ->
            prefs[userFetchDoneKey] = false
            prefs[sessionsFetchDoneKey] = false
        }
    }
}