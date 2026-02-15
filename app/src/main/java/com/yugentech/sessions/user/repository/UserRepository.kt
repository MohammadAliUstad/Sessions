package com.yugentech.sessions.user.repository

import com.yugentech.sessions.user.model.UserData
import com.yugentech.sessions.user.result.UserResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(userId: String): UserData?
    fun getUserFlow(userId: String): Flow<UserData?>
    suspend fun upsertUser(userData: UserData)
    suspend fun syncUser(userData: UserData): UserResult<Unit>
    suspend fun fetchUserOnce(userId: String): UserResult<Unit>
}