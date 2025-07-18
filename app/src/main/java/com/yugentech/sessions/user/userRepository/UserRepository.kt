package com.yugentech.sessions.user.userRepository

import com.yugentech.sessions.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsers(): Flow<List<User>>
}