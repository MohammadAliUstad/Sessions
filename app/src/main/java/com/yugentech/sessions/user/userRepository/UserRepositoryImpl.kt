package com.yugentech.sessions.user.userRepository

import android.util.Log
import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.room.entities.UserEntity
import com.yugentech.sessions.sessions.SyncPreferences
import com.yugentech.sessions.user.UserResult
import com.yugentech.sessions.user.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val TAG = "UserRepository"

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

    override suspend fun upsertUser(userData: UserData): UserResult<Unit> {
        return try {
            val entity = UserEntity.fromUserData(userData)
            userDao.upsertUser(entity)
            syncUser(userData)
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
            Log.d(TAG, "Fetching user once for: $userId")
            val alreadyFetched = syncPreferences.isUserFetchDone().first()
            Log.d(TAG, "User already fetched? $alreadyFetched")

            if (alreadyFetched) {
                return UserResult.Success(Unit)
            }

            when (val result = userService.fetchUser(userId)) {
                is UserResult.Success -> {
                    val userData = result.data
                    val entity = UserEntity.fromUserData(userData)
                    userDao.upsertUser(entity)
                    syncPreferences.setUserFetchDone(true)
                    Log.d(TAG, "User saved to local DB and marked fetch as done")
                    UserResult.Success(Unit)
                }

                is UserResult.Error -> {
                    Log.e(TAG, "Error fetching user: ${result.message}")
                    result
                }

                is UserResult.Loading -> {
                    Log.d(TAG, "User fetch in progress...")
                    UserResult.Loading
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in fetchUserOnce: ${e.message}", e)
            UserResult.Error(e.message ?: "Failed to fetch user")
        }
    }
}