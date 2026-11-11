package com.yugentech.sessions.auth.repository

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.auth.FirebaseUser
import com.yugentech.sessions.auth.model.AuthUser
import com.yugentech.sessions.auth.result.AuthResult
import kotlinx.coroutines.flow.Flow

// Interface defining the contract for all authentication operations
interface AuthRepository {
    /** The signed-in account or the local guest, or null when neither. */
    val authState: Flow<AuthUser?>
    val currentUser: String?
    suspend fun signUp(name: String, email: String, password: String): AuthResult<FirebaseUser>
    suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser>
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>
    fun signOut()
    suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<PendingIntent>
    suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser>

    // --- Guest mode ---

    /** Whether the app is in guest mode: a local guest ID, with no Firebase account. */
    val isGuest: Boolean

    /** Starts guest mode on this phone; nothing is created in Firebase. */
    fun startGuest()

    /** Ends guest mode; the caller removes the guest's data first. */
    fun endGuest()
}
