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
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

// Wraps UI loading status and error messages
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

    // Loads user data and syncs sessions only if the user ID has changed
    fun initUserData(userId: String) {
        if (currentUserId == userId) return
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

    // Manually triggers the synchronization of offline sessions to the cloud
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

    // Generates 300 random sessions over the past year to populate the heatmap for testing
    fun injectDummyData() {
        viewModelScope.launch {
            val dummyTasks = listOf(
                "Study DSA", "Resume Polish", "System Design",
                "Morning Focus", "Project Ryori", "Reading", "Meditation"
            )

            // Generate 300 random sessions to fill up the heatmap
            repeat(300) {
                val randomDaysAgo = Random.nextInt(0, 365) // 0 to 365 days ago
                val randomDurationMins = Random.nextInt(15, 120) // 15 to 120 mins

                // Randomize the exact time of day (00:00 to 23:59)
                val randomHour = Random.nextInt(0, 24)
                val randomMinute = Random.nextInt(0, 60)

                // Use Calendar to safely calculate the timestamp
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -randomDaysAgo)
                calendar.set(Calendar.HOUR_OF_DAY, randomHour)
                calendar.set(Calendar.MINUTE, randomMinute)

                val pastTimestamp = calendar.timeInMillis

                val dummySession = Session(
                    sessionId = UUID.randomUUID().toString(),
                    duration = randomDurationMins * 60,
                    timestamp = pastTimestamp,
                    sessionTask = dummyTasks.random()
                )

                // Save to repository
                sessionsRepository.saveSession(dummySession)
            }
            Timber.d("Yearly dummy data injection complete!")
        }
    }

    fun clearError() {
        _dataState.value = _dataState.value.copy(errorMessage = null)
    }
}