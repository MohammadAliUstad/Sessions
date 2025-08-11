package com.yugentech.sessions.sessions.sessionsRepository

import com.yugentech.sessions.models.Session
import com.yugentech.sessions.room.daos.SessionsDao
import com.yugentech.sessions.room.entities.SessionsEntity
import com.yugentech.sessions.sessions.SessionPreferences
import com.yugentech.sessions.sessions.SessionsService
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SessionsRepositoryImpl(
    private val sessionsDao: SessionsDao,
    private val sessionService: SessionsService,
    private val sessionPreferences: SessionPreferences
) : SessionsRepository {

    override suspend fun saveSession(userId: String, session: Session): SessionResult<Unit> {
        return try {
            val entity = SessionsEntity.fromSession(session, userId)
            sessionsDao.saveSession(entity)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to save session")
        }
    }

    override fun getSessions(userId: String): Flow<List<Session>> {
        return sessionsDao.getSessions(userId).map { entities ->
            entities.map { it.toSession() }
        }
    }

    override fun getTotalDuration(userId: String): Flow<Long> {
        return sessionsDao.getTotalDuration(userId)
    }

    override suspend fun deleteSession(sessionId: String): SessionResult<Unit> {
        return try {
            sessionsDao.deleteSession(sessionId)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to delete session")
        }
    }

    override suspend fun deleteAllSessions(userId: String): SessionResult<Unit> {
        return try {
            sessionsDao.deleteAllSessions(userId)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to delete all sessions")
        }
    }

    override suspend fun getPendingSessions(userId: String): List<Session> {
        return sessionsDao.getPendingSessions(userId).map { it.toSession() }
    }

    override suspend fun syncSessions(userId: String): SessionResult<Unit> {
        return try {
            val pendingSessions = sessionsDao.getPendingSessions(userId)
            if (pendingSessions.isNotEmpty()) {
                val sessions = pendingSessions.map { it.toSession() }
                when (val result = sessionService.uploadPendingSessions(userId, sessions)) {
                    is SessionResult.Success -> {
                        sessionsDao.syncSessions(userId)
                        SessionResult.Success(Unit)
                    }

                    is SessionResult.Error -> result
                }
            } else {
                SessionResult.Success(Unit)
            }
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to sync sessions")
        }
    }

    override suspend fun fetchSessionsOnce(userId: String): SessionResult<Unit> {
        return try {
            val alreadyFetched = sessionPreferences.isInitialFetchDone()
                .first() // Flow<Boolean> → Boolean

            if (alreadyFetched) {
                return SessionResult.Success(Unit)
            }

            when (val result = sessionService.fetchAllSessions(userId)) {
                is SessionResult.Success -> {
                    val remoteSessions = result.data

                    if (remoteSessions.isNotEmpty()) {
                        val entities = remoteSessions.map {
                            SessionsEntity.fromSession(
                                it,
                                userId,
                                pendingSync = false
                            )
                        }
                        sessionsDao.saveSessions(entities) // DAO expects List<SessionsEntity>
                    }

                    sessionPreferences.setInitialFetchDone(true)
                    SessionResult.Success(Unit)
                }

                is SessionResult.Error -> result
            }
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to fetch sessions")
        }
    }
}