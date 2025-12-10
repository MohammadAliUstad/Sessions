package com.yugentech.sessions.sessions

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

val Context.syncDataStore by preferencesDataStore("sync_prefs")

object SyncPrefsKeys {
    val USER_FETCH_DONE = booleanPreferencesKey("user_fetch_done")
    val SESSIONS_FETCH_DONE = booleanPreferencesKey("sessions_fetch_done")
}

// Manages persistent flags for tracking initial data synchronization status
class SyncPreferences(private val context: Context) {

    // Observes whether the initial user profile fetch is complete
    fun isUserFetchDone(): Flow<Boolean> {
        return context.syncDataStore.data.map { prefs ->
            prefs[SyncPrefsKeys.USER_FETCH_DONE] ?: false
        }
    }

    // Observes whether the initial sessions history fetch is complete
    fun isSessionsFetchDone(): Flow<Boolean> {
        return context.syncDataStore.data.map { prefs ->
            prefs[SyncPrefsKeys.SESSIONS_FETCH_DONE] ?: false
        }
    }

    // Checks if both user profile and sessions have been initially synced
    fun isAllInitialFetchDone(): Flow<Boolean> {
        return context.syncDataStore.data.map { prefs ->
            val userDone = prefs[SyncPrefsKeys.USER_FETCH_DONE] ?: false
            val sessionsDone = prefs[SyncPrefsKeys.SESSIONS_FETCH_DONE] ?: false
            userDone && sessionsDone
        }
    }

    // Updates the user fetch completion flag
    suspend fun setUserFetchDone(done: Boolean) {
        Timber.d("Setting user fetch done: $done")
        context.syncDataStore.edit { prefs ->
            prefs[SyncPrefsKeys.USER_FETCH_DONE] = done
        }
    }

    // Updates the sessions fetch completion flag
    suspend fun setSessionsFetchDone(done: Boolean) {
        Timber.d("Setting sessions fetch done: $done")
        context.syncDataStore.edit { prefs ->
            prefs[SyncPrefsKeys.SESSIONS_FETCH_DONE] = done
        }
    }
}