package com.yugentech.sessions.alerts.alertsDatastore

import android.view.View
import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AlertsRepository(
    alertsManager: AlertsManager,
    private val hapticService: HapticService,
    private val soundService: SoundService
) {
    private var isSoundEnabled: Boolean = true
    private var isHapticsEnabled: Boolean = true

    init {
        alertsManager.alertConfiguration.onEach { config ->
            isSoundEnabled = config.soundEnabled
            isHapticsEnabled = config.hapticsEnabled
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun playSessionStartAlert(view: View? = null) {
        if (isSoundEnabled) {
            soundService.playSessionStartSound()
        }
        if (isHapticsEnabled) {
            hapticService.performHaptic(view)
        }
    }

    fun playSessionStopAlert(view: View? = null) {
        if (isSoundEnabled) {
            soundService.playSessionStopSound()
        }
        if (isHapticsEnabled) {
            hapticService.performHaptic(view)
        }
    }

    fun performHaptic(view: View? = null) {
        if (isHapticsEnabled) {
            hapticService.performHaptic(view)
        }
    }
}