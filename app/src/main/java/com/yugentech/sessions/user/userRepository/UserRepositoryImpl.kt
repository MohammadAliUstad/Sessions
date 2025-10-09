package com.yugentech.sessions.user.userRepository

import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.room.entities.UserEntity
import com.yugentech.sessions.sessions.SyncPreferences
import com.yugentech.sessions.user.UserResult
import com.yugentech.sessions.user.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val userService: UserService,
    private val syncPreferences: SyncPreferences
) : UserRepository {


    override fun getUserFlow(userId: String): Flow<UserData?> {
        return userDao.getUserFlow(userId).map { entity ->
            entity?.toUserData()
        }
    }

    override suspend fun upsertUser(userData: UserData) {
        try {
            val entity = UserEntity.fromUserData(userData)
            userDao.upsertUser(entity)
        } catch (e: Exception) {
            UserResult.Error("${e.message}")
        }
    }

    override suspend fun getUser(userId: String): UserData? {
        return userDao.getUser(userId)?.toUserData()
    }

    override suspend fun syncUser(userData: UserData): UserResult<Unit> {
        return userService.uploadUser(userData)
    }

    override suspend fun fetchUserOnce(userId: String): UserResult<Unit> {
        return try {
            val alreadyFetched = syncPreferences.isUserFetchDone().first()

            if (alreadyFetched) {
                return UserResult.Success(Unit)
            }

            val result = userService.fetchUser(userId)

            when (result) {
                is UserResult.Success -> {
                    val userData = result.data

                    val entity = UserEntity.fromUserData(userData)
                    userDao.upsertUser(entity)
                    syncPreferences.setUserFetchDone(true)
                    UserResult.Success(Unit)
                }

                is UserResult.Error -> {
                    result
                }

                is UserResult.Loading -> {
                    UserResult.Loading
                }
            }
        } catch (e: Exception) {
            UserResult.Error(e.message ?: "Failed to fetch user")
        }
    }
}