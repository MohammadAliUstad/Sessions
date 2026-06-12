package com.yugentech.sessions.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.sessions.model.Session
import com.yugentech.sessions.sessions.repository.SessionsRepository
import com.yugentech.sessions.user.datastore.UserDataStore
import com.yugentech.sessions.user.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

data class HomeDataState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val shouldShowReviewDialog: Boolean = false
)

class HomeViewModel(
    private val sessionsRepository: SessionsRepository,
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _dataState = MutableStateFlow(HomeDataState())
    val dataState: StateFlow<HomeDataState> = _dataState.asStateFlow()

    private var currentUserId: String? = null

    fun initUserData(userId: String) {
        if (currentUserId == userId) return
        currentUserId = userId

        viewModelScope.launch {
            fetchUserOnce(userId)
            fetchSessionsOnce(userId)

            syncPendingSessions(userId)
        }
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
        _dataState.update { it.copy(shouldShowReviewDialog = false) }
    }

    fun onReviewPromptShown() {
        viewModelScope.launch {
            userDataStore.updateLastReviewPromptTime(System.currentTimeMillis())
            _dataState.update { it.copy(shouldShowReviewDialog = false) }
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

    fun injectDummyData() {
        viewModelScope.launch {
            // Weighted tasks — Side Hustle & Work dominate
            val weightedTasks = listOf(
                "Work" to 25,
                "Side Hustle" to 22,
                "Research" to 18,
                "Project" to 15,
                "Resume Polish" to 8,
                "System Design" to 7,
                "Reading" to 3,
                "Meditation" to 2,
            )
            val taskPool = weightedTasks.flatMap { (task, weight) -> List(weight) { task } }

            // Weighted weekdays — Mon–Thu favoured, weekends light
            val weightedDays = listOf(
                Calendar.MONDAY to 20,
                Calendar.TUESDAY to 20,
                Calendar.WEDNESDAY to 18,
                Calendar.THURSDAY to 18,
                Calendar.FRIDAY to 12,
                Calendar.SATURDAY to 7,
                Calendar.SUNDAY to 5,
            )
            val dayPool = weightedDays.flatMap { (day, weight) -> List(weight) { day } }

            repeat(300) {
                val randomDaysAgo = Random.nextInt(0, 365)
                val targetWeekday = dayPool.random()

                val calendar = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -randomDaysAgo)
                    // Nudge to the nearest target weekday (within ±3 days)
                    val currentDay = get(Calendar.DAY_OF_WEEK)
                    var diff = targetWeekday - currentDay
                    if (diff > 3) diff -= 7
                    if (diff < -3) diff += 7
                    add(Calendar.DAY_OF_YEAR, diff)

                    // Bias session times: most work happens 9am–10pm
                    val hour = when (Random.nextInt(100)) {
                        in 0..60 -> Random.nextInt(9, 22)   // 60% → productive hours
                        in 61..80 -> Random.nextInt(22, 24) // 20% → late night
                        else -> Random.nextInt(6, 9)         // 20% → early morning
                    }
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, Random.nextInt(0, 60))
                }

                val dummySession = Session(
                    sessionId = UUID.randomUUID().toString(),
                    duration = Random.nextInt(15, 120) * 60,
                    timestamp = calendar.timeInMillis,
                    sessionTask = taskPool.random()
                )

                sessionsRepository.saveSession(dummySession)
            }

            Timber.d("Yearly dummy data injection complete!")
        }
    }

    fun clearError() {
        _dataState.value = _dataState.value.copy(errorMessage = null)
    }
}