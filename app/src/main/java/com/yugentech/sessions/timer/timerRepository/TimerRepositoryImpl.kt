package com.yugentech.sessions.timer.timerRepository

import com.yugentech.sessions.timer.TimerConfig
import com.yugentech.sessions.timer.TimerService
import com.yugentech.sessions.timer.TimerState
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class TimerRepositoryImpl(
    private val timerService: TimerService
) : TimerRepository {

    // Keep the User ID here so it doesn't get lost if the ViewModel clears
    private var sessionUserId: String? = null

    // Direct pipe to the Service's state
    override val timerState: StateFlow<TimerState> = timerService.timerState

    override fun updateConfig(config: TimerConfig) {
        Timber.d("Updating timer config: Focus=${config.focusDurationMinutes}m")
        timerService.updateConfig(config)
    }

    override fun startTimer() {
        timerService.startTimer()
    }

    override fun stopTimer() {
        timerService.stopTimer()
    }

    override fun stopAndResetTimer() {
        timerService.stopAndReset()
    }

    override fun setOnTimerCompleteListener(listener: (Int) -> Unit) {
        timerService.setOnTimerCompleteListener(listener)
    }

    override fun setSessionUserId(userId: String) {
        this.sessionUserId = userId
    }

    override fun getSessionUserId(): String? {
        return sessionUserId
    }
}