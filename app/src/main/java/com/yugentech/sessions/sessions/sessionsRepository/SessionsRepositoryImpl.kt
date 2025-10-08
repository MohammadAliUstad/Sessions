package com.yugentech.sessions.sessions.sessionsRepository

import android.util.Log
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.room.daos.SessionsDao
import com.yugentech.sessions.room.entities.SessionsEntity
import com.yugentech.sessions.sessions.SessionsService
import com.yugentech.sessions.sessions.SyncPreferences
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val TAG = "SessionsRepository"

class SessionsRepositoryImpl(
    private val sessionsDao: SessionsDao,
    private val sessionService: SessionsService,
    private val syncPreferences: SyncPreferences
) : SessionsRepository {

    override suspend fun saveSession(userId: String, session: Session): SessionResult<Unit> {
        return try {
            Log.d(TAG, "Saving session for user: $userId, session: $session")
            val entity = SessionsEntity.fromSession(session, userId)
            sessionsDao.saveSession(entity)
            Log.d(TAG, "Session saved successfully: $entity")
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving session: ${e.message}", e)
            SessionResult.Error(e.message ?: "Failed to save session")
        }
    }

    override fun getSessions(userId: String): Flow<List<Session>> {
        Log.d(TAG, "Fetching sessions for user: $userId")
        return sessionsDao.getSessions(userId)
            .map { entities ->
                Log.d(TAG, "Fetched ${entities.size} sessions from DB for user: $userId")
                entities.map { entity ->
                    entity.toSession().copy(sessionId = entity.sessionId)
                }
            }
    }

    override fun getTotalDuration(userId: String): Flow<Long> {
        Log.d(TAG, "Getting total duration for user: $userId")
        return sessionsDao.getTotalDuration(userId)
    }

    override suspend fun deleteSession(sessionId: String): SessionResult<Unit> {
        return try {
            Log.d(TAG, "Deleting session with ID: $sessionId")
            sessionsDao.deleteSession(sessionId)
            Log.d(TAG, "Session deleted (if existed): $sessionId")
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting session: ${e.message}", e)
            SessionResult.Error(e.message ?: "Failed to delete session")
        }
    }

    override suspend fun deleteAllSessions(userId: String): SessionResult<Unit> {
        return try {
            Log.d(TAG, "Deleting all sessions for user: $userId")
            sessionsDao.deleteAllSessions(userId)
            Log.d(TAG, "All sessions deleted for user: $userId")
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all sessions: ${e.message}", e)
            SessionResult.Error(e.message ?: "Failed to delete all sessions")
        }
    }

    override suspend fun getPendingSessions(userId: String): List<Session> {
        Log.d(TAG, "Fetching pending sessions for user: $userId")
        val pendingSessions = sessionsDao.getPendingSessions(userId).map {
            it.toSession().copy(sessionId = it.sessionId)
        }
        Log.d(TAG, "Pending sessions count: ${pendingSessions.size}")
        return pendingSessions
    }

    override suspend fun syncSessions(userId: String): SessionResult<Unit> {
        return try {
            Log.d(TAG, "Syncing sessions for user: $userId")
            val pendingSessions = sessionsDao.getPendingSessions(userId)
            Log.d(TAG, "Pending sessions to sync: ${pendingSessions.size}")

            if (pendingSessions.isNotEmpty()) {
                val sessions = pendingSessions.map {
                    it.toSession().copy(sessionId = it.sessionId)
                }
                when (val result = sessionService.uploadPendingSessions(userId, sessions)) {
                    is SessionResult.Success -> {
                        Log.d(TAG, "Successfully uploaded sessions to remote for user: $userId")
                        sessionsDao.syncSessions(userId)
                        Log.d(TAG, "Marked sessions as synced in local DB for user: $userId")
                        SessionResult.Success(Unit)
                    }

                    is SessionResult.Error -> {
                        Log.e(TAG, "Failed to upload sessions: ${result.message}")
                        result
                    }
                }
            } else {
                Log.d(TAG, "No pending sessions to sync for user: $userId")
                SessionResult.Success(Unit)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing sessions: ${e.message}", e)
            SessionResult.Error(e.message ?: "Failed to sync sessions")
        }
    }

    override suspend fun fetchSessionsOnce(userId: String): SessionResult<Unit> {
        return try {
            Log.d(TAG, "Fetching sessions once for user: $userId")
            val alreadyFetched = syncPreferences.isSessionsFetchDone().first()
            Log.d(TAG, "Already fetched before? $alreadyFetched")

            if (alreadyFetched) {
                return SessionResult.Success(Unit)
            }

            when (val result = sessionService.fetchAllSessions(userId)) {
                is SessionResult.Success -> {
                    val remoteSessions = result.data
                    Log.d(TAG, "Fetched ${remoteSessions.size} sessions from remote")

                    if (remoteSessions.isNotEmpty()) {
                        val entities = remoteSessions.map {
                            SessionsEntity.fromSession(
                                it,
                                userId,
                                sessionId = it.sessionId,
                                pendingSync = false
                            )
                        }
                        sessionsDao.saveSessions(entities)
                        Log.d(TAG, "Saved ${entities.size} sessions to local DB")
                    }

                    syncPreferences.setSessionsFetchDone(true)
                    Log.d(TAG, "Marked initial fetch as done")
                    SessionResult.Success(Unit)
                }

                is SessionResult.Error -> {
                    Log.e(TAG, "Error fetching sessions: ${result.message}")
                    result
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in fetchSessionsOnce: ${e.message}", e)
            SessionResult.Error(e.message ?: "Failed to fetch sessions")
        }
    }
}