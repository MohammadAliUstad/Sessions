package com.yugentech.sessions.alerts.repository

import android.view.View
import com.yugentech.sessions.alerts.service.BackgroundService
import com.yugentech.sessions.alerts.service.HapticService
import com.yugentech.sessions.alerts.service.SoundService
import com.yugentech.sessions.alerts.datastore.AlertsDataStore
import com.yugentech.sessions.alerts.model.AlertsConfiguration
import com.yugentech.sessions.alerts.model.BackgroundSound
import com.yugentech.sessions.timer.repository.TimerRepository
import com.yugentech.sessions.timer.state.TimerMode // Added Import
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

// Implementation orchestrating sounds, haptics, and background ambience
class AlertsRepositoryImpl(
    externalScope: CoroutineScope,
    private val timerRepository: TimerRepository,
    private val alertsDataStore: AlertsDataStore,
    private val hapticService: HapticService,
    private val soundService: SoundService,
    private val backgroundSoundService: BackgroundService
) : AlertsRepository {

    init {
        // Observe timer state to handle background sound toggling and mode changes
        externalScope.launch {
            timerRepository.timerState.collect { state ->
                handleBackgroundSoundState(state)
            }
        }
    }

    private var lastIsRunning: Boolean? = null
    private var lastAmbientState: Boolean? = null
    private var lastSoundId: String? = null
    private var lastMode: TimerMode? = null

    private fun handleBackgroundSoundState(state: com.yugentech.sessions.timer.state.TimerState) {
        val config = state.timerConfig
        val isRunning = state.isTimerRunning
        val isAmbientEnabled = config.isAmbientEnabled
        val soundId = config.activeBackgroundSoundId
        val mode = state.currentMode

        // Only react if something relevant changed
        if (lastIsRunning == isRunning &&
            lastAmbientState == isAmbientEnabled && 
            lastSoundId == soundId && 
            lastMode == mode) return

        lastIsRunning = isRunning
        lastAmbientState = isAmbientEnabled
        lastSoundId = soundId
        lastMode = mode

        if (isRunning) {
            if (isAmbientEnabled && soundId != null && soundId != "none") {
                startBackgroundSound()
            } else {
                stopBackgroundSound()
            }
        } else {
            // Ensure sound stops whenever the timer is not running
            stopBackgroundSound()
        }
    }

    // Hot flow keeping the latest alert configuration active
    override val alertConfiguration: StateFlow<AlertsConfiguration> =
        alertsDataStore.alertConfiguration
            .stateIn(
                scope = externalScope,
                started = SharingStarted.Eagerly,
                initialValue = AlertsConfiguration()
            )



    // Handles logic when a focus session starts
    override fun onFocusStart(view: View?) {
        Timber.d("onFocusStart triggered")
        playStartAlert(view)
        startBackgroundSound()
    }

    // Stops background sound when timer is paused
    override fun onFocusPause(view: View?) {
        Timber.d("onFocusPause triggered")
        playStopAlert(view)
        stopBackgroundSound()
    }

    // Handles transition to break mode, fading audio and playing alert
    override fun onBreakStart(view: View?) {
        Timber.d("onBreakStart triggered")
        backgroundSoundService.fadeToBreakMode()
        playStopAlert(view)
    }

    override fun onGoalReached(view: View?) {
        Timber.d("onGoalReached triggered")
        backgroundSoundService.stop()
        playGoalReachedAlert(view)
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
        alertsDataStore.setSoundEnabled(enabled)
    }

    // Updates haptics preference via the preferences manager
    override suspend fun setHapticsEnabled(enabled: Boolean) {
        alertsDataStore.setHapticsEnabled(enabled)
    }

    // Plays the active background sound if one is selected
    private fun startBackgroundSound() {
        val currentState = timerRepository.timerState.value
        val config = currentState.timerConfig
        
        if (!config.isAmbientEnabled) {
            stopBackgroundSound()
            return
        }

        val backgroundSound = BackgroundSound.fromId(config.activeBackgroundSoundId)
        val isBreakMode = currentState.currentMode == TimerMode.ShortBreak ||
                currentState.currentMode == TimerMode.LongBreak

        if (backgroundSound != BackgroundSound.NONE) {
            Timber.d("Starting background ambience: ${backgroundSound.id} (Break Mode: $isBreakMode)")
            backgroundSoundService.play(backgroundSound, isBreakMode)
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

    private fun playGoalReachedAlert(view: View?) {
        val alertsConfiguration = alertConfiguration.value
        try {
            if (alertsConfiguration.soundEnabled) soundService.playGoalReachedAlert()
            if (alertsConfiguration.hapticsEnabled) hapticService.performCompletionHaptic(view)
        } catch (e: Exception) {
            Timber.e(e, "Failed to play goal reached alert")
        }
    }
}