package com.yugentech.sessions.authentication.authRepository

import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.authentication.AuthService
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val authService: AuthService
) : AuthRepository {

    override val authState: Flow<FirebaseUser?> = authService.authStateFlow
    override val currentUser: FirebaseUser? get() = authService.currentUser

    override suspend fun signUp(name: String, email: String, password: String) =
        authService.signUp(name, email, password)

    override suspend fun signIn(email: String, password: String) =
        authService.signIn(email, password)

    override suspend fun sendPasswordResetEmail(email: String) =
        authService.sendPasswordResetEmail(email)

    override fun signOut() = authService.signOut()

    override suspend fun getGoogleSignInIntent(webClientId: String) =
        authService.getGoogleSignInIntent(webClientId)

    override suspend fun handleGoogleSignInResult(data: Intent?) =
        authService.handleGoogleSignInResult(data)
}