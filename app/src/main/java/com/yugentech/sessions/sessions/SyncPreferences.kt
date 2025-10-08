package com.yugentech.sessions.sessions

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.syncDataStore by preferencesDataStore("sync_prefs")

object SyncPrefsKeys {
    val USER_FETCH_DONE = booleanPreferencesKey("user_fetch_done")
    val SESSIONS_FETCH_DONE = booleanPreferencesKey("sessions_fetch_done")
}

class SyncPreferences(private val context: Context) {

    fun isUserFetchDone(): Flow<Boolean> {
        return context.syncDataStore.data.map { prefs ->
            prefs[SyncPrefsKeys.USER_FETCH_DONE] ?: false
        }
    }

    fun isSessionsFetchDone(): Flow<Boolean> {
        return context.syncDataStore.data.map { prefs ->
            prefs[SyncPrefsKeys.SESSIONS_FETCH_DONE] ?: false
        }
    }

    fun isAllInitialFetchDone(): Flow<Boolean> {
        return context.syncDataStore.data.map { prefs ->
            val userDone = prefs[SyncPrefsKeys.USER_FETCH_DONE] ?: false
            val sessionsDone = prefs[SyncPrefsKeys.SESSIONS_FETCH_DONE] ?: false
            userDone && sessionsDone
        }
    }

    suspend fun setUserFetchDone(done: Boolean) {
        context.syncDataStore.edit { prefs ->
            prefs[SyncPrefsKeys.USER_FETCH_DONE] = done
        }
    }

    suspend fun setSessionsFetchDone(done: Boolean) {
        context.syncDataStore.edit { prefs ->
            prefs[SyncPrefsKeys.SESSIONS_FETCH_DONE] = done
        }
    }
}