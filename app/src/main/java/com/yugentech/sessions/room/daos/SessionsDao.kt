package com.yugentech.sessions.room.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.yugentech.sessions.room.entities.SessionsEntity
import kotlinx.coroutines.flow.Flow

// Interface defining database operations for session data
@Dao
interface SessionsDao {

    // Inserts or updates a single session record
    @Upsert
    suspend fun saveSession(session: SessionsEntity)

    // Inserts or updates multiple session records efficiently
    @Upsert
    suspend fun saveSessions(sessions: List<SessionsEntity>)

    // Observes all sessions for a user, ordered by most recent
    @Query("SELECT * FROM sessions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSessionsFlow(userId: String): Flow<List<SessionsEntity>>

    // Removes a specific session from the database
    @Query("DELETE FROM sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    // Removes multiple sessions by their IDs
    @Query("DELETE FROM sessions WHERE sessionId IN (:sessionIds)")
    suspend fun deleteSessions(sessionIds: List<String>)

    // Removes all sessions for a specific user
    @Query("DELETE FROM sessions WHERE userId = :userId")
    suspend fun deleteAllSessions(userId: String)

    // Observes the sum of all session durations for a user
    @Query("SELECT IFNULL(SUM(duration), 0) FROM sessions WHERE userId = :userId")
    fun getTotalDuration(userId: String): Flow<Long>

    // Retrieves sessions that haven't been synced to the server yet
    @Query("SELECT * FROM sessions WHERE userId = :userId AND pendingSync = 1")
    suspend fun getPendingSessions(userId: String): List<SessionsEntity>

    // Marks all pending sessions for a user as synced
    @Query("UPDATE sessions SET pendingSync = 0 WHERE userId = :userId AND pendingSync = 1")
    suspend fun syncSessions(userId: String)

    // A one-off read of every session of a user, for writing a guest's backup file
    @Query("SELECT * FROM sessions WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getSessionsOnce(userId: String): List<SessionsEntity>

    // Which of these session IDs are already on this phone (under any user), so an import skips them
    @Query("SELECT sessionId FROM sessions WHERE sessionId IN (:sessionIds)")
    suspend fun getExistingIds(sessionIds: List<String>): List<String>
}