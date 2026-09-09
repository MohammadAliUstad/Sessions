package com.yugentech.sessions.templates.repository

import com.yugentech.sessions.templates.model.Template
import com.yugentech.sessions.timer.config.TimerConfig
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    fun getAllTemplates(): Flow<List<Template>>
    suspend fun getByName(name: String): Template?
    suspend fun saveFromConfig(name: String, config: TimerConfig)
    suspend fun delete(id: Long)
}
