package com.yugentech.sessions.timer.repository

import com.yugentech.sessions.sessions.model.Session
import com.yugentech.sessions.sessions.repository.SessionsRepository
import com.yugentech.sessions.timer.datastore.TimerDatastore
import com.yugentech.sessions.timer.service.TimerService
import com.yugentech.sessions.timer.effect.TimerEffect
import com.yugentech.sessions.timer.state.TimerMode
import com.yugentech.sessions.timer.state.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

// Orchestrator between DataStore config and the TimerService countdown engine.
// Config observation now lives here so TimerService stays storage-agnostic.
class TimerRepositoryImpl(
    private val timerService: TimerService,
    private val timerDatastore: TimerDatastore,
    private val sessionsRepository: SessionsRepository,
    private val externalScope: CoroutineScope
) : TimerRepository {

    init {
        // Observe config changes and push them into the engine.
        // TimerService.updateConfig() handles the logic of whether to interrupt
        // a running countdown or just update non-duration fields in place.
        externalScope.launch {
            timerDatastore.timerConfig.collect { config ->
                timerService.updateConfig(config)
            }
        }

        // Centralized effect handling for persistence
        externalScope.launch {
            timerService.timerEffects.collect { effect ->
                when (effect) {
                    is TimerEffect.FocusCompleted -> {
                        if (effect.durationSeconds >= 60) {
                            saveSession(effect.durationSeconds)
                        }
                    }
                    is TimerEffect.EndGoalReached -> {
                        if (effect.durationSeconds >= 60) {
                            saveSession(effect.durationSeconds)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    override val timerState: StateFlow<TimerState> = timerService.timerState
        .stateIn(
            scope = externalScope,
            started = SharingStarted.Eagerly,
            initialValue = TimerState()
        )

    override val timerEffects: SharedFlow<TimerEffect> = timerService.timerEffects

    override fun start() {
        timerService.start()
    }

    override fun pause() {
        timerService.pause()
    }

    override fun skipToNext() {
        timerService.skipToNext()
    }

    override fun reset() {
        timerService.reset()
    }

    override fun saveCurrentSession() {
        val state = timerState.value
        val timeSpentSeconds = (state.totalTime - state.currentTime).toInt()
        
        if (state.currentMode == TimerMode.Focus && timeSpentSeconds >= 60) {
            saveSession(timeSpentSeconds)
        }
    }

    private fun saveSession(durationSeconds: Int) {
        val state = timerState.value
        val task = state.timerConfig.sessionTask.ifBlank { "Focus Session" }
        
        externalScope.launch {
            val session = Session(
                sessionId = UUID.randomUUID().toString(),
                duration = durationSeconds,
                timestamp = System.currentTimeMillis(),
                sessionTask = task
            )
            Timber.i("Saving session: $session")
            sessionsRepository.saveSession(session)
        }
    }

    override fun updateSessionTask(newTask: String) {
        externalScope.launch {
            timerDatastore.updateSessionTask(newTask)
        }
    }

    override fun updateFocusDuration(duration: Int) {
        externalScope.launch {
            timerDatastore.updateFocusDuration(duration)
        }
    }

    override fun updateShortBreakDuration(duration: Int) {
        externalScope.launch {
            timerDatastore.updateShortBreakDuration(duration)
        }
    }

    override fun updateLongBreakAndTargetSets(duration: Int, sets: Int) {
        externalScope.launch {
            timerDatastore.updateLongBreakAndTargetSets(duration, sets)
        }
    }

    override fun updateActiveBackgroundSound(soundId: String?) {
        externalScope.launch {
            timerDatastore.updateActiveBackgroundSound(soundId)
        }
    }

    override fun toggleAmbientSound(enabled: Boolean) {
        externalScope.launch {
            timerDatastore.toggleAmbientSound(enabled)
        }
    }
}