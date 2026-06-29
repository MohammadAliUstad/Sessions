package com.yugentech.sessions.user.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val IS_GUEST_MODE = booleanPreferencesKey("is_guest_mode")
        private val SESSION_SORT_OPTION = stringPreferencesKey("session_sort_option")
        private val LAST_REVIEW_PROMPT_TIME = longPreferencesKey("last_review_prompt_time")
    }

    val lastReviewPromptTime: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[LAST_REVIEW_PROMPT_TIME] ?: 0L
        }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }

    val isGuestMode: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_GUEST_MODE] ?: false
        }

    val sessionSortOption: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[SESSION_SORT_OPTION]
        }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun saveGuestMode(isGuest: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_GUEST_MODE] = isGuest
        }
    }

    suspend fun saveSessionSortOption(option: String) {
        dataStore.edit { preferences ->
            preferences[SESSION_SORT_OPTION] = option
        }
    }

    suspend fun updateLastReviewPromptTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_REVIEW_PROMPT_TIME] = time
        }
    }
}