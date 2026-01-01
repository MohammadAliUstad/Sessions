package com.yugentech.sessions.sessions

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.sessions.sessionsUtils.SessionResult
import kotlinx.coroutines.tasks.await
import timber.log.Timber

// Service for handling direct Cloud Firestore operations related to sessions
class SessionsService(
    private val firestore: FirebaseFirestore
) {

    // Helper to get the reference for a specific user's sessions collection
    private fun userSessionsCollection(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("sessions")

    // Deletes a specific session from Firestore
    suspend fun deleteSession(userId: String, sessionId: String): SessionResult<Unit> {
        return try {
            Timber.d("Deleting session $sessionId from cloud for user: $userId")

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

    // Uploads a batch of locally completed sessions to Firestore
    suspend fun uploadPendingSessions(userId: String, sessions: List<Session>): SessionResult<Unit> {
        return try {
            if (sessions.isEmpty()) {
                return SessionResult.Success(Unit)
            }

            Timber.i("Uploading ${sessions.size} pending sessions for user: $userId")
            val batch = firestore.batch()
            val sessionsRef = userSessionsCollection(userId)

            sessions.forEach { session ->
                val docRef = sessionsRef.document(session.sessionId)
                batch.set(docRef, session.toMap())
            }

            batch.commit().await()
            Timber.i("Successfully uploaded pending sessions")
            SessionResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to upload pending sessions")
            SessionResult.Error(e.message ?: "Failed to upload pending sessions")
        }
    }

    // Fetches all session history for a user from Firestore
    suspend fun fetchAllSessions(userId: String): SessionResult<List<Session>> {
        return try {
            Timber.d("Fetching all sessions for user: $userId")
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