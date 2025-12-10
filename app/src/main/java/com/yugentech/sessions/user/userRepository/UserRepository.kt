package com.yugentech.sessions.user.userRepository

import com.yugentech.sessions.models.UserData
import com.yugentech.sessions.user.UserResult
import kotlinx.coroutines.flow.Flow

// Defines the contract for managing user profile data (Local + Remote)
interface UserRepository {
    // Retrieves user data from the local database immediately
    suspend fun getUser(userId: String): UserData?

    // Observable flow of user data updates
    fun getUserFlow(userId: String): Flow<UserData?>

    // Saves or updates user data in the local database
    suspend fun upsertUser(userData: UserData)

    // Uploads the current user data to the cloud
    suspend fun syncUser(userData: UserData): UserResult<Unit>

    // Performs an initial fetch of the user profile from the cloud
    suspend fun fetchUserOnce(userId: String): UserResult<Unit>
}