package com.yugentech.sessions.sessions.repository

import com.yugentech.sessions.auth.repository.AuthRepository
import com.yugentech.sessions.sessions.model.Session
import com.yugentech.sessions.room.daos.SessionsDao
import com.yugentech.sessions.room.entities.SessionsEntity
import com.yugentech.sessions.sessions.service.SessionsService
import com.yugentech.sessions.sessions.datastore.SyncDataStore
import com.yugentech.sessions.sessions.result.SessionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

class SessionsRepositoryImpl(
    private val sessionsDao: SessionsDao,
    private val sessionService: SessionsService,
    private val syncDataStore: SyncDataStore,
    private val authRepository: AuthRepository
) : SessionsRepository {

    // Helper to get the current user ID or null if not logged in
    private val currentUserId: String?
        get() = authRepository.currentUser

    override suspend fun saveSession(session: Session): SessionResult<Unit> {
        // Ensure user is logged in before saving
        val userId = currentUserId ?: return SessionResult.Error("User not logged in")

        return try {
            Timber.d("Saving session locally for user $userId: ${session.sessionId}")
            // Convert the domain model to a database entity and save it
            val entity = SessionsEntity.fromSession(session, userId)
            sessionsDao.saveSession(entity)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save session locally")
            SessionResult.Error(e.message ?: "Failed to save session")
        }
    }

    override fun getSessionsFlow(): Flow<List<Session>> {
        val userId = currentUserId ?: return emptyFlow()

        // Observe database changes and convert entities back to domain models
        return sessionsDao.getSessionsFlow(userId)
            .map { entities ->
                entities.map { entity ->
                    entity.toSession().copy(sessionId = entity.sessionId)
                }
            }
    }

    override fun getTotalDuration(): Flow<Long> {
        val userId = currentUserId ?: return emptyFlow()
        // Return a live stream of the total focus time
        return sessionsDao.getTotalDuration(userId)
    }

    override suspend fun deleteSession(sessionId: String): SessionResult<Unit> {
        val userId = currentUserId ?: return SessionResult.Error("User not logged in")

        return try {
            Timber.i("Deleting session: $sessionId")
            // Delete from local database first
            sessionsDao.deleteSession(sessionId)
            // Then attempt to delete from the server
            sessionService.deleteSession(userId, sessionId)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete session")
            SessionResult.Error(e.message ?: "Failed to delete session")
        }
    }

    override suspend fun deleteSessions(sessionIds: List<String>): SessionResult<Unit> {
        val userId = currentUserId ?: return SessionResult.Error("User not logged in")

        return try {
            Timber.i("Deleting ${sessionIds.size} sessions")
            sessionsDao.deleteSessions(sessionIds)
            sessionService.deleteSessions(userId, sessionIds)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete sessions batch")
            SessionResult.Error(e.message ?: "Failed to delete sessions")
        }
    }

    override suspend fun deleteAllSessions(): SessionResult<Unit> {
        val userId = currentUserId ?: return SessionResult.Error("User not logged in")

        return try {
            Timber.i("Deleting all sessions for user: $userId")
            sessionsDao.deleteAllSessions(userId)
            sessionService.deleteAllSessions(userId)
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete all sessions")
            SessionResult.Error(e.message ?: "Failed to delete all sessions")
        }
    }

    override suspend fun syncSessions(): SessionResult<Unit> {
        val userId = currentUserId ?: return SessionResult.Error("User not logged in")

        return try {
            // Find sessions that haven't been uploaded yet
            val pendingSessions = sessionsDao.getPendingSessions(userId)

            if (pendingSessions.isNotEmpty()) {
                Timber.d("Found ${pendingSessions.size} pending sessions to sync")
                val sessions = pendingSessions.map {
                    it.toSession().copy(sessionId = it.sessionId)
                }

                // Upload the pending sessions to the cloud
                when (val result = sessionService.uploadPendingSessions(userId, sessions)) {
                    is SessionResult.Success -> {
                        Timber.i("Successfully synced sessions to cloud")
                        // Mark them as synced in the local database
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

    override suspend fun fetchSessionsOnce(): SessionResult<Unit> {
        val userId = currentUserId ?: return SessionResult.Error("User not logged in")

        return try {
            // Check preferences to see if we already downloaded the initial data
            val alreadyFetched = syncDataStore.isSessionsFetchDone.first()

            if (alreadyFetched) {
                return SessionResult.Success(Unit)
            }

            Timber.i("Performing initial session fetch from cloud")
            // Fetch all sessions from the server
            when (val result = sessionService.fetchAllSessions(userId)) {
                is SessionResult.Success -> {
                    val remoteSessions = result.data

                    if (remoteSessions.isNotEmpty()) {
                        Timber.d("Fetched ${remoteSessions.size} sessions from cloud")
                        // Convert remote data to entities and save them locally
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

                    // Update preference so we don't fetch everything again
                    syncDataStore.setSessionsFetchDone(true)
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