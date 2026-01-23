package com.yugentech.sessions.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.user.userRepository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import kotlin.random.Random

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
            sessionsRepository.fetchSessionsOnce()
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
                sessionsRepository.syncSessions()
                Timber.i("Offline sessions synced")
            } catch (e: Exception) {
                Timber.e(e, "Sync failed")
            }
        }
    }

    // Call this function ONCE from your UI (e.g., via a temporary button)
    fun injectDummyData() {
        viewModelScope.launch {
            val dummyTasks = listOf(
                "Study DSA", "Resume Polish", "System Design",
                "Morning Focus", "Project Ryori", "Reading", "Meditation"
            )

            // Generate 50 random sessions over the last 30 days
            repeat(50) {
                val randomPastDays = Random.nextLong(0, 30) // 0 to 30 days ago
                val randomDurationMins = Random.nextInt(15, 120) // 15 to 120 mins

                // Calculate timestamp: Current time minus random days
                val pastTimestamp = System.currentTimeMillis() - (randomPastDays * 24 * 60 * 60 * 1000)

                val dummySession = Session(
                    sessionId = UUID.randomUUID().toString(),
                    duration = randomDurationMins * 60, // Convert mins to seconds if your app uses seconds
                    timestamp = pastTimestamp,
                    sessionTask = dummyTasks.random()
                )

                // Save to repository
                sessionsRepository.saveSession(dummySession)
            }
            Timber.d("✅ Dummy data injection complete!")
        }
    }

    fun clearError() {
        _dataState.value = _dataState.value.copy(errorMessage = null)
    }
}