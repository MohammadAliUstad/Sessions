package com.yugentech.sessions.alerts.alertsDatastore

import android.view.View
import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import com.yugentech.sessions.alerts.alertsDatastore.backgroundSounds.BackgroundSound
import com.yugentech.sessions.alerts.alertsDatastore.backgroundSounds.BackgroundSoundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class AlertsRepository(
    private val alertsManager: AlertsManager,
    private val hapticService: HapticService,
    private val soundService: SoundService,
    private val backgroundSoundService: BackgroundSoundService
) {
    // --- EXPOSE CONFIG FOR VIEWMODEL ---
    val alertConfiguration: Flow<AlertsConfiguration> = alertsManager.alertConfiguration

    private var isSoundEnabled: Boolean = true
    private var isHapticsEnabled: Boolean = true
    private var activeBackgroundSound: BackgroundSound = BackgroundSound.NONE

    init {
        Timber.d("Initializing AlertsRepository")
        alertsManager.alertConfiguration.onEach { config ->
            Timber.v("Alert configuration updated: Sound=${config.soundEnabled}, Haptics=${config.hapticsEnabled}, BgSound=${config.backgroundSound.id}")
            isSoundEnabled = config.soundEnabled
            isHapticsEnabled = config.hapticsEnabled
            activeBackgroundSound = config.backgroundSound
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun onFocusStart(view: View? = null) {
        Timber.d("onFocusStart triggered")
        playSessionStartAlert(view)
        startBackgroundAmbience()
        backgroundSoundService.fadeToFocusMode()
    }

    fun onBreakStart(view: View? = null) {
        Timber.d("onBreakStart triggered")
        playSessionStopAlert(view)
        backgroundSoundService.fadeToBreakMode()
    }

    fun onSessionStop(view: View? = null) {
        Timber.d("onSessionStop triggered")
        playSessionStopAlert(view)
        stopBackgroundAmbience()
    }

    suspend fun setBackgroundSound(soundId: String?) {
        Timber.d("Setting background sound to: $soundId")
        val sound = BackgroundSound.fromId(soundId)
        alertsManager.setBackgroundSound(sound)
    }

    private fun startBackgroundAmbience() {
        if (activeBackgroundSound != BackgroundSound.NONE) {
            Timber.d("Starting background ambience: ${activeBackgroundSound.id}")
            backgroundSoundService.play(activeBackgroundSound)
        } else {
            Timber.d("Background ambience skipped (None selected)")
        }
    }

    private fun stopBackgroundAmbience() {
        Timber.d("Stopping background ambience")
        backgroundSoundService.stop()
    }

    private fun playSessionStartAlert(view: View? = null) {
        Timber.i("Triggering Session Start Alert (Sound: $isSoundEnabled, Haptics: $isHapticsEnabled)")
        try {
            if (isSoundEnabled) soundService.playSessionStartSound()
            if (isHapticsEnabled) hapticService.performHaptic(view)
        } catch (e: Exception) {
            Timber.e(e, "Failed to play session start alert")
        }
    }

    private fun playSessionStopAlert(view: View? = null) {
        Timber.i("Triggering Session Stop Alert (Sound: $isSoundEnabled, Haptics: $isHapticsEnabled)")
        try {
            if (isSoundEnabled) soundService.playSessionStopSound()
            if (isHapticsEnabled) hapticService.performHaptic(view)
        } catch (e: Exception) {
            Timber.e(e, "Failed to play session stop alert")
        }
    }

    fun performHaptic(view: View? = null) {
        if (isHapticsEnabled) {
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
}