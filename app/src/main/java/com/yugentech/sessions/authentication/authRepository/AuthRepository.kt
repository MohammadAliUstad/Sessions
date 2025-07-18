package com.yugentech.sessions.authentication.authRepository

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.authentication.authUtils.AuthResult

interface AuthRepository {
    suspend fun signUp(name: String, email: String, password: String): AuthResult<FirebaseUser>
    suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser>
    fun getCurrentUser(): AuthResult<FirebaseUser>
    fun signOut()
    suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<PendingIntent>
    suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser>
    suspend fun updateProfile(
        displayName: String,
        profileImageUri: Uri? = null
    ): AuthResult<FirebaseUser>
}