package com.yugentech.sessions.user.userRepository

import android.util.Log
import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.room.entities.UserEntity
import com.yugentech.sessions.user.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val userService: UserService
) : UserRepository {

    companion object {
        private const val TAG = "UserRepository"
    }

    override fun getUserFlow(userId: String): Flow<UserData?> {
        Log.d(TAG, "getUserFlow called for userId: $userId")
        return userDao.getUserFlow(userId).map { entity ->
            val userData = entity?.toUserData()
            Log.d(TAG, "getUserFlow emitted for $userId: ${userData?.name ?: "null"}")
            userData
        }
    }

    override fun getTotalTimeFlow(userId: String): Flow<Long> {
        Log.d(TAG, "getTotalTimeFlow called for userId: $userId")
        return userDao.getTotalTimeFlow(userId).map {
            val time = it ?: 0L
            Log.d(TAG, "getTotalTimeFlow emitted for $userId: $time")
            time
        }
    }

    override suspend fun createUser(userData: UserData): AuthResult<Unit> {
        return try {
            val entity = UserEntity.fromUserData(userData.copy(pendingSync = true))
            userDao.insertUser(entity)

            // 🎯 IMMEDIATE SYNC after creation
            syncUserToFirestore(userData.userId)

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to create user")
        }
    }

    override suspend fun updateUser(userData: UserData): AuthResult<Unit> {
        return try {
            val entity = UserEntity.fromUserData(userData.copy(pendingSync = true))
            userDao.updateUser(entity)
            syncUserToFirestore(userData.userId)
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to update user")
        }
    }

    override suspend fun addStudyTime(userId: String, additionalSeconds: Int) {
        Log.d(TAG, "⏱️ Adding study time: ${additionalSeconds}s for user: $userId")
        try {
            userDao.addToTotalTime(userId, additionalSeconds)
            Log.d(TAG, "✅ Study time added successfully")
            syncUserToFirestore(userId)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to add study time", e)
        }
    }

    override suspend fun syncUserToFirestore(userId: String) {
        Log.d(TAG, "☁️ Starting Firestore sync for user: $userId")
        try {
            val localUser = userDao.getUser(userId)?.toUserData()
            if (localUser == null) {
                Log.w(TAG, "⚠️ No local user found for sync: $userId")
                return
            }

            Log.d(TAG, "📤 Uploading user to Firestore: ${localUser.name}")
            // 🎯 CHANGE: Use uploadUserToFirestore instead of updateUserInFirestore
            when (val result = userService.uploadUserToFirestore(localUser)) {
                is AuthResult.Success -> {
                    Log.d(TAG, "✅ Firestore sync successful, marking as synced")
                    userDao.markSynced(userId)
                }

                is AuthResult.Error -> {
                    Log.e(TAG, "❌ Firestore sync failed: ${result.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception during Firestore sync", e)
        }
    }

    // 🎯 ADD THIS NEW METHOD for direct user retrieval (for AuthViewModel)
    override suspend fun getUser(userId: String): UserData? {
        Log.d(TAG, "🔍 Direct getUser called for: $userId")
        return try {
            val entity = userDao.getUser(userId)
            val userData = entity?.toUserData()
            Log.d(TAG, "🔍 Direct getUser result for $userId: ${userData?.name ?: "null"}")
            userData
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error in direct getUser", e)
            null
        }
    }
}