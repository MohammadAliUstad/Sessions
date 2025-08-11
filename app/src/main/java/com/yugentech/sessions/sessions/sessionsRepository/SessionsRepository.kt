package com.yugentech.sessions.sessions.sessionsRepository

import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {
    suspend fun saveSession(userId: String, session: Session): SessionResult<Unit>
    fun getSessions(userId: String): Flow<List<Session>>
    fun getTotalDuration(userId: String): Flow<Long>
    suspend fun deleteSession(sessionId: String): SessionResult<Unit>
    suspend fun deleteAllSessions(userId: String): SessionResult<Unit>
    suspend fun getPendingSessions(userId: String): List<Session>
    suspend fun syncSessions(userId: String): SessionResult<Unit>
    suspend fun fetchSessionsOnce(userId: String): SessionResult<Unit>
}