package com.yugentech.sessions.user

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.authentication.authUtils.AuthErrorMapper
import com.yugentech.sessions.models.UserData
import kotlinx.coroutines.tasks.await
import timber.log.Timber

// Handles direct network operations with Firestore for user documents
class UserService(
    private val firestore: FirebaseFirestore
) {

    // Creates a reference to a specific user document in the 'users' collection
    private fun profileDocRef(userId: String) = firestore.collection("users").document(userId)

    // Saves the user object to Firestore and handles potential errors
    suspend fun uploadUser(userData: UserData): UserResult<Unit> {
        return try {
            Timber.d("Uploading user profile for: ${userData.userId}")
            val uploadData = userData.toMap()

            profileDocRef(userData.userId).set(uploadData).await()
            Timber.i("User profile uploaded successfully")
            UserResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to upload user profile")
            UserResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }

    // Retrieves user data from Firestore and maps it to the domain model
    suspend fun fetchUser(userId: String): UserResult<UserData> {
        return try {
            Timber.d("Fetching user profile for: $userId")
            val document = profileDocRef(userId).get().await()

            if (!document.exists()) {
                Timber.w("User profile document not found")
                return UserResult.Error("User not found")
            }

            val userData = UserData(
                userId = document.getString("userId") ?: userId,
                name = document.getString("name"),
                email = document.getString("email"),
                avatarId = document.getLong("avatarId")?.toInt() ?: 0
            )

            Timber.i("User profile fetched successfully")
            UserResult.Success(userData)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch user profile")
            UserResult.Error(AuthErrorMapper.mapFirebaseAuthError(e))
        }
    }
}