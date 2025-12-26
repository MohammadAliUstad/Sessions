package com.yugentech.sessions.subjects

import com.yugentech.sessions.models.Subject
import com.yugentech.sessions.room.daos.SubjectDao
import com.yugentech.sessions.room.entities.SubjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class SubjectsRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val subjectsService: SubjectsService
) : SubjectsRepository {

    override fun getSubjects(userId: String): Flow<List<Subject>> {
        // We map entities to domain models here so the UI stays clean
        return subjectDao.getSubjectsFlow(userId).map { entities ->
            entities.map { it.toSubject() }
        }
    }

    override suspend fun addSubject(subject: Subject) {
        // 1. Save locally first (Offline-first approach)
        val entity =
            SubjectEntity.fromSubject(subject) // Add 'pendingSync = true' if you add that column later
        subjectDao.insertSubject(entity)
        Timber.d("Added subject locally: ${subject.name}")

        // 2. Try to sync immediately (Optional, or wait for syncSubjects call)
        syncSubjects(subject.userId)
    }

    override suspend fun deleteSubject(subjectId: String) {
        // 1. Delete locally immediately
        subjectDao.deleteSubject(subjectId)
        Timber.d("Deleted subject locally: $subjectId")
    }

    override suspend fun syncSubjects(userId: String) {
        // Step A: Upload local pending subjects (if you implement pendingSync logic)
        // For now, let's assume we push everything or use a specific logic.
        // A simple approach is fetching all local subjects and ensuring they exist in cloud.
        // Or simpler: Just fetch from cloud to update local.

        // Step B: Fetch latest from Cloud
        when (val result = subjectsService.fetchAllSubjects(userId)) {
            is SubjectResult.Success -> {
                val entities = result.data.map { SubjectEntity.fromSubject(it) }
                subjectDao.insertSubjects(entities) // Uses OnConflictStrategy.REPLACE
                Timber.d("Synced ${entities.size} subjects from cloud")
            }

            is SubjectResult.Error -> {
                Timber.e("Sync failed: ${result.message}")
            }
        }
    }
}