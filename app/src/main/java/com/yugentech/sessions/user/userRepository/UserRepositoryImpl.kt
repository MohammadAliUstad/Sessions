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
import timber.log.Timber

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val userService: UserService,
    private val syncPreferences: SyncPreferences
) : UserRepository {

    override fun getUserFlow(userId: String): Flow<UserData?> {
        return userDao.getUserFlow(userId)
            .map { entity -> entity?.toUserData() }
    }

    override suspend fun upsertUser(userData: UserData) {
        try {
            Timber.d("Upserting user locally: ${userData.userId}")
            val entity = UserEntity.fromUserData(userData)
            userDao.upsertUser(entity)
        } catch (e: Exception) {
            Timber.e(e, "Failed to upsert user locally")
            throw e
        }
    }

    override suspend fun getUser(userId: String): UserData? {
        return userDao.getUser(userId)?.toUserData()
    }

    override suspend fun syncUser(userData: UserData): UserResult<Unit> {
        Timber.i("Syncing user data to cloud: ${userData.userId}")
        return userService.uploadUser(userData)
    }

    override suspend fun fetchUserOnce(userId: String): UserResult<Unit> {
        return try {
            val alreadyFetched = syncPreferences.isUserFetchDone().first()

            if (alreadyFetched) {
                return UserResult.Success(Unit)
            }

            Timber.i("Performing initial user profile fetch")
            when (val result = userService.fetchUser(userId)) {
                is UserResult.Success -> {
                    val userData = result.data
                    Timber.d("Fetched user profile from cloud. Saving locally.")

                    val entity = UserEntity.fromUserData(userData)
                    userDao.upsertUser(entity)

                    syncPreferences.setUserFetchDone(true)
                    UserResult.Success(Unit)
                }
                is UserResult.Error -> {
                    Timber.w("Failed to fetch user from cloud: ${result.message}")
                    result
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during user fetch")
            UserResult.Error(e.message ?: "Failed to fetch user")
        }
    }
}