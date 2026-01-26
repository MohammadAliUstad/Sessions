package com.yugentech.sessions.viewModels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.alerts.alertsRepository.AlertsRepository
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.user.userRepository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.concurrent.TimeUnit

// Holds user profile data, calculated statistics, and UI state flags
data class ProfileUiState(
    val user: UserData? = null,
    val sessions: List<Session> = emptyList(),
    val totalTime: Long = 0L,
    val streakCount: Int = 0,
    val taskDistribution: Map<String, Int> = emptyMap(),
    val dailyVolume: Map<Int, Int> = emptyMap(),
    val peakHour: Int? = null,
    val heatmapHistory: Map<LocalDate, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val sessionsRepository: SessionsRepository,
    private val alertsRepository: AlertsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    fun loadUser(userId: String) {
        if (currentUserId == userId && uiState.value.user != null) return

        currentUserId = userId
        userRepository.getUserFlow(userId)
            .filterNotNull()
            .onEach { user ->
                _uiState.update { state -> state.copy(user = user, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun loadProfile(userId: String) {
        if (currentUserId == userId) return

        loadUser(userId)

        sessionsRepository.getSessionsFlow()
            .onEach { sessions ->
                // Aggregate total duration across all sessions
                val totalDurationSeconds = sessions.sumOf { it.duration.toLong() }

                val dailyCounts = IntArray(8)
                val hourlyCounts = IntArray(24)
                val heatmapData = mutableMapOf<LocalDate, Int>()

                sessions.forEach { session ->
                    val cal = Calendar.getInstance().apply { timeInMillis = session.timestamp }

                    // --- FIX START ---
                    // OLD: dailyCounts[cal.get(Calendar.DAY_OF_WEEK)]++ (This counted sessions)
                    // NEW: Add the session duration to the daily total
                    dailyCounts[cal.get(Calendar.DAY_OF_WEEK)] += session.duration
                    // --- FIX END ---

                    hourlyCounts[cal.get(Calendar.HOUR_OF_DAY)]++

                    val date = Instant.ofEpochMilli(session.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    heatmapData[date] = (heatmapData[date] ?: 0) + 1
                }

                _uiState.update { state ->
                    val mostActiveHourIndex = hourlyCounts.indices.maxByOrNull { hourlyCounts[it] }

                    val validPeakHour = if (mostActiveHourIndex != null && hourlyCounts[mostActiveHourIndex] > 0) {
                        mostActiveHourIndex
                    } else {
                        null
                    }

                    state.copy(
                        sessions = sessions,
                        totalTime = totalDurationSeconds,
                        streakCount = calculateStreak(sessions),
                        taskDistribution = sessions.groupBy { it.sessionTask }
                            .mapValues { entry -> entry.value.sumOf { it.duration } },
                        dailyVolume = dailyCounts.mapIndexed { index, count -> index to count }
                            .toMap(),
                        peakHour = validPeakHour,
                        heatmapHistory = heatmapData,
                        isLoading = false
                    )
                }
            }
            .catch { e -> Timber.e(e, "Error loading sessions flow") }
            .launchIn(viewModelScope)
    }

    private fun calculateStreak(sessions: List<Session>): Int {
        if (sessions.isEmpty()) return 0

        val sessionDates = sessions.map {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.distinct().sortedDescending()

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val yesterday = today - TimeUnit.DAYS.toMillis(1)

        if (sessionDates.first() < yesterday) return 0

        var currentStreak = 0
        var lastDate =
            if (sessionDates.first() == today) today else yesterday + TimeUnit.DAYS.toMillis(1)

        for (date in sessionDates) {
            if (lastDate - date <= TimeUnit.DAYS.toMillis(1)) {
                currentStreak++
                lastDate = date
            } else {
                break
            }
        }
        return currentStreak
    }

    fun upsertUser(userData: UserData) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true, errorMessage = null) }
                userRepository.upsertUser(userData)
                userRepository.syncUser(userData)
                _uiState.update { it.copy(isSaving = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteSession(userId: String, sessionId: String) {
        viewModelScope.launch {
            sessionsRepository.deleteSession(sessionId)
        }
    }

    fun performHaptic(view: View? = null) {
        viewModelScope.launch {
            alertsRepository.performHaptic(view)
        }
    }
}