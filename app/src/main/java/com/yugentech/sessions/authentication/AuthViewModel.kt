package com.yugentech.sessions.authentication

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.authentication.authRepository.AuthRepository
import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.authentication.authUtils.AuthState
import com.yugentech.sessions.models.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        viewModelScope.launch {
            when (val result = authRepository.getCurrentUser()) {
                is AuthResult.Success -> {
                    val user = result.data
                    _userId.value = user.uid
                    // Fetch complete user profile data from Firestore
                    loadUserProfileData(user.uid)
                }

                else -> _authState.value = AuthState(isUserLoggedIn = false)
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.signUp(name, email, password)) {
                is AuthResult.Success -> {
                    val user = result.data
                    _userId.value = user.uid
                    loadUserProfileData(user.uid)
                }

                is AuthResult.Error -> _authState.value =
                    _authState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.signIn(email, password)) {
                is AuthResult.Success -> {
                    val user = result.data
                    _userId.value = user.uid
                    loadUserProfileData(user.uid)
                }

                is AuthResult.Error -> _authState.value =
                    _authState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState(isUserLoggedIn = false)
        _userId.value = null
    }

    fun getGoogleSignInIntent(webClientId: String) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.getGoogleSignInIntent(webClientId)) {
                is AuthResult.Success -> _authState.value =
                    _authState.value.copy(isLoading = false, pendingIntent = result.data)

                is AuthResult.Error -> _authState.value =
                    _authState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.handleGoogleSignInResult(data)) {
                is AuthResult.Success -> {
                    val user = result.data
                    _userId.value = user.uid
                    loadUserProfileData(user.uid)
                }

                is AuthResult.Error -> _authState.value =
                    _authState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun updateProfile(displayName: String, avatarId: Int) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.updateProfile(displayName, avatarId)) {
                is AuthResult.Success -> {
                    val user = result.data
                    // Reload the complete user profile data after update
                    loadUserProfileData(user.uid)
                    Log.d("AuthViewModel", "Profile update successful, reloading user data")
                }

                is AuthResult.Error -> {
                    Log.e("AuthViewModel", "Profile update failed: ${result.message}")
                    _authState.value =
                        _authState.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    // NEW: Load complete user profile data from Firestore
    private suspend fun loadUserProfileData(userId: String) {
        when (val profileResult = authRepository.getUserProfileData(userId)) {
            is AuthResult.Success -> {
                val userData = profileResult.data
                _authState.value = AuthState(
                    isUserLoggedIn = true,
                    userId = userId,
                    userData = userData,
                    isLoading = false
                )
                Log.d("AuthViewModel", "User profile loaded: $userData")
            }

            is AuthResult.Error -> {
                when (val authResult = authRepository.getCurrentUser()) {
                    is AuthResult.Success -> {
                        val user = authResult.data
                        val fallbackUserData = UserData(
                            userId = user.uid,
                            username = user.displayName,
                            email = user.email,
                            avatarId = 1
                        )
                        _authState.value = AuthState(
                            isUserLoggedIn = true,
                            userId = userId,
                            userData = fallbackUserData,
                            isLoading = false
                        )
                    }

                    else -> {
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            error = "Failed to load user data"
                        )
                    }
                }
            }
        }
    }
}