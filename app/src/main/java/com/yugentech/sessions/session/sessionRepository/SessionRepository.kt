package com.yugentech.sessions.session.sessionRepository

import com.yugentech.sessions.models.Session
import com.yugentech.sessions.session.sessionUtils.SessionResult
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun saveSession(userId: String, session: Session): SessionResult<Unit>
    suspend fun updateTotalTime(userId: String, additionalSeconds: Int): SessionResult<Unit>
    fun getSessions(userId: String): Flow<List<Session>>
    fun getTotalTime(userId: String): Flow<Long>
}