package com.yugentech.sessions.authentication

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.authentication.authRepository.AuthRepository
import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.authentication.authUtils.AuthState
import com.yugentech.sessions.authentication.authUtils.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    init {
        viewModelScope.launch {
            when (val result = authRepository.getCurrentUser()) {
                is AuthResult.Success -> updateAuthState(result.data)
                else -> _authState.value = AuthState(isUserLoggedIn = false)
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.signUp(name, email, password)) {
                is AuthResult.Success -> updateAuthState(result.data)
                is AuthResult.Error -> _authState.value =
                    _authState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.signIn(email, password)) {
                is AuthResult.Success -> updateAuthState(result.data)
                is AuthResult.Error -> _authState.value =
                    _authState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState(isUserLoggedIn = false)
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
                is AuthResult.Success -> updateAuthState(result.data)
                is AuthResult.Error -> _authState.value =
                    _authState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    // New method to update profile
    fun updateProfile(displayName: String, profileImageUri: Uri? = null) {
        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.updateProfile(displayName, profileImageUri)) {
                is AuthResult.Success -> updateAuthState(result.data)
                is AuthResult.Error -> _authState.value =
                    _authState.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    private fun updateAuthState(user: FirebaseUser?) {
        if (user != null) {
            val userData = UserData(
                userId = user.uid,
                username = user.displayName,
                profilePictureUrl = user.photoUrl?.toString(),
                email = user.email
            )

            _authState.value = AuthState(
                isUserLoggedIn = true,
                userId = user.uid,
                userData = userData,
                isLoading = false
            )
        } else {
            _authState.value = AuthState(isUserLoggedIn = false, isLoading = false)
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}