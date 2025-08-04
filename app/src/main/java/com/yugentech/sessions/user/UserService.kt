package com.yugentech.sessions.user

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.authentication.authUtils.AuthErrorMapper
import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.models.UserData
import kotlinx.coroutines.tasks.await

class UserService(
    private val firestore: FirebaseFirestore
) {
    private fun profileDocRef(userId: String) = firestore.collection("users").document(userId)

    suspend fun createUser(userId: String, userData: UserData): AuthResult<Unit> {
        return try {
            profileDocRef(userId).set(userData.toMap()).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    suspend fun getUser(userId: String): AuthResult<UserData?> {
        return try {
            val doc = profileDocRef(userId).get().await()
            if (doc.exists()) {
                val userData = UserData.fromMap(doc.data ?: emptyMap())
                AuthResult.Success(userData)
            } else {
                AuthResult.Error("User profile not found")
            }
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    suspend fun updateUser(userId: String, userData: UserData): AuthResult<Unit> {
        return try {
            profileDocRef(userId).update(userData.toMap()).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }
}