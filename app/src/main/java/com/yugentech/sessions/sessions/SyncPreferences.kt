package com.yugentech.sessions.sessions

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

// Initialize DataStore with the name "sync_prefs"
val Context.syncDataStore by preferencesDataStore("sync_prefs")

object SyncPrefsKeys {
    val USER_FETCH_DONE = booleanPreferencesKey("user_fetch_done")
    val SESSIONS_FETCH_DONE = booleanPreferencesKey("sessions_fetch_done")
}

class SyncPreferences(private val context: Context) {

    // Stream checking if user profile data has been fetched
    fun isUserFetchDone(): Flow<Boolean> {
        return context.syncDataStore.data.map { prefs ->
            prefs[SyncPrefsKeys.USER_FETCH_DONE] ?: false
        }
    }

    // Stream checking if session history has been fetched
    fun isSessionsFetchDone(): Flow<Boolean> {
        return context.syncDataStore.data.map { prefs ->
            prefs[SyncPrefsKeys.SESSIONS_FETCH_DONE] ?: false
        }
    }

    // Combined check to ensure all initial data syncing is complete
    fun isAllInitialFetchDone(): Flow<Boolean> {
        return context.syncDataStore.data.map { prefs ->
            val userDone = prefs[SyncPrefsKeys.USER_FETCH_DONE] ?: false
            val sessionsDone = prefs[SyncPrefsKeys.SESSIONS_FETCH_DONE] ?: false
            userDone && sessionsDone
        }
    }

    // Save flag indicating user profile fetch is complete
    suspend fun setUserFetchDone(done: Boolean) {
        Timber.d("Setting user fetch done: $done")
        context.syncDataStore.edit { prefs ->
            prefs[SyncPrefsKeys.USER_FETCH_DONE] = done
        }
    }

    // Save flag indicating session history fetch is complete
    suspend fun setSessionsFetchDone(done: Boolean) {
        Timber.d("Setting sessions fetch done: $done")
        context.syncDataStore.edit { prefs ->
            prefs[SyncPrefsKeys.SESSIONS_FETCH_DONE] = done
        }
    }

    // Reset all flags to false (used on logout)
    suspend fun clearSyncFlags() {
        Timber.d("Clearing all sync flags due to logout")
        context.syncDataStore.edit { prefs ->
            prefs[SyncPrefsKeys.USER_FETCH_DONE] = false
            prefs[SyncPrefsKeys.SESSIONS_FETCH_DONE] = false
        }
    }
}