package com.yugentech.sessions.timer.repository

import com.yugentech.sessions.timer.datastore.TimerDatastore
import com.yugentech.sessions.timer.service.TimerService
import com.yugentech.sessions.timer.effect.TimerEffect
import com.yugentech.sessions.timer.state.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimerRepositoryImpl(
    private val timerService: TimerService,
    private val timerDatastore: TimerDatastore,
    private val externalScope: CoroutineScope
) : TimerRepository {

    // Expose the service's state flow, kept alive by the external scope
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
}