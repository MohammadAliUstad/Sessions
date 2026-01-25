package com.yugentech.sessions.sessions

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class SessionsService(
    private val firestore: FirebaseFirestore
) {

    // Helper to reference the "sessions" sub-collection for a specific user
    private fun userSessionsCollection(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("sessions")

    suspend fun deleteSession(userId: String, sessionId: String): SessionResult<Unit> {
        return try {
            Timber.d("Deleting session $sessionId from cloud for user: $userId")

            // Locate the specific document and delete it asynchronously
            userSessionsCollection(userId)
                .document(sessionId)
                .delete()
                .await()

            Timber.i("Successfully deleted session from cloud")
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete session from cloud")
            SessionResult.Error(e.message ?: "Failed to delete session")
        }
    }

    suspend fun uploadPendingSessions(userId: String, sessions: List<Session>): SessionResult<Unit> {
        return try {
            if (sessions.isEmpty()) {
                return SessionResult.Success(Unit)
            }

            Timber.i("Uploading ${sessions.size} pending sessions for user: $userId")
            // Create a write batch to upload multiple sessions atomically
            val batch = firestore.batch()
            val sessionsRef = userSessionsCollection(userId)

            sessions.forEach { session ->
                val docRef = sessionsRef.document(session.sessionId)
                batch.set(docRef, session.toMap())
            }

            // Commit all changes to Firestore at once
            batch.commit().await()
            Timber.i("Successfully uploaded pending sessions")
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to upload pending sessions")
            SessionResult.Error(e.message ?: "Failed to upload pending sessions")
        }
    }

    suspend fun fetchAllSessions(userId: String): SessionResult<List<Session>> {
        return try {
            Timber.d("Fetching all sessions for user: $userId")
            // Retrieve all documents in the user's sessions collection
            val snapshot = userSessionsCollection(userId).get().await()
            val sessions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Session::class.java)
            }
            Timber.i("Fetched ${sessions.size} sessions from cloud")
            SessionResult.Success(sessions)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch sessions from cloud")
            SessionResult.Error(e.message ?: "Failed to fetch sessions")
        }
    }
}