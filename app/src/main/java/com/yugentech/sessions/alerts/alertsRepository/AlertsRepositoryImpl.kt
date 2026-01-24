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

class AlertsRepositoryImpl(
    externalScope: CoroutineScope,
    private val timerRepository: TimerRepository,
    private val alertsPreferences: AlertsPreferences,
    private val hapticService: HapticService,
    private val soundService: SoundService,
    private val backgroundSoundService: BackgroundSoundService
) : AlertsRepository {

    override val alertConfiguration: StateFlow<AlertsConfiguration> =
        alertsPreferences.alertConfiguration
            .stateIn(
                scope = externalScope,
                started = SharingStarted.Eagerly,
                initialValue = AlertsConfiguration()
            )

    private val currentSoundId: String?
        get() = timerRepository.timerState.value.timerConfig.activeBackgroundSoundId

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
        backgroundSoundService.fadeToBreakMode()
        playStopAlert(view)
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
            try {
                hapticService.performHaptic(view)
            } catch (e: Exception) {
                Timber.e(e, "Failed to perform haptic feedback")
            }
        }
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        alertsPreferences.setSoundEnabled(enabled)
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        alertsPreferences.setHapticsEnabled(enabled)
    }

    private fun startBackgroundSound() {
        // Pull the sound ID directly from the TimerRepository's current state
        val backgroundSound = BackgroundSound.fromId(currentSoundId)

        if (backgroundSound != BackgroundSound.NONE) {
            Timber.d("Starting background ambience from TimerConfig: ${backgroundSound.id}")
            backgroundSoundService.play(backgroundSound)
        } else {
            Timber.d("Background ambience skipped (None selected)")
        }
    }

    private fun stopBackgroundSound() {
        backgroundSoundService.stop()
    }

    private fun playStartAlert(view: View?) {
        val alertsConfiguration = alertConfiguration.value
        try {
            if (alertsConfiguration.soundEnabled) soundService.playStartAlert()
            if (alertsConfiguration.hapticsEnabled) hapticService.performHaptic(view)
        } catch (e: Exception) {
            Timber.e(e, "Failed to play session start alert")
        }
    }

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