package com.yugentech.sessions.templates.repository

import com.yugentech.sessions.auth.repository.AuthRepository
import com.yugentech.sessions.room.daos.TemplateDao
import com.yugentech.sessions.room.entities.TemplateEntity
import com.yugentech.sessions.templates.model.Template
import com.yugentech.sessions.templates.service.TemplateService
import com.yugentech.sessions.timer.config.TimerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Templates live in Room, as before, and are mirrored to the user's account in Firestore: every
 * save and delete goes to the cloud too, and [syncTemplates] merges the two by name when the user
 * signs in, so a fresh install gets its templates back.
 */
class TemplateRepositoryImpl(
    private val templateDao: TemplateDao,
    private val templateService: TemplateService,
    private val authRepository: AuthRepository
) : TemplateRepository {

    private val currentUserId: String?
        get() = authRepository.currentUser

    override fun getAllTemplates(): Flow<List<Template>> =
        templateDao.getAllTemplates().map { list -> list.map { it.toTemplate() } }

    override suspend fun getByName(name: String): Template? =
        templateDao.getByName(name)?.toTemplate()

    override suspend fun saveFromConfig(name: String, config: TimerConfig) {
        val existing = templateDao.getByName(name)
        val entity = TemplateEntity.fromConfig(name, config).copy(id = existing?.id ?: 0)
        templateDao.upsert(entity)
        Timber.d("Template saved: $name (id=${entity.id})")

        currentUserId?.let { templateService.upload(it, entity.toTemplate()) }
    }

    override suspend fun delete(id: Long) {
        // The cloud copy is found by name, so read it before the row goes.
        val name = templateDao.getById(id)?.name
        templateDao.deleteById(id)
        Timber.d("Template deleted: id=$id")

        val userId = currentUserId
        if (userId != null && name != null) templateService.delete(userId, name)
    }

    override suspend fun syncTemplates() {
        val userId = currentUserId ?: return
        try {
            val remote = templateService.fetchAll(userId)
            val local = templateDao.getAllOnce()
            val localNames = local.mapTo(HashSet()) { it.name }
            val remoteNames = remote.mapTo(HashSet()) { it.name }

            // In the cloud but not on this device (a fresh install, or another phone): add it.
            val restored = remote.filter { it.name !in localNames }
            restored.forEach { templateDao.upsert(it.toEntity()) }

            // On this device but not in the cloud (made before syncing existed): upload it.
            val uploaded = local.filter { it.name !in remoteNames }
            uploaded.forEach { templateService.upload(userId, it.toTemplate()) }

            Timber.i("Templates synced: ${restored.size} restored, ${uploaded.size} uploaded")
        } catch (e: Exception) {
            // Offline, say: the next sign-in or app start tries again.
            Timber.e(e, "Template sync failed")
        }
    }

    override suspend fun clearLocal() {
        templateDao.deleteAll()
        Timber.d("Local templates cleared")
    }

    private fun Template.toEntity() = TemplateEntity(
        name = name,
        focusDuration = focusDuration,
        shortBreakDuration = shortBreakDuration,
        longBreakDuration = longBreakDuration,
        targetSets = targetSets,
        setsPerLongBreak = setsPerLongBreak,
        longBreakEnabled = longBreakEnabled,
        activeBackgroundSoundId = activeBackgroundSoundId,
        isAmbientEnabled = isAmbientEnabled
    )
}
