package com.yugentech.sessions.user

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserService(
    firestore: FirebaseFirestore,
) {
    private val usersRef = firestore.collection("users")

    fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val listener = usersRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val users = snapshot?.documents?.mapNotNull { doc ->
                User.fromMap(doc.id, doc.data ?: emptyMap())
            }.orEmpty()

            trySend(users).isSuccess
        }

        awaitClose { listener.remove() }
    }
}