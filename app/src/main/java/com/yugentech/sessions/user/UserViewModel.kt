package com.yugentech.sessions.user

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.alerts.alertsDatastore.AlertsRepository
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.user.userRepository.UserRepository
import com.yugentech.sessions.user.utils.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository,
    private val alertsRepository: AlertsRepository
) : ViewModel() {

    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private var currentUserId: String? = null

    init {
        loadUser(userState.value.user?.userId ?: "")
    }

    fun performHaptic(view: View? = null) {
        viewModelScope.launch {
            alertsRepository.performHaptic(view)
        }
    }

    fun loadUser(userId: String) {
        if (currentUserId == userId) return
        currentUserId = userId
        _userState.update { it.copy(isLoading = true, errorMessage = null) }
        userRepository.getUserFlow(userId)
            .filterNotNull()
            .onEach { user ->
                _userState.update {
                    it.copy(
                        user = user,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .catch { e ->
                _userState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun upsertUser(userData: UserData) {
        viewModelScope.launch {
            _userState.update { it.copy(isLoading = true, errorMessage = null) }
            userRepository.upsertUser(userData)
            _userState.update { it.copy(isLoading = false) }
        }
    }

    fun syncUser() {
        val user = _userState.value.user ?: return

        viewModelScope.launch {
            _userState.update { it.copy(isLoading = true, errorMessage = null) }
            userRepository.syncUser(user)
            _userState.update { it.copy(isLoading = false) }
        }
    }
}