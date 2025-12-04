package com.yugentech.sessions.authentication.authRepository

import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.authentication.AuthService
import com.yugentech.sessions.authentication.authUtils.AuthResult
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

// Implementation of AuthRepository that delegates to AuthService with logging
class AuthRepositoryImpl(
    private val authService: AuthService
) : AuthRepository {

    override val authState: Flow<FirebaseUser?> = authService.authStateFlow

    override val currentUser: FirebaseUser?
        get() = authService.currentUser

    override suspend fun signUp(name: String, email: String, password: String): AuthResult<FirebaseUser> {
        Timber.i("Sign up requested") // Log action but NOT PII (email/password)
        return authService.signUp(name, email, password)
    }

    override suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser> {
        Timber.i("Sign in requested")
        return authService.signIn(email, password)
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        Timber.i("Password reset requested")
        return authService.sendPasswordResetEmail(email)
    }

    override fun signOut() {
        Timber.i("Signing out current user")
        authService.signOut()
    }

    override suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<android.app.PendingIntent> {
        Timber.d("Requesting Google Sign-In Intent")
        return authService.getGoogleSignInIntent(webClientId)
    }

    override suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser> {
        Timber.i("Handling Google Sign-In result")
        return authService.handleGoogleSignInResult(data)
    }
}