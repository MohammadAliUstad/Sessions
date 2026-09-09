package com.yugentech.sessions.templates.repository

import com.yugentech.sessions.room.daos.TemplateDao
import com.yugentech.sessions.room.entities.TemplateEntity
import com.yugentech.sessions.templates.model.Template
import com.yugentech.sessions.timer.config.TimerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class TemplateRepositoryImpl(
    private val templateDao: TemplateDao
) : TemplateRepository {

    override fun getAllTemplates(): Flow<List<Template>> =
        templateDao.getAllTemplates().map { list -> list.map { it.toTemplate() } }

    override suspend fun getByName(name: String): Template? =
        templateDao.getByName(name)?.toTemplate()

    override suspend fun saveFromConfig(name: String, config: TimerConfig) {
        val existing = templateDao.getByName(name)
        val entity = TemplateEntity.fromConfig(name, config).copy(id = existing?.id ?: 0)
        templateDao.upsert(entity)
        Timber.d("Template saved: $name (id=${entity.id})")
    }

    override suspend fun delete(id: Long) {
        templateDao.deleteById(id)
        Timber.d("Template deleted: id=$id")
    }
}
