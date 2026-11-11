package com.yugentech.sessions.auth.viewmodel

import com.yugentech.sessions.R
import com.yugentech.sessions.localization.AppStrings
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yugentech.sessions.auth.guest.GuestDataManager
import com.yugentech.sessions.auth.model.AuthUser
import com.yugentech.sessions.auth.repository.AuthRepository
import com.yugentech.sessions.auth.result.AuthResult
import com.yugentech.sessions.auth.state.AuthState
import com.yugentech.sessions.sessions.datastore.SyncDataStore
import com.yugentech.sessions.templates.repository.TemplateRepository
import com.yugentech.sessions.ui.auth.state.ForgotPasswordState
import com.yugentech.sessions.user.datastore.UserDataStore
import com.yugentech.sessions.user.model.UserData
import com.yugentech.sessions.user.repository.UserRepository
import com.yugentech.sessions.user.result.UserResult
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

/**
 * A new profile's avatar: 0, the placeholder's own ID (it draws the default avatar). Matching it
 * means the Profile screen doesn't animate from the placeholder to a different, random avatar.
 */
private const val NEW_USER_AVATAR_ID = 0

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val syncDataStore: SyncDataStore,
    private val userDataStore: UserDataStore,
    private val templateRepository: TemplateRepository,
    private val guestDataManager: GuestDataManager
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState(isInitializing = true))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val showOnboarding = userDataStore.isOnboardingCompleted
        .map { !it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
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
            authRepository.authState.collect { user ->
                if (user != null) {
                    Timber.d("Auth state update: User logged in ${user.uid} (guest: ${user.isGuest})")
                    // A guest's ID never leaves the phone, crash reports included
                    FirebaseCrashlytics.getInstance().setUserId(if (user.isGuest) "" else user.uid)

                    if (_authState.value.userId != user.uid) {
                        loadUserProfile(user)
                    }
                } else {
                    Timber.d("Auth state update: User logged out")
                    FirebaseCrashlytics.getInstance().setUserId("")

                    _authState.update {
                        AuthState(
                            isInitializing = false,
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
                        // The default avatar, the same one the Profile screen shows before the
                        // profile loads, so it doesn't visibly swap on the first visit
                        avatarId = NEW_USER_AVATAR_ID
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
            userDataStore.saveOnboardingCompleted(true)
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
                _authState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    // --- Guest mode ---
    // A guest exists only on this phone: a local ID, no Firebase account, and data that never
    // leaves the device. To keep it in an account they export a backup file, leave guest mode,
    // sign in, and import the file there.

    /** Starts guest mode; works offline. The auth state then loads it like any sign-in. */
    fun continueAsGuest() {
        _authState.update { it.copy(isLoading = true, error = null) }
        authRepository.startGuest()
    }

    /**
     * Leaves guest mode: removes the guest's data from this phone, then their local ID. The auth
     * state then shows the sign-in screen, as after signing out.
     */
    fun exitGuestMode() {
        val guestId = authRepository.currentUser ?: return
        Timber.i("User requested to exit guest mode")
        _authState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            guestDataManager.deleteLocalData(guestId)
            syncDataStore.clearSyncFlags()
            authRepository.endGuest()
        }
    }

    fun signOut() {
        Timber.i("User requested sign out")
        _authState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // The templates table isn't per account, so empty it before the next account signs
            // in (the cloud copy stays and comes back at this account's next sign-in).
            templateRepository.clearLocal()
            authRepository.signOut()
            syncDataStore.clearSyncFlags()
        }
    }

    fun forgotPassword(email: String) {
        _forgotPasswordState.value = ForgotPasswordState.Loading
        viewModelScope.launch {
            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is AuthResult.Success -> {
                    Timber.i("Password reset email sent")
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

    private fun loadUserProfile(user: AuthUser) {
        profileLoadingJob?.cancel()
        profileLoadingJob = viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            val isGuest = user.isGuest

            when (userRepository.fetchUserOnce(user.uid)) {
                is UserResult.Success -> {
                    val localUser = userRepository.getUser(user.uid)
                    if (localUser != null) {
                        Timber.d("Local profile loaded for ${user.uid}")
                        _authState.update {
                            it.copy(
                                isInitializing = false,
                                isLoading = false,
                                isUserLoggedIn = true,
                                isGuest = isGuest,
                                userId = user.uid,
                                userData = localUser
                            )
                        }
                    } else {
                        Timber.e("Profile missing after fetch success")
                        _authState.update {
                            it.copy(
                                isInitializing = false,
                                isLoading = false,
                                error = AppStrings.get(R.string.auth_error_profile_load)
                            )
                        }
                    }
                }

                is UserResult.Error -> {
                    // Offline with a profile already on this phone (a guest reopening the app,
                    // say): use it rather than making a new one over their name and avatar
                    val localUser = userRepository.getUser(user.uid)
                    if (localUser != null) {
                        Timber.w("Profile fetch failed; using the profile on this device")
                        _authState.update {
                            it.copy(
                                isInitializing = false,
                                isLoading = false,
                                isUserLoggedIn = true,
                                isGuest = isGuest,
                                userId = user.uid,
                                userData = localUser
                            )
                        }
                        return@launch
                    }

                    Timber.w("Profile fetch failed, attempting creation")
                    // A guest has no name or email yet; the app shows "Guest" until they pick a name
                    val newUser = UserData(
                        userId = user.uid,
                        name = if (isGuest) null else user.displayName ?: "User",
                        email = if (isGuest) null else user.email ?: "",
                        avatarId = NEW_USER_AVATAR_ID
                    )
                    syncOrCreateUser(newUser, isGuest)
                }

                is UserResult.Loading -> {}
            }
        }
    }

    private suspend fun syncOrCreateUser(userData: UserData, isGuest: Boolean = false) {
        try {
            userRepository.upsertUser(userData)

            when (val syncResult = userRepository.syncUser(userData)) {
                is UserResult.Success -> {
                    _authState.update {
                        it.copy(
                            isInitializing = false,
                            isLoading = false,
                            isUserLoggedIn = true,
                            isGuest = isGuest,
                            userId = userData.userId,
                            userData = userData
                        )
                    }
                }

                is UserResult.Error -> {
                    Timber.e("User sync failed: ${syncResult.message}")
                    _authState.update {
                        it.copy(
                            isInitializing = false,
                            isLoading = false,
                            error = AppStrings.get(R.string.auth_error_sync, syncResult.message)
                        )
                    }
                }

                else -> {}
            }
        } catch (e: Exception) {
            Timber.e(e, "Critical error creating user profile")
            _authState.update {
                it.copy(
                    isInitializing = false,
                    isLoading = false,
                    error = AppStrings.get(R.string.auth_error_profile_create, e.message.orEmpty())
                )
            }
        }
    }
}