package com.yugentech.sessions.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.session.sessionRepository.SessionRepository
import com.yugentech.sessions.session.sessionUtils.TimerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SessionViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val timerManager = TimerManager(viewModelScope)

    val isStudying: StateFlow<Boolean> = timerManager.isRunning
    val currentTime: StateFlow<Int> = timerManager.currentTime

    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions.asStateFlow()

    private val _totalTime = MutableStateFlow(0L)
    val totalTime: StateFlow<Long> = _totalTime.asStateFlow()

    private val _selectedDuration = MutableStateFlow(25 * 60)
    val selectedDuration: StateFlow<Int> = _selectedDuration.asStateFlow()

    private var currentUserId = ""

    init {
        timerManager.setDuration(_selectedDuration.value)
        timerManager.setOnTimerCompleteListener {
            handleTimerCompletion()
        }
    }

    fun setUserId(id: String) {
        currentUserId = id
    }

    private fun handleTimerCompletion() {
        stopTimer()
        saveSession(currentUserId, Session(duration = _selectedDuration.value))
        updateTotalTime(currentUserId, _selectedDuration.value)
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
            saveSession(currentUserId, Session(duration = elapsed))
            updateTotalTime(currentUserId, elapsed)
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
        viewModelScope.launch {
            sessionRepository.saveSession(userId, session)
        }
    }

    private fun updateTotalTime(userId: String, additionalSeconds: Int) {
        viewModelScope.launch {
            sessionRepository.updateTotalTime(userId, additionalSeconds)
        }
    }

    fun getSessions(userId: String) {
        sessionRepository.getSessions(userId)
            .onEach { _sessions.value = it.sortedByDescending { s -> s.timestamp } }
            .launchIn(viewModelScope)
    }

    fun getTotalTime(userId: String) {
        sessionRepository.getTotalTime(userId)
            .onEach { _totalTime.value = it }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        timerManager.stop()
    }
}