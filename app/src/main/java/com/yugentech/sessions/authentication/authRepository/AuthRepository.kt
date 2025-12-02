package com.yugentech.sessions.authentication.authRepository

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.authentication.authUtils.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<FirebaseUser?>
    val currentUser: FirebaseUser?

    suspend fun signUp(name: String, email: String, password: String): AuthResult<FirebaseUser>
    suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser>
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>
    fun signOut()
    suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<PendingIntent>
    suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser>
}