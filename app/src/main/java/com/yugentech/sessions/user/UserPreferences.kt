package com.yugentech.sessions.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// FIX: Constructor now accepts DataStore directly, injected by Koin
class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    // Read the value directly from the injected dataStore
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false // Default is false
        }

    // Write the value
    suspend fun saveOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }
}