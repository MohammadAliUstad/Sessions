package com.yugentech.sessions.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.user.userRepository.UserRepository
import com.yugentech.sessions.user.utils.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private var currentUserId: String? = null

    init {
        loadUser(userState.value.user?.userId ?: "")
    }

    fun loadUser(userId: String) {
        if (currentUserId == userId) return
        currentUserId = userId

        _userState.update { it.copy(isLoading = true, errorMessage = null) }

        userRepository.getUserFlow(userId)
            .onEach { user ->
                _userState.update {
                    it.copy(
                        user = user,
                        isLoading = false,
                        errorMessage = if (user == null) "User not found" else null
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