package com.yugentech.sessions.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

        Log.d("UserViewModel", "Loading user with ID: $userId")

        userRepository.getUserFlow(userId)
            .filterNotNull()
            .onEach { user ->
                Log.d("UserViewModel", "Fetched user: $user")
                _userState.update {
                    it.copy(
                        user = user,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .catch { e ->
                Log.e("UserViewModel", "Error fetching user: ${e.message}", e)
                _userState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun upsertUser(userData: UserData) {
        viewModelScope.launch {
            Log.d("UserViewModel", "Upserting user: $userData")

            _userState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.upsertUser(userData)

            Log.d("UserViewModel", "User upsert completed for: ${userData.userId}")

            _userState.update { it.copy(isLoading = false) }
        }
    }

    fun syncUser() {
        val user = _userState.value.user ?: return

        viewModelScope.launch {
            Log.d("UserViewModel", "Syncing user: $user")

            _userState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.syncUser(user)

            Log.d("UserViewModel", "User sync completed for: ${user.userId}")

            _userState.update { it.copy(isLoading = false) }
        }
    }
}
