package com.yugentech.sessions.viewModels

import com.yugentech.sessions.R
import com.yugentech.sessions.localization.AppStrings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.sessions.repository.SessionsRepository
import com.yugentech.sessions.templates.repository.TemplateRepository
import com.yugentech.sessions.user.datastore.UserDataStore
import com.yugentech.sessions.user.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class HomeDataState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val shouldShowReviewDialog: Boolean = false
)

class HomeViewModel(
    private val sessionsRepository: SessionsRepository,
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore,
    private val templateRepository: TemplateRepository
) : ViewModel() {

    private val _dataState = MutableStateFlow(HomeDataState())
    val dataState: StateFlow<HomeDataState> = _dataState.asStateFlow()

    private var currentUserId: String? = null

    fun initUserData(userId: String) {
        if (currentUserId == userId) return
        currentUserId = userId

        viewModelScope.launch {
            fetchUserOnce(userId)
            fetchSessionsOnce()

            syncPendingSessions(userId)
        }
        // Templates: restore the user's from the cloud (a fresh install) and upload any this
        // device has that the cloud doesn't.
        viewModelScope.launch { templateRepository.syncTemplates() }
    }

    fun triggerReviewPrompt() {
        viewModelScope.launch {
            try {
                val lastPrompt = userDataStore.lastReviewPromptTime.first()
                val currentTime = System.currentTimeMillis()
                val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000L

                if (currentTime - lastPrompt >= oneWeekInMillis) {
                    // Ensure user has some sessions before asking
                    val sessions = sessionsRepository.getSessionsFlow().first()
                    if (sessions.size >= 3) {
                        Timber.i("Showing Review Dialog")
                        _dataState.update { it.copy(shouldShowReviewDialog = true) }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "triggerReviewPrompt error")
            }
        }
    }

    fun onReviewDialogDismissed() {
        viewModelScope.launch {
            userDataStore.updateLastReviewPromptTime(System.currentTimeMillis())
            _dataState.update { it.copy(shouldShowReviewDialog = false) }
        }
    }

    fun onReviewPromptShown() {
        viewModelScope.launch {
            userDataStore.updateLastReviewPromptTime(System.currentTimeMillis())
            _dataState.update { it.copy(shouldShowReviewDialog = false) }
        }
    }

    suspend fun fetchSessionsOnce() {
        _dataState.value = _dataState.value.copy(isLoading = true)
        try {
            sessionsRepository.fetchSessionsOnce()
            Timber.i("Sessions fetched successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error fetching sessions")
            _dataState.value = _dataState.value.copy(errorMessage = AppStrings.get(R.string.home_history_error))
        } finally {
            _dataState.value = _dataState.value.copy(isLoading = false)
        }
    }

    suspend fun fetchUserOnce(userId: String) {
        try {
            userRepository.fetchUserOnce(userId)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user profile")
        }
    }

    fun syncPendingSessions(userId: String? = currentUserId) {
        userId ?: return
        viewModelScope.launch {
            try {
                sessionsRepository.syncSessions()
                Timber.i("Offline sessions synced")
            } catch (e: Exception) {
                Timber.e(e, "Sync failed")
            }
        }
    }
}