package com.yugentech.sessions.sessions.repository

import com.yugentech.sessions.sessions.model.Session
import com.yugentech.sessions.sessions.result.SessionResult
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {
    // Save a completed session to local storage
    suspend fun saveSession(session: Session): SessionResult<Unit>

    // Observe the list of sessions as a stream of data
    fun getSessionsFlow(): Flow<List<Session>>

    // Observe the total duration of all sessions
    fun getTotalDuration(): Flow<Long>

    // Remove a session from both local storage and the server
    suspend fun deleteSession(sessionId: String): SessionResult<Unit>

    // Remove multiple sessions from both local storage and the server
    suspend fun deleteSessions(sessionIds: List<String>): SessionResult<Unit>

    // Remove all sessions for the current user
    suspend fun deleteAllSessions(): SessionResult<Unit>

    // Upload any sessions recorded offline to the server
    suspend fun syncSessions(): SessionResult<Unit>

    // Download sessions from the server only once (usually on first login)
    suspend fun fetchSessionsOnce(): SessionResult<Unit>
}