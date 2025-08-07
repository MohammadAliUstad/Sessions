package com.yugentech.sessions.sessions

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.tasks.await

class SessionsService(
    private val firestore: FirebaseFirestore
) {

    private fun sessionsCollectionRef(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("sessions")

    suspend fun getSessionsFromFirestore(userId: String): SessionResult<List<Session>> {
        return try {
            val snapshot = sessionsCollectionRef(userId).get().await()
            val sessions = snapshot.documents.mapNotNull { doc ->
                Session.fromMap(doc.data ?: emptyMap())
            }
            SessionResult.Success(sessions)
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to fetch sessions")
        }
    }

    suspend fun batchUploadSessions(userId: String, sessions: List<Session>): SessionResult<Unit> {
        return try {
            if (sessions.isEmpty()) return SessionResult.Success(Unit)

            val batch = firestore.batch()
            val sessionsRef = sessionsCollectionRef(userId)

            sessions.forEach { session ->
                val docRef = sessionsRef.document()
                batch.set(docRef, session.toMap())
            }

            batch.commit().await()
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to batch upload sessions")
        }
    }
}