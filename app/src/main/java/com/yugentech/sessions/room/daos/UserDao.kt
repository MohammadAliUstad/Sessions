package com.yugentech.sessions.room.daos

import androidx.room.*
import com.yugentech.sessions.room.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): UserEntity?

    @Query("SELECT totalTimeStudied FROM users WHERE userId = :userId")
    fun getTotalTimeFlow(userId: String): Flow<Long?>

    @Query("SELECT * FROM users WHERE pendingSync = 1")
    suspend fun getUsersPendingSync(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET totalTimeStudied = totalTimeStudied + :additionalSeconds, pendingSync = 1 WHERE userId = :userId")
    suspend fun addToTotalTime(userId: String, additionalSeconds: Int)

    @Query("UPDATE users SET pendingSync = 0 WHERE userId = :userId")
    suspend fun markSynced(userId: String)
}