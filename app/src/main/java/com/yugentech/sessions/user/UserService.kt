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

    suspend fun uploadUserToFirestore(userData: UserData): AuthResult<Unit> {
        return try {
            val uploadData = userData.copy(
                lastSyncTimestamp = System.currentTimeMillis(),
                pendingSync = false
            ).toMap()

            profileDocRef(userData.userId).set(uploadData).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }
}