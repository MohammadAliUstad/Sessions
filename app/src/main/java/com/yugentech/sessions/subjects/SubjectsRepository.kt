package com.yugentech.sessions.subjects

import com.yugentech.sessions.models.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectsRepository {
    // UI observes this flow to show the list of subjects
    fun getSubjects(userId: String): Flow<List<Subject>>

    // Called when user adds a new preset in the "Task" dialog
    suspend fun addSubject(subject: Subject)

    //Called when user wants to remove a preset
    suspend fun deleteSubject(subjectId: String)

    // Called on app startup to sync data
    suspend fun syncSubjects(userId: String)
}