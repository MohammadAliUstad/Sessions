package com.yugentech.sessions.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yugentech.sessions.room.entities.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    // Used when the user creates a new Subject manually
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    // Used during Sync to save a batch of subjects from Cloud
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    // UI observes this. Because it returns Flow, any database change
    // automatically triggers a UI update (recomposition)
    @Query("SELECT * FROM subjects WHERE userId = :userId ORDER BY name ASC")
    fun getSubjectsFlow(userId: String): Flow<List<SubjectEntity>>

    // Optional: Useful if you want to implement "Delete Subject" later
    @Query("DELETE FROM subjects WHERE subjectId = :subjectId")
    suspend fun deleteSubject(subjectId: String)
}