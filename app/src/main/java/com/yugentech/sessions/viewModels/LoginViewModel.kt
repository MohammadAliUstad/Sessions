package com.yugentech.sessions.viewModels

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yugentech.sessions.authentication.authRepository.AuthRepository
import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.authentication.authUtils.AuthState
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.sessions.SyncPreferences
import com.yugentech.sessions.user.UserResult
import com.yugentech.sessions.user.userRepository.UserRepository
import com.yugentech.sessions.ui.auth.states.ForgotPasswordState
import com.yugentech.sessions.user.UserPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val syncPreferences: SyncPreferences,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState(isLoading = true))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val showOnboarding = userPreferences.isOnboardingCompleted
        .map { !it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _forgotPasswordState =
        MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState.asStateFlow()

    private var profileLoadingJob: Job? = null

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authState.collect { firebaseUser ->
                if (firebaseUser != null) {
                    Timber.d("Auth state update: User logged in ${firebaseUser.uid}")

                    FirebaseCrashlytics.getInstance().setUserId(firebaseUser.uid)

                    if (_authState.value.userId != firebaseUser.uid) {
                        loadUserProfile(firebaseUser)
                    }
                } else {
                    Timber.d("Auth state update: User logged out")

                    FirebaseCrashlytics.getInstance().setUserId("")

                    _authState.update {
                        AuthState(
                            isLoading = false,
                            isUserLoggedIn = false,
                            userId = null,
                            userData = null,
                            error = null
                        )
                    }
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        _authState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            if (result is AuthResult.Error) {
                Timber.w("Sign in failed: ${result.message}")
                _authState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        _authState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = authRepository.signUp(name, email, password)) {
                is AuthResult.Success -> {
                    Timber.i("Sign up successful, creating local profile")
                    val firebaseUser = result.data
                    val newUser = UserData(
                        userId = firebaseUser.uid,
                        name = name,
                        email = email,
                        avatarId = (1..27).random()
                    )
                    syncOrCreateUser(newUser)
                }

                is AuthResult.Error -> {
                    Timber.w("Sign up failed: ${result.message}")
                    _authState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.saveOnboardingCompleted(true)
        }
    }

    fun getGoogleSignInIntent(webClientId: String) {
        _authState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = authRepository.getGoogleSignInIntent(webClientId)) {
                is AuthResult.Success -> {
                    _authState.update { it.copy(isLoading = false, intent = result.data) }
                }

                is AuthResult.Error -> {
                    Timber.e("Failed to get Google Intent: ${result.message}")
                    _authState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        _authState.update { it.copy(isLoading = true, error = null, intent = null) }

        viewModelScope.launch {
            val result = authRepository.handleGoogleSignInResult(data)
            if (result is AuthResult.Error) {
                Timber.w("Google Sign-In failed: ${result.message}")
                _authState.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun signOut() {
        Timber.i("User requested sign out")
        _authState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.signOut()
            syncPreferences.clearSyncFlags()
        }
    }

    fun forgotPassword(email: String) {
        _forgotPasswordState.value = ForgotPasswordState.Loading
        viewModelScope.launch {
            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is AuthResult.Success -> {
                    Timber.i("Password reset email sent to $email")
                    _forgotPasswordState.value = ForgotPasswordState.Success
                }

                is AuthResult.Error -> {
                    Timber.w("Password reset failed: ${result.message}")
                    _forgotPasswordState.value = ForgotPasswordState.Error(result.message)
                }
            }
        }
    }

    fun clearForgotPasswordState() {
        _forgotPasswordState.value = ForgotPasswordState.Idle
    }

    fun clearError() {
        _authState.update { it.copy(error = null) }
    }

    private fun loadUserProfile(firebaseUser: FirebaseUser) {
        profileLoadingJob?.cancel()
        profileLoadingJob = viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            when (userRepository.fetchUserOnce(firebaseUser.uid)) {
                is UserResult.Success -> {
                    val localUser = userRepository.getUser(firebaseUser.uid)
                    if (localUser != null) {
                        Timber.d("Local profile loaded for ${firebaseUser.uid}")
                        _authState.update {
                            it.copy(
                                isUserLoggedIn = true,
                                userId = firebaseUser.uid,
                                userData = localUser,
                                isLoading = false
                            )
                        }
                    } else {
                        Timber.e("Profile missing after fetch success")
                        _authState.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to load local profile"
                            )
                        }
                    }
                }

                is UserResult.Error -> {
                    Timber.w("Profile fetch failed, attempting creation")
                    val newUser = UserData(
                        userId = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "User",
                        email = firebaseUser.email ?: "",
                        avatarId = (1..27).random()
                    )
                    syncOrCreateUser(newUser)
                }

                is UserResult.Loading -> {}
            }
        }
    }

    private suspend fun syncOrCreateUser(userData: UserData) {
        try {
            userRepository.upsertUser(userData)

            when (val syncResult = userRepository.syncUser(userData)) {
                is UserResult.Success -> {
                    _authState.update {
                        it.copy(
                            isUserLoggedIn = true,
                            userId = userData.userId,
                            userData = userData,
                            isLoading = false
                        )
                    }
                }

                is UserResult.Error -> {
                    Timber.e("User sync failed: ${syncResult.message}")
                    _authState.update {
                        it.copy(isLoading = false, error = "Sync failed: ${syncResult.message}")
                    }
                }

                else -> {}
            }
        } catch (e: Exception) {
            Timber.e(e, "Critical error creating user profile")
            _authState.update {
                it.copy(isLoading = false, error = "Profile creation failed: ${e.message}")
            }
        }
    }
}