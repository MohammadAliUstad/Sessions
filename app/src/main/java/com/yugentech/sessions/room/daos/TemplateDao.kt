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

    @Upsert
    suspend fun upsert(template: TemplateEntity)

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteById(id: Long)
}
