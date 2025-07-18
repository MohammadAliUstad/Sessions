package com.yugentech.sessions.session

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.yugentech.sessions.models.Session
import com.yugentech.sessions.session.sessionUtils.SessionResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SessionService(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveSession(userId: String, session: Session): SessionResult<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("sessions")
                .add(session.toMap())
                .await()

            SessionResult.Success(Unit)
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to save session")
        }
    }

    suspend fun updateTotalTime(userId: String, additionalSeconds: Int): SessionResult<Unit> {
        return try {
            val userRef = firestore
                .collection("users")
                .document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentTotal = snapshot.getLong("totalTimeStudied") ?: 0
                transaction.update(userRef, "totalTimeStudied", currentTotal + additionalSeconds)
            }.await()

            SessionResult.Success(Unit)
        } catch (e: Exception) {
            SessionResult.Error(e.message ?: "Failed to update total time")
        }
    }

    fun getSessions(userId: String): Flow<List<Session>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = firestore
            .collection("users")
            .document(userId)
            .collection("sessions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val sessions = snapshot?.documents?.mapNotNull { doc ->
                    Session.fromMap(doc.data ?: emptyMap())
                } ?: emptyList()

                trySend(sessions).isSuccess
            }

        awaitClose { listenerRegistration.remove() }
    }

    fun getTotalTime(userId: String): Flow<Long> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val totalTime = snapshot?.getLong("totalTimeStudied") ?: 0L
                trySend(totalTime).isSuccess
            }

        awaitClose { listener.remove() }
    }
}