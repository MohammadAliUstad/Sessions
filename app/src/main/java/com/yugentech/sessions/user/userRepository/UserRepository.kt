package com.yugentech.sessions.user.userRepository

import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.models.UserData
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // 📱 LOCAL DATA ACCESS
    fun getUserFlow(userId: String): Flow<UserData?>
    suspend fun getUser(userId: String): UserData? // 👈 Direct access
    fun getTotalTimeFlow(userId: String): Flow<Long>

    // ✏️ DATA MODIFICATION (auto-sync after each operation)
    suspend fun createUser(userData: UserData): AuthResult<Unit>
    suspend fun updateUser(userData: UserData): AuthResult<Unit>
    suspend fun addStudyTime(userId: String, additionalSeconds: Int)

    // ☁️ CLOUD SYNC (only for current user)
    suspend fun syncUserToFirestore(userId: String)

    // 🎯 REMOVED: downloadUserFromFirestore - unnecessary!
    // 🎯 REMOVED: getUsersPendingSync - security issue!
}