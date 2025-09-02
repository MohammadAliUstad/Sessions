package com.yugentech.sessions.soundEffects.soundRepository

import kotlinx.coroutines.flow.Flow

interface SoundRepository {
    suspend fun playStart()
    suspend fun playCompletion()
    suspend fun setSoundEnabled(enabled: Boolean)
    fun isSoundEnabled(): Flow<Boolean>
}