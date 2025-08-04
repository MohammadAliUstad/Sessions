package com.yugentech.sessions.authentication.authRepository

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.authentication.AuthService
import com.yugentech.sessions.authentication.authUtils.AuthResult

class AuthRepositoryImpl(
    private val authService: AuthService
) : AuthRepository {

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): AuthResult<FirebaseUser> {
        return authService.signUp(name, email, password)
    }

    override suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser> {
        return authService.signIn(email, password)
    }

    override fun getCurrentUser(): AuthResult<FirebaseUser> {
        return authService.getCurrentUser()
    }

    override fun signOut() {
        authService.signOut()
    }

    override suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<PendingIntent> {
        return authService.getGoogleSignInIntent(webClientId)
    }

    override suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser> {
        return authService.handleGoogleSignInResult(data)
    }
}