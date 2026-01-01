package com.yugentech.sessions.sessions.sessionsRepository

import com.yugentech.sessions.models.Session
import com.yugentech.sessions.room.daos.SessionsDao
import com.yugentech.sessions.room.entities.SessionsEntity
import com.yugentech.sessions.sessions.SessionsService
import com.yugentech.sessions.sessions.SyncPreferences
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

class SessionsRepositoryImpl(
    private val sessionsDao: SessionsDao,
    private val sessionService: SessionsService,
    private val syncPreferences: SyncPreferences
) : SessionsRepository {

    override suspend fun saveSession(userId: String, session: Session): SessionResult<Unit> {
        return try {
            Timber.d("Saving session locally: ${session.sessionId}")
            val entity = SessionsEntity.fromSession(session, userId)
            sessionsDao.saveSession(entity)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save session locally")
            SessionResult.Error(e.message ?: "Failed to save session")
        }
    }

    override fun getSessionsFlow(userId: String): Flow<List<Session>> {
        return sessionsDao.getSessionsFlow(userId)
            .map { entities ->
                entities.map { entity ->
                    entity.toSession().copy(sessionId = entity.sessionId)
                }
            }
    }

    override fun getTotalDuration(userId: String): Flow<Long> {
        return sessionsDao.getTotalDuration(userId)
    }

    override suspend fun deleteSession(userId: String, sessionId: String): SessionResult<Unit> {
        return try {
            Timber.i("Deleting session: $sessionId")
            sessionsDao.deleteSession(sessionId)
            sessionService.deleteSession(userId, sessionId)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete session")
            SessionResult.Error(e.message ?: "Failed to delete session")
        }
    }

    override suspend fun deleteAllSessions(userId: String): SessionResult<Unit> {
        return try {
            Timber.w("Deleting ALL sessions for user: $userId")
            sessionsDao.deleteAllSessions(userId)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete all sessions")
            SessionResult.Error(e.message ?: "Failed to delete all sessions")
        }
    }

    override suspend fun syncSessions(userId: String): SessionResult<Unit> {
        return try {
            val pendingSessions = sessionsDao.getPendingSessions(userId)

            if (pendingSessions.isNotEmpty()) {
                Timber.d("Found ${pendingSessions.size} pending sessions to sync")
                val sessions = pendingSessions.map {
                    it.toSession().copy(sessionId = it.sessionId)
                }

                when (val result = sessionService.uploadPendingSessions(userId, sessions)) {
                    is SessionResult.Success -> {
                        Timber.i("Successfully synced sessions to cloud")
                        sessionsDao.syncSessions(userId)
                        SessionResult.Success(Unit)
                    }
                    is SessionResult.Error -> {
                        Timber.e("Cloud sync failed: ${result.message}")
                        result
                    }
                }
            } else {
                SessionResult.Success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during session sync")
            SessionResult.Error(e.message ?: "Failed to sync sessions")
        }
    }

    override suspend fun fetchSessionsOnce(userId: String): SessionResult<Unit> {
        return try {
            val alreadyFetched = syncPreferences.isSessionsFetchDone().first()

            if (alreadyFetched) {
                return SessionResult.Success(Unit)
            }

            Timber.i("Performing initial session fetch from cloud")
            when (val result = sessionService.fetchAllSessions(userId)) {
                is SessionResult.Success -> {
                    val remoteSessions = result.data

                    if (remoteSessions.isNotEmpty()) {
                        Timber.d("Fetched ${remoteSessions.size} sessions from cloud")
                        val entities = remoteSessions.map {
                            SessionsEntity.fromSession(
                                it,
                                userId,
                                sessionId = it.sessionId,
                                pendingSync = false
                            )
                        }
                        sessionsDao.saveSessions(entities)
                    }

                    syncPreferences.setSessionsFetchDone(true)
                    SessionResult.Success(Unit)
                }
                is SessionResult.Error -> {
                    Timber.e("Failed to fetch cloud sessions: ${result.message}")
                    result
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during initial session fetch")
            SessionResult.Error(e.message ?: "Failed to fetch sessions")
        }
    }
}