package com.yugentech.sessions.user.userRepository

import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.models.UserData

interface UserRepository {
    suspend fun getUser(userId: String): AuthResult<UserData>
    suspend fun createUser(userId: String, userData: UserData): AuthResult<Unit>
    suspend fun updateUser(userId: String, userData: UserData): AuthResult<Unit>
}