package com.yugentech.sessions.auth.repository

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.auth.result.AuthResult
import kotlinx.coroutines.flow.Flow

// Interface defining the contract for all authentication operations
interface AuthRepository {
    val authState: Flow<FirebaseUser?>
    val currentUser: String?
    suspend fun signUp(name: String, email: String, password: String): AuthResult<FirebaseUser>
    suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser>
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>
    fun signOut()
    suspend fun setGuestMode(isGuest: Boolean)
    suspend fun isGuestMode(): Boolean
    suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<PendingIntent>
    suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser>
}