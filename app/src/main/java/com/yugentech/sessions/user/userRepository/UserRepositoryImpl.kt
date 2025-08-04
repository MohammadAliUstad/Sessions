package com.yugentech.sessions.user.userRepository

import com.yugentech.sessions.authentication.authUtils.AuthResult
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.user.UserService

class UserRepositoryImpl(
    private val userService: UserService
) : UserRepository {

    override suspend fun getUser(userId: String): AuthResult<UserData> {
        return userService.getUser(userId)
    }

    override suspend fun createUser(userId: String, userData: UserData): AuthResult<Unit> {
        return userService.createUser(userId, userData)
    }

    override suspend fun updateUser(userId: String, userData: UserData): AuthResult<Unit> {
        return userService.updateUser(userId, userData)
    }
}