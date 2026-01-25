package com.yugentech.sessions.alerts.alertsRepository

import android.view.View
import com.yugentech.sessions.alerts.BackgroundSoundService
import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import com.yugentech.sessions.alerts.alertsDatastore.AlertsPreferences
import com.yugentech.sessions.alerts.models.AlertsConfiguration
import com.yugentech.sessions.alerts.models.BackgroundSound
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

// Implementation orchestrating sounds, haptics, and background ambience
class AlertsRepositoryImpl(
    externalScope: CoroutineScope,
    private val timerRepository: TimerRepository,
    private val alertsPreferences: AlertsPreferences,
    private val hapticService: HapticService,
    private val soundService: SoundService,
    private val backgroundSoundService: BackgroundSoundService
) : AlertsRepository {

    // Hot flow keeping the latest alert configuration active
    override val alertConfiguration: StateFlow<AlertsConfiguration> =
        alertsPreferences.alertConfiguration
            .stateIn(
                scope = externalScope,
                started = SharingStarted.Eagerly,
                initialValue = AlertsConfiguration()
            )

    // Helper to get the currently selected background sound ID from timer state
    private val currentSoundId: String?
        get() = timerRepository.timerState.value.timerConfig.activeBackgroundSoundId

    // Handles logic when a focus session starts
    override fun onFocusStart(view: View?) {
        Timber.d("onFocusStart triggered")
        playStartAlert(view)
        startBackgroundSound()
    }

    // Stops background sound when timer is paused
    override fun onFocusPause(view: View?) {
        Timber.d("onFocusPause triggered")
        stopBackgroundSound()
    }

    // Handles transition to break mode, fading audio and playing alert
    override fun onBreakStart(view: View?) {
        Timber.d("onBreakStart triggered")
        backgroundSoundService.fadeToBreakMode()
        playStopAlert(view)
    }

    // Handles stopping a session completely
    override fun onFocusStop(view: View?) {
        Timber.d("onSessionStop triggered")
        playStopAlert(view)
        stopBackgroundSound()
    }

    // cleans up resources when leaving the screen
    override fun onLeave(view: View?) {
        Timber.d("onLeave triggered")
        stop()
    }

    // Previews a sound without saving it as the active choice
    override fun playPreview(soundId: String?) {
        val sound = BackgroundSound.fromId(soundId)
        backgroundSoundService.playPreview(sound)
    }

    // Executes haptic feedback if the user has it enabled
    override fun performHaptic(view: View?) {
        if (alertConfiguration.value.hapticsEnabled) {
            try {
                hapticService.performHaptic(view)
            } catch (e: Exception) {
                Timber.e(e, "Failed to perform haptic feedback")
            }
        }
    }

    // Updates sound preference via the preferences manager
    override suspend fun setSoundEnabled(enabled: Boolean) {
        alertsPreferences.setSoundEnabled(enabled)
    }

    // Updates haptics preference via the preferences manager
    override suspend fun setHapticsEnabled(enabled: Boolean) {
        alertsPreferences.setHapticsEnabled(enabled)
    }

    // Plays the active background sound if one is selected
    private fun startBackgroundSound() {
        val backgroundSound = BackgroundSound.fromId(currentSoundId)

        if (backgroundSound != BackgroundSound.NONE) {
            Timber.d("Starting background ambience from TimerConfig: ${backgroundSound.id}")
            backgroundSoundService.play(backgroundSound)
        } else {
            Timber.d("Background ambience skipped (None selected)")
        }
    }

    // Helper to stop background audio
    private fun stopBackgroundSound() {
        backgroundSoundService.stop()
    }

    // Helper to release audio resources
    private fun stop() {
        backgroundSoundService.release()
    }

    // Plays the start sound and haptic based on configuration
    private fun playStartAlert(view: View?) {
        val alertsConfiguration = alertConfiguration.value
        try {
            if (alertsConfiguration.soundEnabled) soundService.playStartAlert()
            if (alertsConfiguration.hapticsEnabled) hapticService.performHaptic(view)
        } catch (e: Exception) {
            Timber.e(e, "Failed to play session start alert")
        }
    }

    // Plays the stop sound and haptic based on configuration
    private fun playStopAlert(view: View?) {
        val alertsConfiguration = alertConfiguration.value
        try {
            if (alertsConfiguration.soundEnabled) soundService.playStopAlert()
            if (alertsConfiguration.hapticsEnabled) hapticService.performHaptic(view)
        } catch (e: Exception) {
            Timber.e(e, "Failed to play session stop alert")
        }
    }
}