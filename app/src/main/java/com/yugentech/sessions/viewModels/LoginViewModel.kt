package com.yugentech.sessions.viewModels

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.sessions.authentication.authRepository.AuthRepository
import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.authentication.authUtils.AuthState
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.user.UserResult
import com.yugentech.sessions.user.userRepository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "LoginViewModel"

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(
        AuthState(
            isLoading = true,
            isUserLoggedIn = false,
            userId = null,
            userData = null,
            error = null,
            intent = null
        )
    )
    val authState: StateFlow<AuthState> = _authState

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState

    init {
        checkCurrentUser()
    }

    fun signUp(name: String, email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            when (val result = authRepository.signUp(name, email, password)) {
                is AuthResult.Success -> {
                    val firebaseUser = result.data
                    _userId.value = firebaseUser.uid

                    val userData = UserData(
                        userId = firebaseUser.uid,
                        name = name,
                        email = email,
                        avatarId = 1
                    )

                    try {
                        // Save locally first
                        userRepository.upsertUser(userData)

                        // Then sync to Firestore
                        when (val syncResult = userRepository.syncUser(userData)) {
                            is UserResult.Success -> {
                                _authState.value = AuthState(
                                    isUserLoggedIn = true,
                                    userId = firebaseUser.uid,
                                    userData = userData,
                                    isLoading = false,
                                    error = null,
                                    intent = null
                                )
                            }

                            is UserResult.Error -> {
                                _authState.value = _authState.value.copy(
                                    isLoading = false,
                                    isUserLoggedIn = false,
                                    error = "Failed to sync user profile: ${syncResult.message}"
                                )
                            }

                            is UserResult.Loading -> {
                                _authState.value = _authState.value.copy(
                                    isLoading = true,
                                    error = null
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            isUserLoggedIn = false,
                            error = "Failed to create user profile: ${e.message}"
                        )
                    }
                }

                is AuthResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isUserLoggedIn = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true, error = null, intent = null)

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
                        isUserLoggedIn = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true)
            authRepository.signOut()
            _authState.value = AuthState(
                isUserLoggedIn = false,
                userId = null,
                userData = null,
                isLoading = false,
                error = null,
                intent = null
            )
            _userId.value = null
        }
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
        _authState.value = _authState.value.copy(isLoading = true, error = null)

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
        _authState.value = _authState.value.copy(isLoading = true, error = null, intent = null)

        viewModelScope.launch {
            when (val result = authRepository.handleGoogleSignInResult(data)) {
                is AuthResult.Success -> {
                    val firebaseUser = result.data
                    _userId.value = firebaseUser.uid
                    loadUserProfile(firebaseUser.uid)
                }

                is AuthResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isUserLoggedIn = false,
                        error = result.message,
                        intent = null
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
                    _authState.value = AuthState(
                        isUserLoggedIn = false,
                        userId = null,
                        userData = null,
                        isLoading = false,
                        error = null,
                        intent = null
                    )
                }
            }
        }
    }

    private suspend fun loadUserProfile(userId: String) {
        try {
            // First, try to fetch from Firestore (this also saves locally)
            Log.d(TAG, "Attempting to fetch user from Firestore: $userId")
            when (val fetchResult = userRepository.fetchUserOnce(userId)) {
                is UserResult.Success -> {
                    Log.d(TAG, "User fetched successfully, loading from local DB")
                    // Now load from local database
                    val localUser = userRepository.getUser(userId)
                    if (localUser != null) {
                        _authState.value = AuthState(
                            isUserLoggedIn = true,
                            userId = userId,
                            userData = localUser,
                            isLoading = false,
                            error = null,
                            intent = null
                        )
                    } else {
                        Log.e(TAG, "User fetched but not found in local DB")
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            isUserLoggedIn = false,
                            error = "Failed to load user profile"
                        )
                    }
                }

                is UserResult.Error -> {
                    Log.w(TAG, "User not found in Firestore, creating new profile")
                    // User doesn't exist in Firestore, create new profile
                    when (val authResult = authRepository.getCurrentUser()) {
                        is AuthResult.Success -> {
                            val firebaseUser = authResult.data
                            val userData = UserData(
                                userId = firebaseUser.uid,
                                name = firebaseUser.displayName ?: "User",
                                email = firebaseUser.email ?: "",
                                avatarId = 0
                            )

                            try {
                                // Save locally
                                userRepository.upsertUser(userData)

                                // Sync to Firestore
                                when (val syncResult = userRepository.syncUser(userData)) {
                                    is UserResult.Success -> {
                                        _authState.value = AuthState(
                                            isUserLoggedIn = true,
                                            userId = userId,
                                            userData = userData,
                                            isLoading = false,
                                            error = null,
                                            intent = null
                                        )
                                    }

                                    is UserResult.Error -> {
                                        _authState.value = _authState.value.copy(
                                            isLoading = false,
                                            isUserLoggedIn = false,
                                            error = "Failed to sync user profile: ${syncResult.message}"
                                        )
                                    }

                                    is UserResult.Loading -> {
                                        _authState.value = _authState.value.copy(
                                            isLoading = true,
                                            error = null
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                _authState.value = _authState.value.copy(
                                    isLoading = false,
                                    isUserLoggedIn = false,
                                    error = "Failed to create user profile: ${e.message}"
                                )
                            }
                        }

                        is AuthResult.Error -> {
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                isUserLoggedIn = false,
                                error = "User not found"
                            )
                        }
                    }
                }

                is UserResult.Loading -> {
                    _authState.value = _authState.value.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading user profile", e)
            _authState.value = _authState.value.copy(
                isLoading = false,
                isUserLoggedIn = false,
                error = "Failed to load user profile: ${e.message}"
            )
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}