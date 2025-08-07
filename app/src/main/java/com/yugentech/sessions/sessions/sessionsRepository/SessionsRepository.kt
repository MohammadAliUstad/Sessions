package com.yugentech.sessions.sessions.sessionsRepository

import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {
    // UI functions - instant from Room
    fun getSessionsFlow(userId: String): Flow<List<Session>>
    suspend fun saveSession(userId: String, session: Session): SessionResult<Unit>

    // Background sync functions
    suspend fun syncSessionsToFirestore(userId: String): SessionResult<Unit>
    suspend fun downloadSessionsFromFirestore(userId: String): SessionResult<List<Session>>
    suspend fun getSessionsPendingSync(): List<Session>
}