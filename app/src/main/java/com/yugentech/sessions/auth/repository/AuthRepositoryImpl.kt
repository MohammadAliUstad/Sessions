package com.yugentech.sessions.auth.repository

import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.auth.result.AuthResult
import com.yugentech.sessions.auth.service.AuthService
import com.yugentech.sessions.user.datastore.UserDataStore
import com.yugentech.sessions.utils.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber

// Implementation that delegates calls to the underlying AuthService
class AuthRepositoryImpl(
    private val authService: AuthService,
    private val userDataStore: UserDataStore
) : AuthRepository {

    private var _isGuestModeCached = false

    // Exposes real-time authentication state updates
    override val authState: Flow<FirebaseUser?> = authService.authStateFlow

    // Retrieves the current user's unique ID if logged in
    override val currentUser: String?
        get() {
            val firebaseUserId = authService.currentUser?.uid
            return if (firebaseUserId != null) {
                firebaseUserId
            } else if (_isGuestModeCached) {
                AppConstants.GUEST_USER_ID
            } else {
                null
            }
        }

    // Registers a new user with email and password
    override suspend fun signUp(name: String, email: String, password: String): AuthResult<FirebaseUser> {
        Timber.i("Sign up requested")
        return authService.signUp(name, email, password)
    }

    // Authenticates an existing user
    override suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser> {
        Timber.i("Sign in requested")
        return authService.signIn(email, password)
    }

    // Triggers a password recovery email for the user
    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        Timber.i("Password reset requested")
        return authService.sendPasswordResetEmail(email)
    }

    // Logs the user out of the application
    override fun signOut() {
        Timber.i("Signing out current user")
        authService.signOut()
    }

    override suspend fun setGuestMode(isGuest: Boolean) {
        _isGuestModeCached = isGuest
        userDataStore.saveGuestMode(isGuest)
    }

    override suspend fun isGuestMode(): Boolean {
        // Update cache from DataStore if needed, or just return cache if we keep it in sync
        _isGuestModeCached = userDataStore.isGuestMode.first()
        return _isGuestModeCached
    }

    // Prepares the intent required to launch the Google Sign-In flow
    override suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<android.app.PendingIntent> {
        Timber.d("Requesting Google Sign-In Intent")
        return authService.getGoogleSignInIntent(webClientId)
    }

    // Processes the result returned from the Google Sign-In activity
    override suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser> {
        Timber.i("Handling Google Sign-In result")
        return authService.handleGoogleSignInResult(data)
    }
}