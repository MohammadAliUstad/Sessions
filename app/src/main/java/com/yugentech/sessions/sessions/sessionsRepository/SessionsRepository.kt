package com.yugentech.sessions.sessions.sessionsRepository

import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.flow.Flow

// Defines the contract for session data management (Local + Remote)
interface SessionsRepository {
    // Saves a session locally and marks it for sync
    suspend fun saveSession(userId: String, session: Session): SessionResult<Unit>

    // Observable flow of all sessions for a user, ordered by date
    fun getSessionsFlow(userId: String): Flow<List<Session>>

    // Observable flow of the total focus time for a user
    fun getTotalDuration(userId: String): Flow<Long>

    // Deletes a specific session by ID
    suspend fun deleteSession(userId: String, sessionId: String): SessionResult<Unit>

    // Clears all session history for a user
    suspend fun deleteAllSessions(userId: String): SessionResult<Unit>

    // Uploads pending local sessions to the cloud
    suspend fun syncSessions(userId: String): SessionResult<Unit>

    // Performs an initial fetch of cloud sessions (run once per install/login)
    suspend fun fetchSessionsOnce(userId: String): SessionResult<Unit>
}