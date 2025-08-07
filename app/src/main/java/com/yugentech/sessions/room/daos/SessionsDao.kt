package com.yugentech.sessions.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yugentech.sessions.room.entities.SessionsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionsDao {

    @Query("SELECT * FROM sessions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSessionsFlow(userId: String): Flow<List<SessionsEntity>>

    @Query("SELECT * FROM sessions WHERE pendingSync = 1")
    suspend fun getSessionsPendingSync(): List<SessionsEntity>

    @Query("SELECT * FROM sessions WHERE userId = :userId AND pendingSync = 1")
    suspend fun getUserSessionsPendingSync(userId: String): List<SessionsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)  // Removed .Companion
    suspend fun insertSession(session: SessionsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)  // Removed .Companion
    suspend fun insertSessions(sessions: List<SessionsEntity>)

    @Query("UPDATE sessions SET pendingSync = 0 WHERE userId = :userId")
    suspend fun markAllSynced(userId: String)

    @Query("DELETE FROM sessions WHERE userId = :userId")
    suspend fun deleteAllUserSessions(userId: String)
}