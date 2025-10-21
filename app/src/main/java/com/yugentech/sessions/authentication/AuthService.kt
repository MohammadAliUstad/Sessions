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
        Log.d(TAG, "Attempting signUp for email: $email")
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            Log.d(TAG, "User created: ${user.uid}, updating display name...")
            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            ).await()
            Log.d(TAG, "Display name updated successfully for ${user.email}")
            AuthResult.Success(user)
        } catch (e: Exception) {
            Log.e(TAG, "signUp failed: ${e.message}", e)
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser> {
        Log.d(TAG, "Attempting signIn for email: $email")
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user!!
            Log.d(TAG, "signIn success for user: ${user.email}")
            AuthResult.Success(user)
        } catch (e: Exception) {
            Log.e(TAG, "signIn failed: ${e.message}", e)
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        Log.d(TAG, "Sending password reset email to: $email")
        return try {
            auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Password reset email sent successfully.")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "sendPasswordResetEmail failed: ${e.message}", e)
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    fun getCurrentUser(): AuthResult<FirebaseUser> {
        val user = auth.currentUser
        Log.d(TAG, "Checking current user: ${user?.email ?: "null"}")
        return if (user != null) {
            AuthResult.Success(user)
        } else {
            AuthResult.Error("No user is currently signed in")
        }
    }

    fun signOut() {
        Log.d(TAG, "Signing out current user: ${auth.currentUser?.email}")
        auth.signOut()
    }

    suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<PendingIntent> {
        Log.d(TAG, "Building Google Sign-In intent with webClientId: $webClientId")
        return try {
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

            Log.d(TAG, "Calling beginSignIn() on One Tap client...")
            val result = oneTapClient.beginSignIn(signInRequest).await()
            Log.d(TAG, "One Tap beginSignIn succeeded. PendingIntent received.")
            AuthResult.Success(result.pendingIntent)
        } catch (e: Exception) {
            Log.e(TAG, "getGoogleSignInIntent failed: ${e.message}", e)
            AuthResult.Error(AuthErrorMapper.mapGoogleSignInError(e))
        }
    }

    suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser> {
        Log.d(TAG, "Handling Google Sign-In result...")
        return try {
            if (data == null) {
                Log.e(TAG, "Sign-In intent data is null.")
                return AuthResult.Error("Google Sign-In was cancelled. Please try again.")
            }

            Log.d(TAG, "Extracting sign-in credential from intent...")
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            Log.d(TAG, "Extracted credential: idToken=${idToken?.take(10)}...")

            if (idToken == null) {
                Log.e(TAG, "Google ID token is null.")
                throw Exception("Google ID token is null")
            }

            Log.d(TAG, "Authenticating with Firebase using Google credential...")
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(firebaseCredential).await()
            val user = result.user!!

            Log.d(TAG, "Firebase sign-in successful for user: ${user.email}")
            AuthResult.Success(user)
        } catch (e: Exception) {
            Log.e(TAG, "handleGoogleSignInResult failed: ${e.message}", e)
            AuthResult.Error(AuthErrorMapper.mapGoogleSignInError(e))
        }
    }
}
