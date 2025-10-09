package com.yugentech.sessions.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yugentech.sessions.room.entities.SessionsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: SessionsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSessions(sessions: List<SessionsEntity>)

    @Query("SELECT * FROM sessions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSessions(userId: String): Flow<List<SessionsEntity>>

    @Query("DELETE FROM sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM sessions WHERE userId = :userId")
    suspend fun deleteAllSessions(userId: String)

    @Query("SELECT IFNULL(SUM(duration), 0) FROM sessions WHERE userId = :userId")
    fun getTotalDuration(userId: String): Flow<Long>

    @Query("SELECT * FROM sessions WHERE userId = :userId AND pendingSync = 1")
    suspend fun getPendingSessions(userId: String): List<SessionsEntity>

    @Query("UPDATE sessions SET pendingSync = 0 WHERE userId = :userId AND pendingSync = 1")
    suspend fun syncSessions(userId: String)

    @Query("SELECT sessionId FROM sessions WHERE userId = :userId")
    suspend fun getSessionIds(userId: String): List<String>

    @Query("SELECT COUNT(*) FROM sessions WHERE sessionId = :sessionId")
    suspend fun checkSessionExists(sessionId: String): Int
}