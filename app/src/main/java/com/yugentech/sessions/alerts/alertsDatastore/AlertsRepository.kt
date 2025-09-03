package com.yugentech.sessions.alerts.alertsDatastore

import android.view.View
import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import kotlinx.coroutines.flow.first

class AlertsRepository(
    private val alertsManager: AlertsManager,
    private val hapticService: HapticService,
    private val soundService: SoundService
) {
    suspend fun playSessionStartAlert(view: View? = null) {
        val config = alertsManager.alertConfiguration.first()

        if (config.soundEnabled) {
            soundService.playSessionStartSound()
        }

        if (config.hapticsEnabled) {
            hapticService.performHaptic(view)
        }
    }

    suspend fun playSessionStopAlert(view: View? = null) {
        val config = alertsManager.alertConfiguration.first()

        if (config.soundEnabled) {
            soundService.playSessionStopSound()
        }

        if (config.hapticsEnabled) {
            hapticService.performHaptic(view)
        }
    }

    suspend fun performHaptic(view: View? = null) {
        val config = alertsManager.alertConfiguration.first()

        if (config.hapticsEnabled) {
            hapticService.performHaptic(view)
        }
    }
}