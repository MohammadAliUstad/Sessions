package com.yugentech.sessions.room.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.yugentech.sessions.room.entities.UserEntity
import kotlinx.coroutines.flow.Flow

// Interface defining database operations for user profile data
@Dao
interface UserDao {

    // Inserts or updates a user profile
    @Upsert
    suspend fun saveUser(user: UserEntity)

    // Retrieves a user profile directly by ID
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): UserEntity?

    // Observes changes to a user profile in real-time
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserFlow(userId: String): Flow<UserEntity?>
}