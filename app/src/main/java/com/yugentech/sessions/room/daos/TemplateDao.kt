package com.yugentech.sessions.room.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.yugentech.sessions.room.entities.TemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {

    @Query("SELECT * FROM templates ORDER BY name ASC")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): TemplateEntity?

    @Query("SELECT * FROM templates WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TemplateEntity?

    /** A one-off read of every template, for merging with the cloud copy. */
    @Query("SELECT * FROM templates")
    suspend fun getAllOnce(): List<TemplateEntity>

    @Upsert
    suspend fun upsert(template: TemplateEntity)

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Empties the table: on sign-out, or when a different account signs in. */
    @Query("DELETE FROM templates")
    suspend fun deleteAll()
}
