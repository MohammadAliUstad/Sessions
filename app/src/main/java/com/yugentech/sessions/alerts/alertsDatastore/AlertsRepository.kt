package com.yugentech.sessions.alerts.alertsDatastore

import android.view.View
import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

// Coordinates the logic between user settings and the actual hardware services (Sound/Haptics)
class AlertsRepository(
    alertsManager: AlertsManager,
    private val hapticService: HapticService,
    private val soundService: SoundService
) {
    private var isSoundEnabled: Boolean = true
    private var isHapticsEnabled: Boolean = true

    // Observes settings changes in real-time to update local flags
    init {
        alertsManager.alertConfiguration.onEach { config ->
            Timber.v("Alert configuration updated: Sound=${config.soundEnabled}, Haptics=${config.hapticsEnabled}")
            isSoundEnabled = config.soundEnabled
            isHapticsEnabled = config.hapticsEnabled
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    // Triggers alerts for session start, handling potential hardware errors gracefully
    fun playSessionStartAlert(view: View? = null) {
        Timber.i("Triggering Session Start Alert")
        try {
            if (isSoundEnabled) soundService.playSessionStartSound()
            if (isHapticsEnabled) hapticService.performHaptic(view)
        } catch (e: Exception) {
            Timber.e(e, "Failed to play session start alert")
        }
    }

    // Triggers alerts for session stop
    fun playSessionStopAlert(view: View? = null) {
        Timber.i("Triggering Session Stop Alert")
        try {
            if (isSoundEnabled) soundService.playSessionStopSound()
            if (isHapticsEnabled) hapticService.performHaptic(view)
        } catch (e: Exception) {
            Timber.e(e, "Failed to play session stop alert")
        }
    }

    // Triggers a standalone haptic feedback effect
    fun performHaptic(view: View? = null) {
        if (isHapticsEnabled) {
            try {
                hapticService.performHaptic(view)
            } catch (e: Exception) {
                Timber.e(e, "Failed to perform haptic feedback")
            }
        }
    }
}