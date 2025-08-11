package com.yugentech.sessions.user.userRepository

import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.models.UserData
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(userId: String): UserData?
    fun getUserFlow(userId: String): Flow<UserData?>
    suspend fun upsertUser(userData: UserData): AuthResult<Unit>
    suspend fun syncUser(userData: UserData): AuthResult<Unit>
}