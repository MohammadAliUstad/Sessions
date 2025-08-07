package com.yugentech.sessions.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import com.yugentech.sessions.sessions.sessionsUtils.TimerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SessionsViewModel(
    private val sessionsRepository: SessionsRepository
) : ViewModel() {

    private val timerManager = TimerManager(viewModelScope)

    val isStudying: StateFlow<Boolean> = timerManager.isRunning
    val currentTime: StateFlow<Int> = timerManager.currentTime

    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions.asStateFlow()

    private val _selectedDuration = MutableStateFlow(25 * 60)
    val selectedDuration: StateFlow<Int> = _selectedDuration.asStateFlow()

    // Add UI state for operations
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

    private var currentUserId = ""

    init {
        timerManager.setDuration(_selectedDuration.value)
        timerManager.setOnTimerCompleteListener {
            handleTimerCompletion()
        }
    }

    fun setUserId(id: String) {
        currentUserId = id
        if (id.isNotEmpty()) {
            observeSessions(id)
        }
    }

    private fun handleTimerCompletion() {
        stopTimer()
        saveSession(
            currentUserId, Session(
                duration = _selectedDuration.value,
                timestamp = System.currentTimeMillis()
            )
        )
        resetTimer()
    }

    fun updateSelectedDuration(minutes: Int) {
        _selectedDuration.value = minutes * 60
        timerManager.setDuration(_selectedDuration.value)
    }

    fun startTimer() = timerManager.start()
    fun stopTimer() = timerManager.stop()
    fun resetTimer() = timerManager.reset()

    fun saveCurrentSession() {
        val elapsed = timerManager.getElapsedTime()
        if (elapsed > 0) {
            saveSession(
                currentUserId, Session(
                    duration = elapsed,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun stopAndSaveSession() {
        stopTimer()
        saveCurrentSession()
        resetTimer()
    }

    fun stopAndDiscardSession() {
        stopTimer()
        resetTimer()
    }

    private fun saveSession(userId: String, session: Session) {
        if (userId.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = sessionsRepository.saveSession(userId, session)) {
                is SessionResult.Success -> {
                    _errorMessage.value = null
                    // Sessions are automatically updated via Flow from observeSessions()
                }

                is SessionResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
            _isLoading.value = false
        }
    }

    // Updated to use the correct repository method
    private fun observeSessions(userId: String) {
        sessionsRepository.getSessionsFlow(userId)
            .onEach { sessionsList ->
                _sessions.value = sessionsList.sortedByDescending { it.timestamp }
            }
            .launchIn(viewModelScope)
    }

    // Sync sessions to Firestore
    fun syncSessions() {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _syncStatus.value = "Syncing sessions..."

            when (val result = sessionsRepository.syncSessionsToFirestore(currentUserId)) {
                is SessionResult.Success -> {
                    _syncStatus.value = "Sessions synced successfully"
                    _errorMessage.value = null
                }

                is SessionResult.Error -> {
                    _syncStatus.value = null
                    _errorMessage.value = "Sync failed: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    // Download sessions from Firestore
    fun downloadSessions() {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _syncStatus.value = "Downloading sessions..."

            when (val result = sessionsRepository.downloadSessionsFromFirestore(currentUserId)) {
                is SessionResult.Success -> {
                    _syncStatus.value = "Sessions downloaded successfully"
                    _errorMessage.value = null
                    // Sessions will be automatically updated via the Flow
                }

                is SessionResult.Error -> {
                    _syncStatus.value = null
                    _errorMessage.value = "Download failed: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    // Get sessions pending sync (for debugging/monitoring)
    fun getPendingSyncSessions() {
        viewModelScope.launch {
            val pendingSessions = sessionsRepository.getSessionsPendingSync()
            _syncStatus.value = "Pending sync: ${pendingSessions.size} sessions"
        }
    }

    // Clear error messages
    fun clearError() {
        _errorMessage.value = null
    }

    // Clear sync status
    fun clearSyncStatus() {
        _syncStatus.value = null
    }

    // Calculate total study time from current sessions
    fun getTotalStudyTime(): Long {
        return _sessions.value.sumOf { it.duration.toLong() }
    }

    // Get sessions for a specific date range (helper function)
    fun getSessionsForToday(): List<Session> {
        val today = System.currentTimeMillis()
        val startOfDay = today - (today % (24 * 60 * 60 * 1000))
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000)

        return _sessions.value.filter { session ->
            session.timestamp in startOfDay until endOfDay
        }
    }

    // Get total study time for today
    fun getTotalStudyTimeToday(): Long {
        return getSessionsForToday().sumOf { it.duration.toLong() }
    }

    override fun onCleared() {
        super.onCleared()
        timerManager.stop()
    }
}