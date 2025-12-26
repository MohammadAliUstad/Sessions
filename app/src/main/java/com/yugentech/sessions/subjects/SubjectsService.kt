package com.yugentech.sessions.subjects

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.models.Subject
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class SubjectsService(
    private val firestore: FirebaseFirestore
) {

    private fun userSubjectsCollection(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("subjects")

    // Uploads new locally created subjects to Firestore
    suspend fun uploadPendingSubjects(userId: String, subjects: List<Subject>): SubjectResult<Unit> {
        return try {
            if (subjects.isEmpty()) {
                return SubjectResult.Success(Unit)
            }

            Timber.i("Uploading ${subjects.size} pending subjects for user: $userId")
            val batch = firestore.batch()
            val subjectsRef = userSubjectsCollection(userId)

            subjects.forEach { subject ->
                val docRef = subjectsRef.document(subject.subjectId)
                batch.set(docRef, subject.toMap())
            }

            batch.commit().await()
            Timber.i("Successfully uploaded pending subjects")
            SubjectResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to upload pending subjects")
            SubjectResult.Error(e.message ?: "Failed to upload subjects")
        }
    }

    // Fetches all subjects from Firestore to sync with local DB
    suspend fun fetchAllSubjects(userId: String): SubjectResult<List<Subject>> {
        return try {
            Timber.d("Fetching all subjects for user: $userId")
            val snapshot = userSubjectsCollection(userId).get().await()
            val subjects = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Subject::class.java)
            }
            Timber.i("Fetched ${subjects.size} subjects from cloud")
            SubjectResult.Success(subjects)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch subjects from cloud")
            SubjectResult.Error(e.message ?: "Failed to fetch subjects")
        }
    }
}