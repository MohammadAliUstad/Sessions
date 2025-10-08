package com.yugentech.sessions.user

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.authentication.authUtils.AuthErrorMapper
import com.yugentech.sessions.models.UserData
import kotlinx.coroutines.tasks.await

class UserService(
    private val firestore: FirebaseFirestore
) {

    private fun profileDocRef(userId: String) = firestore.collection("users").document(userId)

    suspend fun uploadUser(userData: UserData): UserResult<Unit> {
        return try {
            val uploadData = userData.toMap()

            profileDocRef(userData.userId).set(uploadData).await()
            UserResult.Success(Unit)
        } catch (e: Exception) {
            UserResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    suspend fun fetchUser(userId: String): UserResult<UserData> {
        return try {
            val document = profileDocRef(userId).get().await()

            if (!document.exists()) {
                return UserResult.Error("User not found")
            }

            val userData = UserData(
                userId = document.getString("userId") ?: userId,
                name = document.getString("name"),
                email = document.getString("email"),
                avatarId = document.getLong("avatarId")?.toInt() ?: 0
            )

            UserResult.Success(userData)
        } catch (e: Exception) {
            UserResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }
}