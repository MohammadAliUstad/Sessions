package com.yugentech.sessions.sessions.sessionsRepository

import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {
    suspend fun saveSession(session: Session): SessionResult<Unit>
    fun getSessionsFlow(): Flow<List<Session>>
    fun getTotalDuration(): Flow<Long>
    suspend fun deleteSession(sessionId: String): SessionResult<Unit>
    suspend fun syncSessions(): SessionResult<Unit>
    suspend fun fetchSessionsOnce(): SessionResult<Unit>
}