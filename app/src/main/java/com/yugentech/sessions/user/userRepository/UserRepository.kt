package com.yugentech.sessions.user.userRepository

import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.user.UserResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(userId: String): UserData?
    fun getUserFlow(userId: String): Flow<UserData?>
    suspend fun upsertUser(userData: UserData)
    suspend fun syncUser(userData: UserData): UserResult<Unit>
    suspend fun fetchUserOnce(userId: String): UserResult<Unit>
}