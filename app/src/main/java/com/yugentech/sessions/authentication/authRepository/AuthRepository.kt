package com.yugentech.sessions.authentication.authRepository

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.authentication.authUtils.AuthResult
import kotlinx.coroutines.flow.Flow

// Interface defining the contract for authentication operations
interface AuthRepository {
    // Observable flow of the current authentication state (User or Null)
    val authState: Flow<FirebaseUser?>

    // Immediate access to the current logged-in user, if any
    val currentUser: String?

    // Registers a new user with email and password
    suspend fun signUp(name: String, email: String, password: String): AuthResult<FirebaseUser>

    // Authenticates an existing user
    suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser>

    // Triggers a password reset email for the given address
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>

    // Signs out the current user and clears session data
    fun signOut()

    // Retrieves the intent required to launch the Google Sign-In flow
    suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<PendingIntent>

    // Processes the result returned from the Google Sign-In activity
    suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser>
}