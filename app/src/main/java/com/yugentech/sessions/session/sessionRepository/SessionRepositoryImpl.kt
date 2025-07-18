package com.yugentech.sessions.session.sessionRepository

import com.yugentech.sessions.models.Session
import com.yugentech.sessions.session.SessionService
import com.yugentech.sessions.session.sessionUtils.SessionResult
import kotlinx.coroutines.flow.Flow

class SessionRepositoryImpl(
    private val sessionService: SessionService
) : SessionRepository {

    override suspend fun saveSession(userId: String, session: Session): SessionResult<Unit> {
        return sessionService.saveSession(userId, session)
    }

    override suspend fun updateTotalTime(
        userId: String,
        additionalSeconds: Int
    ): SessionResult<Unit> {
        return sessionService.updateTotalTime(userId, additionalSeconds)
    }

    override fun getSessions(userId: String): Flow<List<Session>> {
        return sessionService.getSessions(userId)
    }

    override fun getTotalTime(userId: String): Flow<Long> {
        return sessionService.getTotalTime(userId)
    }
}