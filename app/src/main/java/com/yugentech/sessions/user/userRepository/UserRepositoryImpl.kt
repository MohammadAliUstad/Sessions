package com.yugentech.sessions.user.userRepository

import com.yugentech.sessions.models.User
import com.yugentech.sessions.user.UserService
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val userService: UserService
) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> {
        return userService.getAllUsers()
    }
}