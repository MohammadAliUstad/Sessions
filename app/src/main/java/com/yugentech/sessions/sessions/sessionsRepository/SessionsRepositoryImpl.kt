package com.yugentech.sessions.sessions.sessionsRepository

import com.yugentech.sessions.models.Session
import com.yugentech.sessions.room.daos.SessionsDao
import com.yugentech.sessions.room.entities.SessionsEntity
import com.yugentech.sessions.sessions.SessionsService
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import com.yugentech.sessions.user.userRepository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionsRepositoryImpl(
    private val sessionsDao: SessionsDao,
    private val sessionService: SessionsService,
    private val userRepository: UserRepository
) : SessionsRepository {

    override fun getSessionsFlow(userId: String): Flow<List<Session>> {
        return sessionsDao.getSessionsFlow(userId).map { entities ->
            entities.map { it.toSession() }
        }
    }

    override suspend fun saveSession(userId: String, session: Session): SessionResult<Unit> {
        return try {
            val entity = SessionsEntity.fromSession(session, userId)
            sessionsDao.insertSession(entity)
            userRepository.addStudyTime(userId, session.duration)
            syncSessionsToFirestore(userId)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to save session")
        }
    }

    override suspend fun syncSessionsToFirestore(userId: String): SessionResult<Unit> {
        return try {
            val pendingSessions = sessionsDao.getUserSessionsPendingSync(userId)

            if (pendingSessions.isNotEmpty()) {
                val sessions = pendingSessions.map { it.toSession() }

                when (val result = sessionService.batchUploadSessions(userId, sessions)) {
                    is SessionResult.Success -> {
                        sessionsDao.markAllSynced(userId)
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

    override suspend fun downloadSessionsFromFirestore(userId: String): SessionResult<List<Session>> {
        return when (val result = sessionService.getSessionsFromFirestore(userId)) {
            is SessionResult.Success -> {
                try {
                    val entities = result.data.map { session ->
                        SessionsEntity.fromSession(session, userId).copy(pendingSync = false)
                    }
                    sessionsDao.insertSessions(entities)
                    result
                } catch (_: Exception) {
                    SessionResult.Error("Failed to save sessions locally")
                }
            }

            is SessionResult.Error -> result
        }
    }

    override suspend fun getSessionsPendingSync(): List<Session> {
        return try {
            sessionsDao.getSessionsPendingSync().map { it.toSession() }
        } catch (_: Exception) {
            emptyList()
        }
    }
}