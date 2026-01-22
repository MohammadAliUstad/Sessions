package com.yugentech.sessions.alerts.alertsRepository

import android.view.View
import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import com.yugentech.sessions.alerts.models.AlertsConfiguration
import com.yugentech.sessions.alerts.alertsDatastore.AlertsPreferences
import com.yugentech.sessions.alerts.models.BackgroundSound
import com.yugentech.sessions.alerts.BackgroundSoundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class AlertsRepositoryImpl(
    private val alertsPreferences: AlertsPreferences,
    private val hapticService: HapticService,
    private val soundService: SoundService,
    private val backgroundSoundService: BackgroundSoundService,
    externalScope: CoroutineScope
) : AlertsRepository {

    override val alertConfiguration: StateFlow<AlertsConfiguration> = alertsPreferences.alertConfiguration
        .stateIn(
            scope = externalScope,
            started = SharingStarted.Eagerly,
            initialValue = AlertsConfiguration()
        )

    override fun onFocusStart(view: View?) {
        Timber.d("onFocusStart triggered")
        playStartAlert(view)
        startBackgroundSound()
    }

    override fun onFocusPause(view: View?) {
        Timber.d("onFocusPause triggered")
        stopBackgroundSound()
    }

    override fun onBreakStart(view: View?) {
        Timber.d("onBreakStart triggered")
        playStopAlert(view)
        backgroundSoundService.fadeToBreakMode()
    }

    override fun onFocusStop(view: View?) {
        Timber.d("onSessionStop triggered")
        playStopAlert(view)
        stopBackgroundSound()
    }

    override fun playPreview(soundId: String?) {
        val sound = BackgroundSound.fromId(soundId)
        backgroundSoundService.playPreview(sound)
    }

    override fun performHaptic(view: View?) {
        if (alertConfiguration.value.hapticsEnabled) {
            Timber.v("Performing manual haptic feedback")
            try {
                hapticService.performHaptic(view)
            } catch (e: Exception) {
                Timber.e(e, "Failed to perform haptic feedback")
            }
        } else {
            Timber.v("Haptic feedback skipped (Disabled in settings)")
        }
    }

    override suspend fun setBackgroundSound(soundId: String?) {
        Timber.d("Setting background sound to: $soundId")
        val sound = BackgroundSound.fromId(soundId)
        alertsPreferences.setBackgroundSound(sound)
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        Timber.d("Repository: Setting sound enabled to $enabled")
        alertsPreferences.setSoundEnabled(enabled)
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        Timber.d("Repository: Setting haptics enabled to $enabled")
        alertsPreferences.setHapticsEnabled(enabled)
    }

    private fun startBackgroundSound() {
        val backgroundSound = alertConfiguration.value.backgroundSound

        if (backgroundSound != BackgroundSound.NONE) {
            Timber.d("Starting background ambience: ${backgroundSound.id}")
            backgroundSoundService.play(backgroundSound)
        } else {
            Timber.d("Background ambience skipped (None selected)")
        }
    }

    private fun stopBackgroundSound() {
        Timber.d("Stopping background ambience")
        backgroundSoundService.stop()
    }

    private fun playStartAlert(view: View?) {
        val alertsConfiguration = alertConfiguration.value
        Timber.i("Triggering Session Start Alert (Sound: ${alertsConfiguration.soundEnabled}, Haptics: ${alertsConfiguration.hapticsEnabled})")

        try {
            if (alertsConfiguration.soundEnabled) soundService.playStartAlert()
            if (alertsConfiguration.hapticsEnabled) hapticService.performHaptic(view)
        } catch (e: Exception) {
            Timber.e(e, "Failed to play session start alert")
        }
    }

    private fun playStopAlert(view: View?) {
        val alertsConfiguration = alertConfiguration.value
        Timber.i("Triggering Session Stop Alert (Sound: ${alertsConfiguration.soundEnabled}, Haptics: ${alertsConfiguration.hapticsEnabled})")

        try {
            if (alertsConfiguration.soundEnabled) soundService.playStopAlert()
            if (alertsConfiguration.hapticsEnabled) hapticService.performHaptic(view)
        } catch (e: Exception) {
            Timber.e(e, "Failed to play session stop alert")
        }
    }
}