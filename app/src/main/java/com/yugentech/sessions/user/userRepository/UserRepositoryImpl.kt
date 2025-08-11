package com.yugentech.sessions.user.userRepository

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

    override fun getUserFlow(userId: String): Flow<UserData?> {
        return userDao.getUserFlow(userId).map { entity ->
            entity?.toUserData()
        }
    }

    override suspend fun upsertUser(userData: UserData): AuthResult<Unit> {
        return try {
            val entity = UserEntity.fromUserData(userData)
            userDao.upsertUser(entity)
            syncUser(userData)
        } catch (e: Exception) {
            AuthResult.Error("${e.message}")
        }
    }

    override suspend fun getUser(userId: String): UserData? {
        return userDao.getUser(userId)?.toUserData()
    }

    override suspend fun syncUser(userData: UserData): AuthResult<Unit> {
        return userService.uploadUser(userData)
    }
}