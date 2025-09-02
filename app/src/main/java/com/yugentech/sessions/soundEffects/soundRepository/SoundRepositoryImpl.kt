package com.yugentech.sessions.soundEffects.soundRepository

import com.yugentech.sessions.soundEffects.SoundService
import com.yugentech.sessions.soundEffects.alertDatastore.AlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SoundRepositoryImpl(
    private val soundService: SoundService,
    private val alertRepository: AlertRepository
) : SoundRepository {

    override suspend fun playStart() {
        if (alertRepository.alertConfiguration.first().soundEnabled) {
            soundService.playStart()
        }
    }

    override suspend fun playCompletion() {
        if (alertRepository.alertConfiguration.first().soundEnabled) {
            soundService.playCompletion()
        }
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        val currentConfig = alertRepository.alertConfiguration.first()
        alertRepository.setConfig(
            currentConfig.copy(soundEnabled = enabled)
        )
    }

    override fun isSoundEnabled(): Flow<Boolean> =
        alertRepository.alertConfiguration.map { it.soundEnabled }
}