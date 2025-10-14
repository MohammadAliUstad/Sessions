package com.yugentech.sessions.auth.repository

import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.auth.service.AuthService
import com.yugentech.sessions.auth.result.AuthResult
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

// Implementation that delegates calls to the underlying AuthService
class AuthRepositoryImpl(
    private val authService: AuthService
) : AuthRepository {

    // Exposes real-time authentication state updates
    override val authState: Flow<FirebaseUser?> = authService.authStateFlow

    // Retrieves the current user's unique ID if logged in
    override val currentUser: String?
        get() = authService.currentUser?.uid

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

    // Logs the current user out of the application
    override fun signOut() {
        Timber.i("Signing out current user")
        authService.signOut()
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