package com.yugentech.sessions.authentication.authRepository

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.authentication.AuthService
import com.yugentech.sessions.authentication.authUtils.AuthErrorMapper
import com.yugentech.sessions.authentication.authUtils.AuthResult
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val firestore: FirebaseFirestore
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

    override suspend fun updateProfile(
        displayName: String,
        profileImageUri: Uri?
    ): AuthResult<FirebaseUser> {
        return try {
            val currentUserResult = authService.getCurrentUser()
            if (currentUserResult !is AuthResult.Success) {
                return AuthResult.Error("No user is currently signed in")
            }

            val currentUser = currentUserResult.data

            val profileUpdateBuilder = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)

            if (profileImageUri != null) {
                profileUpdateBuilder.photoUri = profileImageUri
            }

            currentUser.updateProfile(profileUpdateBuilder.build()).await()

            val userUpdates = mutableMapOf<String, Any>(
                "name" to displayName
            )

            if (profileImageUri != null) {
                userUpdates["profilePictureUrl"] = profileImageUri.toString()
            }

            firestore.collection("users")
                .document(currentUser.uid)
                .update(userUpdates)
                .await()

            AuthResult.Success(currentUser)
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }
}