package com.yugentech.sessions.viewModels

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.authentication.authRepository.AuthRepository
import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.authentication.authUtils.AuthState
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.user.userRepository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState(isLoading = true))
    val authState: StateFlow<AuthState> = _authState

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState

    init {
        checkCurrentUser()
    }

    fun signUp(name: String, email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.signUp(name, email, password)) {
                is AuthResult.Success -> {
                    val firebaseUser = result.data
                    _userId.value = firebaseUser.uid

                    // Create initial user profile
                    val userData = UserData(
                        userId = firebaseUser.uid,
                        name = name,
                        email = email,
                        avatarId = 1
                    )

                    when (val createResult = userRepository.upsertUser(userData)) {
                        is AuthResult.Success -> {
                            _authState.value = AuthState(
                                isUserLoggedIn = true,
                                userId = firebaseUser.uid,
                                userData = userData,
                                isLoading = false
                            )
                        }

                        is AuthResult.Error -> {
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                error = "Failed to create user profile: ${createResult.message}"
                            )
                        }
                    }
                }

                is AuthResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.signIn(email, password)) {
                is AuthResult.Success -> {
                    val firebaseUser = result.data
                    _userId.value = firebaseUser.uid
                    loadUserProfile(firebaseUser.uid)
                }

                is AuthResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState(isUserLoggedIn = false)
        _userId.value = null
    }

    fun forgotPassword(email: String) {
        _forgotPasswordState.value = ForgotPasswordState.Loading

        viewModelScope.launch {
            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is AuthResult.Success -> {
                    _forgotPasswordState.value = ForgotPasswordState.Success
                }

                is AuthResult.Error -> {
                    _forgotPasswordState.value = ForgotPasswordState.Error(result.message)
                }
            }
        }
    }

    fun clearForgotPasswordState() {
        _forgotPasswordState.value = ForgotPasswordState.Idle
    }

    fun getGoogleSignInIntent(webClientId: String) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.getGoogleSignInIntent(webClientId)) {
                is AuthResult.Success -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        intent = result.data
                    )
                }

                is AuthResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.handleGoogleSignInResult(data)) {
                is AuthResult.Success -> {
                    val firebaseUser = result.data
                    _userId.value = firebaseUser.uid

                    // Load user profile
                    loadUserProfile(firebaseUser.uid)
                }

                is AuthResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            when (val result = authRepository.getCurrentUser()) {
                is AuthResult.Success -> {
                    val firebaseUser = result.data
                    _userId.value = firebaseUser.uid
                    loadUserProfile(firebaseUser.uid)
                }

                is AuthResult.Error -> {
                    _authState.value = AuthState(isUserLoggedIn = false)
                }
            }
        }
    }

    private suspend fun loadUserProfile(userId: String) {
        try {
            val localUser = userRepository.getUser(userId)
            if (localUser != null) {
                _authState.value = AuthState(
                    isUserLoggedIn = true,
                    userId = userId,
                    userData = localUser,
                    isLoading = false
                )
                userRepository.syncUser(localUser)

            } else {
                when (val authResult = authRepository.getCurrentUser()) {
                    is AuthResult.Success -> {
                        val firebaseUser = authResult.data
                        val userData = UserData(
                            userId = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "User",
                            email = firebaseUser.email ?: "",
                            avatarId = 0
                        )

                        when (val createResult = userRepository.upsertUser(userData)) {
                            is AuthResult.Success -> {
                                _authState.value = AuthState(
                                    isUserLoggedIn = true,
                                    userId = userId,
                                    userData = userData,
                                    isLoading = false
                                )
                            }

                            is AuthResult.Error -> {
                                _authState.value = _authState.value.copy(
                                    isLoading = false,
                                    error = "Failed to create user profile: ${createResult.message}"
                                )
                            }
                        }
                    }

                    is AuthResult.Error -> {
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            error = "User not found"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            _authState.value = _authState.value.copy(
                isLoading = false,
                error = "Failed to load user profile: ${e.message}"
            )
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}

// Forgot Password State
sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}