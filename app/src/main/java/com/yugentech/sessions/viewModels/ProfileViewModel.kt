package com.yugentech.sessions.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import com.yugentech.sessions.user.userRepository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserData? = null,
    val sessions: List<Session> = emptyList(),
    val totalTime: Long = 0L,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val sessionsRepository: SessionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    fun loadProfile(userId: String) {
        if (currentUserId == userId) return // prevent reloading
        currentUserId = userId

        // Load user and sessions concurrently
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Observe user changes
            userRepository.getUserFlow(userId).onEach { user ->
                _uiState.update { state -> state.copy(user = user) }
            }.launchIn(viewModelScope)

            // Observe sessions changes
            sessionsRepository.getSessions(userId).onEach { sessions ->
                _uiState.update { state -> state.copy(sessions = sessions) }
            }.launchIn(viewModelScope)

            // Observe total time
            sessionsRepository.getTotalDuration(userId).onEach { total ->
                _uiState.update { state -> state.copy(totalTime = total) }
            }.launchIn(viewModelScope)

            // Fetch sessions only once
            when (val result = sessionsRepository.fetchSessionsOnce(userId)) {
                is SessionResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> Unit
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            Log.d("ProfileRepository", "Attempting to delete session: $sessionId")

            when (val result = sessionsRepository.deleteSession(sessionId)) {
                is SessionResult.Error -> {
                    Log.e("ProfileRepository", "Failed to delete session: $sessionId | Error: ${result.message}")
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> {
                    Log.d("ProfileRepository", "Session deleted successfully: $sessionId")
                }
            }

            // Optional: check current sessions after deletion
            val currentSessions = _uiState.value.sessions
            Log.d("ProfileRepository", "Current sessions in state after deletion: ${currentSessions.map { it.sessionId }}")
        }
    }

}