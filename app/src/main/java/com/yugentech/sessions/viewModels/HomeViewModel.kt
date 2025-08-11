package com.yugentech.sessions.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class HomeUiState(
    val isRunning: Boolean = false,
    val selectedDuration: Int = 25 * 60, // in seconds
    val currentTime: Int = 25 * 60, // in seconds
    val errorMessage: String? = null
)

class HomeViewModel(
    private val sessionsRepository: SessionsRepository,
    private val timerRepository: TimerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var sessionStartTime: Long? = null

    init {
        // Sync timerRepository state with UI state
        viewModelScope.launch {
            combine(
                timerRepository.isRunning,
                timerRepository.currentTime
            ) { isRunning, currentTime ->
                _uiState.value.copy(
                    isRunning = isRunning,
                    currentTime = currentTime
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }

        timerRepository.setDuration(_uiState.value.selectedDuration)

        timerRepository.onTimerComplete {
            handleTimerComplete()
        }
    }

    fun setUserId(userId: String) {
        currentUserId = userId
    }

    fun updateSelectedDuration(minutes: Int) {
        val durationInSeconds = minutes * 60
        _uiState.value = _uiState.value.copy(selectedDuration = durationInSeconds)

        // Only update timer duration if timer is NOT running
        if (!_uiState.value.isRunning) {
            timerRepository.setDuration(durationInSeconds)
        }
    }

    fun startTimer() {
        if (_uiState.value.isRunning) return

        sessionStartTime = System.currentTimeMillis()

        // Only set duration if currentTime equals selectedDuration (fresh start)
        if (_uiState.value.currentTime == _uiState.value.selectedDuration) {
            timerRepository.setDuration(_uiState.value.selectedDuration)
        }

        timerRepository.startTimer()
    }

    fun stopTimer() {
        timerRepository.stopTimer()
    }

    fun resetTimer() {
        timerRepository.resetTimer()
    }

    fun stopAndSaveSession() {
        val userId = currentUserId ?: return
        val elapsedTime = timerRepository.getElapsedTime()
        if (elapsedTime <= 0) {
            stopAndDiscardSession()
            return
        }

        viewModelScope.launch {
            val session = Session(
                sessionId = UUID.randomUUID().toString(),
                duration = elapsedTime,
                timestamp = System.currentTimeMillis()
            )

            when (sessionsRepository.saveSession(userId, session)) {
                is SessionResult.Success -> resetStudyState()
                is SessionResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = "Failed to save session")
                }
            }
        }
    }

    fun stopAndDiscardSession() {
        resetStudyState()
    }

    private fun handleTimerComplete() {
        stopAndSaveSession()
    }

    private fun resetStudyState() {
        timerRepository.stopTimer()
        timerRepository.resetTimer()
        sessionStartTime = null
    }
}
