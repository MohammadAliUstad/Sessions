package com.yugentech.sessions.sessions

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.sessionDataStore by preferencesDataStore("session_prefs")

object SessionPrefsKeys {
    val INITIAL_FETCH_DONE = booleanPreferencesKey("initial_fetch_done")
}

class SessionPreferences(private val context: Context) {

    fun isInitialFetchDone(): Flow<Boolean> {
        return context.sessionDataStore.data.map { prefs ->
            prefs[SessionPrefsKeys.INITIAL_FETCH_DONE] ?: false
        }
    }

    suspend fun setInitialFetchDone(done: Boolean) {
        context.sessionDataStore.edit { prefs ->
            prefs[SessionPrefsKeys.INITIAL_FETCH_DONE] = done
        }
    }
}