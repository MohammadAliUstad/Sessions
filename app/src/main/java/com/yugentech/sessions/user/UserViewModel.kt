package com.yugentech.sessions.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.user.userRepository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<AuthResult<UserData>>(AuthResult.Error("No data yet"))
    val userState: StateFlow<AuthResult<UserData>> get() = _userState

    private val _updateStatus = MutableStateFlow<AuthResult<Unit>?>(null)
    val updateStatus: StateFlow<AuthResult<Unit>?> get() = _updateStatus

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _userState.value = AuthResult.Error("Loading...")
            val result = userRepository.getUser(userId)
            _userState.value = result
        }
    }

    fun updateUser(userId: String, userData: UserData) {
        viewModelScope.launch {
            _updateStatus.value = null // reset previous state
            val result = userRepository.updateUser(userId, userData)
            _updateStatus.value = result

            // Optionally reload updated data
            if (result is AuthResult.Success) {
                loadUser(userId)
            }
        }
    }
}
