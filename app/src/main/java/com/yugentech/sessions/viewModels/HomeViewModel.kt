package com.yugentech.sessions.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import com.yugentech.sessions.alerts.alertsDatastore.AlertsRepository
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID

data class HomeUiState(
    val isRunning: Boolean = false,
    val selectedDuration: Int = 25 * 60,
    val currentTime: Int = 25 * 60,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val sessionsRepository: SessionsRepository,
    private val alertsRepository: AlertsRepository,
    private val timerRepository: TimerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var sessionStartTime: Long? = null

    init {
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

    fun getElapsedTime(): Int {
        return timerRepository.getElapsedTime()
    }

    fun setUserId(userId: String) {
        currentUserId = userId
    }

    fun updateSelectedDuration(minutes: Int) {
        val durationInSeconds = minutes * 60
        _uiState.value = _uiState.value.copy(selectedDuration = durationInSeconds)

        if (!_uiState.value.isRunning) {
            timerRepository.setDuration(durationInSeconds)
        }
    }

    fun startTimer() {
        if (_uiState.value.isRunning) return
        viewModelScope.launch {
            sessionStartTime = System.currentTimeMillis()
            timerRepository.startTimer()
            alertsRepository.playSessionStartAlert()
        }
    }

    fun stopTimer() {
        viewModelScope.launch {
            timerRepository.stopTimer()
            alertsRepository.playSessionStopAlert()
        }
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

            alertsRepository.playButtonTapAlert()

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