package com.yugentech.sessions.sessions

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.tasks.await

class SessionsService(
    private val firestore: FirebaseFirestore
) {



    suspend fun uploadPendingSessions(userId: String, sessions: List<Session>): SessionResult<Unit> {
        return try {
            if (sessions.isEmpty()) {
                return SessionResult.Success(Unit)
            }

            val batch = firestore.batch()
            val sessionsRef = userSessionsCollection(userId)

            sessions.forEach { session ->
                val docRef = sessionsRef.document(session.sessionId)
                batch.set(docRef, session.toMap())
            }

            batch.commit().await()
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to upload pending sessions")
        }
    }

    suspend fun fetchAllSessions(userId: String): SessionResult<List<Session>> {
        return try {
            val snapshot = userSessionsCollection(userId).get().await()
            val sessions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Session::class.java)
            }
            SessionResult.Success(sessions)
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to fetch sessions")
        }
    }
}