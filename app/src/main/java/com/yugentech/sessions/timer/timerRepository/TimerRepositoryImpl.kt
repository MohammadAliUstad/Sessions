package com.yugentech.sessions.timer.timerRepository

import com.yugentech.sessions.timer.TimerService
import com.yugentech.sessions.timer.states.TimerConfig
import com.yugentech.sessions.timer.states.TimerEffect
import com.yugentech.sessions.timer.states.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class TimerRepositoryImpl(
    private val timerService: TimerService,
    externalScope: CoroutineScope
) : TimerRepository {

    override val timerState: StateFlow<TimerState> = timerService.timerState
        .stateIn(
            scope = externalScope,
            started = SharingStarted.Eagerly,
            initialValue = TimerState()
        )

    override val timerEffects: SharedFlow<TimerEffect> = timerService.timerEffects

    override fun start() {
        Timber.d("Starting timer")
        timerService.start()
    }

    override fun pause() {
        Timber.d("Pausing timer")
        timerService.pause()
    }

    override fun reset() {
        Timber.d("Resetting timer")
        timerService.reset()
    }

    override fun discardSession() {
        Timber.d("Discarding session and clearing completed sets")
        timerService.discardSession()
    }

    override fun updateConfig(config: TimerConfig) {
        Timber.d("Updating config: Focus=${config.focusDuration}m, Target=${config.targetSets} sets")
        timerService.updateConfig(config)
    }
}