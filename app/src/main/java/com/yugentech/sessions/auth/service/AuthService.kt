package com.yugentech.sessions.auth.service

import android.app.PendingIntent
import android.content.Intent
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.yugentech.sessions.auth.mapper.AuthErrorMapper
import com.yugentech.sessions.auth.result.AuthResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

// Service class handling direct interactions with Firebase Authentication and Google Sign-In
class AuthService(
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient
) {
    // Converts the Firebase auth state listener into a cold Flow for reactive updates
    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            Timber.d("Auth state changed. User: ${user?.uid ?: "null"}")
            trySend(user)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    // Returns the currently logged-in user or null
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Creates a new account with email and password and updates the display name
    suspend fun signUp(name: String, email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            Timber.d("Attempting sign up for email: $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!

            user.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            ).await()

            Timber.i("User signed up successfully: ${user.uid}")
            AuthResult.Success(user)
        } catch (e: Exception) {
            Timber.e(e, "Sign up failed")
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    // Authenticates an existing user using email and password
    suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            Timber.d("Attempting sign in for email: $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Timber.i("User signed in successfully: ${result.user?.uid}")
            AuthResult.Success(result.user!!)
        } catch (e: Exception) {
            Timber.w(e, "Sign in failed")
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    // Sends a password recovery email to the specified address
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            Timber.d("Sending password reset email to: $email")
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to send reset email")
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    // Logs the user out of Firebase and the Google One Tap client
    fun signOut() {
        Timber.i("Signing out user")
        auth.signOut()
        oneTapClient.signOut()
    }

    // Builds the Google One Tap sign-in request and returns the pending intent to launch it
    suspend fun getGoogleSignInIntent(webClientId: String): AuthResult<PendingIntent> {
        return try {
            Timber.d("Preparing Google One Tap Sign-In intent")
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
            AuthResult.Success(result.pendingIntent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get Google Sign-In intent")
            AuthResult.Error(AuthErrorMapper.mapGoogleSignInError(e))
        }
    }

    // Extracts credentials from the Google Sign-In result and authenticates with Firebase
    suspend fun handleGoogleSignInResult(data: Intent?): AuthResult<FirebaseUser> {
        return try {
            if (data == null) {
                Timber.w("Google Sign-In result data was null")
                return AuthResult.Error("Google Sign-In cancelled")
            }

            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
                ?: return AuthResult.Error("Google ID token is missing").also { Timber.e("Missing Google ID Token") }

            Timber.d("Google ID Token retrieved, exchanging for Firebase Credential")
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(firebaseCredential).await()

            Timber.i("Google Sign-In successful: ${result.user?.uid}")
            AuthResult.Success(result.user!!)
        } catch (e: Exception) {
            Timber.e(e, "Google Sign-In handling failed")
            AuthResult.Error(AuthErrorMapper.mapGoogleSignInError(e))
        }
    }
}