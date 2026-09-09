package com.yugentech.sessions.user.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

class UserDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val SESSION_SORT_OPTION = stringPreferencesKey("session_sort_option")
        private val LAST_REVIEW_PROMPT_TIME = longPreferencesKey("last_review_prompt_time")
    }

    val lastReviewPromptTime: Flow<Long> = dataStore.data
        .catch {
            Timber.e(it, "Error reading last review prompt time")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[LAST_REVIEW_PROMPT_TIME] ?: 0L
        }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .catch {
            Timber.e(it, "Error reading onboarding completed state")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }

    val sessionSortOption: Flow<String?> = dataStore.data
        .catch {
            Timber.e(it, "Error reading session sort option")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[SESSION_SORT_OPTION]
        }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
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