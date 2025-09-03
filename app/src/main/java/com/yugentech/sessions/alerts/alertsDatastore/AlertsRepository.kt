package com.yugentech.sessions.alerts.alertsDatastore

import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import kotlinx.coroutines.flow.first

class AlertsRepository(
    private val alertsManager: AlertsManager,
    private val hapticService: HapticService,
    private val soundService: SoundService
) {
    suspend fun playSessionStartAlert() {
        val config = alertsManager.alertConfiguration.first()

        if (config.soundEnabled) {
            soundService.playSessionStartSound()
        }

        if (config.hapticsEnabled) {
            hapticService.performSessionStartHaptic()
        }
    }

    suspend fun playSessionStopAlert() {
        val config = alertsManager.alertConfiguration.first()

        if (config.soundEnabled) {
            soundService.playSessionStopSound()
        }

        if (config.hapticsEnabled) {
            hapticService.performSessionStopHaptic()
        }
    }

    suspend fun playButtonTapAlert() {
        val config = alertsManager.alertConfiguration.first()

        if (config.hapticsEnabled) {
            hapticService.performButtonTapHaptic()
        }
    }
}