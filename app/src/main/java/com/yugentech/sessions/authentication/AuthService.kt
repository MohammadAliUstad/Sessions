@file:Suppress("DEPRECATION")

package com.yugentech.sessions.authentication

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.yugentech.sessions.authentication.authUtils.AuthErrorMapper
import com.yugentech.sessions.authentication.authUtils.AuthResult
import kotlinx.coroutines.tasks.await

class AuthService(
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient
) {

    companion object {
        private const val TAG = "AuthService"
    }

    suspend fun signUp(name: String, email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            ).await()
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(result.user!!)
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    fun getCurrentUser(): AuthResult<FirebaseUser> {
        val user = auth.currentUser
        return if (user != null) {
            AuthResult.Success(user)
        } else {
            AuthResult.Error("No user is currently signed in")
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<PendingIntent> {
        return try {
            Log.d(TAG, "Starting Google Sign-In with webClientId=$webClientId")

            val signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(webClientId)
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(false)
                .build()

            val result = oneTapClient.beginSignIn(signInRequest).await()
            Log.d(TAG, "Google Sign-In PendingIntent retrieved successfully")
            AuthResult.Success(result.pendingIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In Intent failed: ${e.localizedMessage}", e)
            AuthResult.Error(AuthErrorMapper.mapGoogleSignInError(e))
        }
    }

    suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser> {
        return try {
            if (data == null) {
                Log.e(TAG, "Google Sign-In cancelled, intent data is null")
                return AuthResult.Error("Google Sign-In was cancelled. Please try again.")
            }

            Log.d(TAG, "Handling Google Sign-In result...")

            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken ?: throw Exception("Google ID token is null")
            Log.d(TAG, "Google ID Token retrieved successfully")

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(firebaseCredential).await()
            val user = result.user!!

            Log.d(TAG, "Google Sign-In successful. User: ${user.email}")
            AuthResult.Success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling Google Sign-In result: ${e.localizedMessage}", e)
            AuthResult.Error(AuthErrorMapper.mapGoogleSignInError(e))
        }
    }
}
