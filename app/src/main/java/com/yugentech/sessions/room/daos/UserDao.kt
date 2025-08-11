package com.yugentech.sessions.room.daos

import androidx.room.*
import com.yugentech.sessions.room.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserFlow(userId: String): Flow<UserEntity?>

    @Upsert
    suspend fun upsertUser(user: UserEntity)
}