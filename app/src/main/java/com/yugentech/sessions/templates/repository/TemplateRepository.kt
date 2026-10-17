package com.yugentech.sessions.templates.repository

import com.yugentech.sessions.templates.model.Template
import com.yugentech.sessions.timer.config.TimerConfig
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    fun getAllTemplates(): Flow<List<Template>>
    suspend fun getByName(name: String): Template?
    suspend fun saveFromConfig(name: String, config: TimerConfig)
    suspend fun delete(id: Long)

    /** Merges this device's templates with the user's cloud copy, by name; run at sign-in. */
    suspend fun syncTemplates()

    /** Removes this device's templates (the cloud copy stays); run at sign-out. */
    suspend fun clearLocal()
}
