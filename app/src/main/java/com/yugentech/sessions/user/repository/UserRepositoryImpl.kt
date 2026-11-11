package com.yugentech.sessions.user.repository

import com.yugentech.sessions.auth.repository.AuthRepository
import com.yugentech.sessions.user.model.UserData
import com.yugentech.sessions.room.daos.UserDao
import com.yugentech.sessions.room.entities.UserEntity
import com.yugentech.sessions.sessions.datastore.SyncDataStore
import com.yugentech.sessions.user.result.UserResult
import com.yugentech.sessions.user.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val userService: UserService,
    private val syncDataStore: SyncDataStore,
    private val authRepository: AuthRepository
) : UserRepository {

    // A guest's profile stays on this phone: it's never uploaded
    private val isGuest: Boolean
        get() = authRepository.isGuest

    override fun getUserFlow(userId: String): Flow<UserData?> {
        return userDao.getUserFlow(userId)
            .map { entity -> entity?.toUserData() }
    }

    override suspend fun upsertUser(userData: UserData) {
        try {
            Timber.d("Upserting user locally: ${userData.userId}")
            val entity = UserEntity.fromUserData(userData)
            userDao.saveUser(entity)
        } catch (e: Exception) {
            Timber.e(e, "Failed to upsert user locally")
            throw e
        }
    }

    override suspend fun getUser(userId: String): UserData? {
        return userDao.getUser(userId)?.toUserData()
    }

    override suspend fun syncUser(userData: UserData): UserResult<Unit> {
        if (isGuest) return UserResult.Success(Unit)
        Timber.i("Syncing user data to cloud: ${userData.userId}")
        return userService.uploadUser(userData)
    }

    override suspend fun fetchUserOnce(userId: String): UserResult<Unit> {
        // A guest has no cloud profile: the one on this phone, or none yet (then it's created)
        if (isGuest) {
            return if (userDao.getUser(userId) != null) UserResult.Success(Unit)
            else UserResult.Error("No guest profile on this device yet")
        }
        return try {
            val alreadyFetched = syncDataStore.isUserFetchDone.first()

            if (alreadyFetched) {
                val localUser = userDao.getUser(userId)
                if (localUser != null) {
                    return UserResult.Success(Unit)
                }
                // Sync flag is set but local DB is empty — stale flag from a DB wipe.
                // Reset it so we fall through to a fresh Firestore fetch.
                Timber.w("Sync flag set but local user missing; re-fetching from cloud")
                syncDataStore.setUserFetchDone(false)
            }

            Timber.i("Performing initial user profile fetch")
            when (val result = userService.fetchUser(userId)) {
                is UserResult.Success -> {
                    val userData = result.data
                    Timber.d("Fetched user profile from cloud. Saving locally.")

                    val entity = UserEntity.fromUserData(userData)
                    userDao.saveUser(entity)

                    syncDataStore.setUserFetchDone(true)
                    UserResult.Success(Unit)
                }
                is UserResult.Error -> {
                    Timber.w("Failed to fetch user from cloud: ${result.message}")
                    result
                }
                is UserResult.Loading -> {
                    UserResult.Loading
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during user fetch")
            UserResult.Error(e.message ?: "Failed to fetch user")
        }
    }
}