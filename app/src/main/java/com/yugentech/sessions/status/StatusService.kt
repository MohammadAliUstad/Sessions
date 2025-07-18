package com.yugentech.sessions.status

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class StatusService(
    firebaseDatabase: FirebaseDatabase
) {
    private val studyStatusRef = firebaseDatabase.getReference("studyStatus")

    suspend fun setStudyStatus(userId: String, isStudying: Boolean): Boolean {
        return try {
            studyStatusRef
                .child(userId)
                .setValue(isStudying)
                .await()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getStudyStatus(userId: String): Boolean {
        return try {
            val snapshot =
                studyStatusRef
                    .child(userId)
                    .get()
                    .await()
            snapshot.getValue(Boolean::class.java) == true
        } catch (_: Exception) {
            false
        }
    }

    fun getAllStatuses(): Flow<Map<String, Boolean>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val statusMap = snapshot.children.mapNotNull { child ->
                    val key = child.key ?: return@mapNotNull null
                    key to (child.getValue(Boolean::class.java) == true)
                }.toMap()

                trySend(statusMap).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        studyStatusRef.addValueEventListener(listener)
        awaitClose { studyStatusRef.removeEventListener(listener) }
    }
}