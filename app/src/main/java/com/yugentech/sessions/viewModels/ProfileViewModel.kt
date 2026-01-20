package com.yugentech.sessions.viewModels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.alerts.alertsRepository.AlertsRepository
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
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

data class ProfileUiState(
    val user: UserData? = null,
    val sessions: List<Session> = emptyList(),
    val totalTime: Long = AppConstants.LONG,
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

    // Lightweight load for editing: only fetches user details
    fun loadUser(userId: String) {
        if (currentUserId == userId && uiState.value.user != null) return

        currentUserId = userId
        Timber.d("Loading user profile for edit: $userId")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.getUserFlow(userId)
                .filterNotNull()
                .onEach { user ->
                    _uiState.update { state -> state.copy(user = user, isLoading = false) }
                }
                .catch { e ->
                    Timber.e(e, "Error fetching user flow")
                    _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
                }
                .launchIn(viewModelScope)
        }
    }

    // Heavyweight load for dashboard: fetches user + session stats
    fun loadProfile(userId: String) {
        if (currentUserId == userId) return

        Timber.d("Loading full profile with stats: $userId")
        loadUser(userId)

        viewModelScope.launch {
            sessionsRepository.getSessionsFlow()
                .onEach { sessions ->
                    _uiState.update { state -> state.copy(sessions = sessions) }
                }
                .catch { e -> Timber.e(e, "Error loading sessions flow") }
                .launchIn(viewModelScope)

            sessionsRepository.getTotalDuration()
                .onEach { total ->
                    _uiState.update { state -> state.copy(totalTime = total) }
                }
                .catch { e -> Timber.e(e, "Error loading total duration") }
                .launchIn(viewModelScope)
        }
    }

    // Saves profile changes locally and syncs to cloud
    fun upsertUser(userData: UserData) {
        viewModelScope.launch {
            Timber.i("Saving profile updates")
            try {
                _uiState.update { it.copy(isSaving = true, errorMessage = null) }

                userRepository.upsertUser(userData)
                userRepository.syncUser(userData)

                Timber.i("Profile saved successfully")
                _uiState.update { it.copy(isSaving = false) }
            } catch (e: Exception) {
                Timber.e(e, "Failed to save profile")
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Failed to save profile"
                    )
                }
            }
        }
    }

    // Deletes a specific session entry
    fun deleteSession(userId: String, sessionId: String) {
        Timber.i("Deleting session: $sessionId")
        viewModelScope.launch {
            if (sessionsRepository.deleteSession(sessionId) is SessionResult.Error) {
                Timber.e("Failed to delete session")
                _uiState.update { it.copy(errorMessage = "Failed to delete session") }
            }
        }
    }

    fun performHaptic(view: View? = null) {
        viewModelScope.launch {
            alertsRepository.performHaptic(view)
        }
    }
}