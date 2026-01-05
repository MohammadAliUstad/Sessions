package com.yugentech.sessions.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.user.userRepository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

// Simple state for data loading status
data class HomeDataState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val sessionsRepository: SessionsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _dataState = MutableStateFlow(HomeDataState())
    val dataState: StateFlow<HomeDataState> = _dataState.asStateFlow()

    private var currentUserId: String? = null

    // Called when the screen initializes or user logs in
    fun initUserData(userId: String) {
        if (currentUserId == userId) return // Prevent duplicate fetches
        currentUserId = userId

        viewModelScope.launch {
            // Load fresh data
            fetchUserOnce(userId)
            fetchSessionsOnce(userId)

            // Sync any offline data from previous sessions
            syncPendingSessions(userId)
        }
    }

    suspend fun fetchSessionsOnce(userId: String) {
        _dataState.value = _dataState.value.copy(isLoading = true)
        try {
            sessionsRepository.fetchSessionsOnce(userId)
            Timber.i("Sessions fetched successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error fetching sessions")
            _dataState.value = _dataState.value.copy(errorMessage = "Could not load history")
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

    // Can be called manually (Pull-to-Refresh)
    fun syncPendingSessions(userId: String? = currentUserId) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                sessionsRepository.syncSessions(uid)
                Timber.i("Offline sessions synced")
            } catch (e: Exception) {
                Timber.e(e, "Sync failed")
            }
        }
    }

    fun clearError() {
        _dataState.value = _dataState.value.copy(errorMessage = null)
    }
}