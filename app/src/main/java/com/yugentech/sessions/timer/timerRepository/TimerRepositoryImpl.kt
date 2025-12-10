package com.yugentech.sessions.timer.timerRepository

import com.yugentech.sessions.timer.TimerService
import com.yugentech.sessions.timer.TimerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class TimerRepositoryImpl(
    private val timerService: TimerService
) : TimerRepository {

    // Persistent storage for User ID to survive ViewModel recreation
    private var sessionUserId: String? = null

    // MAPPING: Convert the Enum (RUNNING/PAUSED/IDLE) to the Boolean your UI expects.
    // isRunning is TRUE only if the underlying state is explicitly RUNNING.
    override val isRunning: Flow<Boolean> = timerService.timerState.map { state ->
        state == TimerState.RUNNING
    }

    // Pass-through the current time directly
    override val currentTime = timerService.currentTime

    override fun setSessionUserId(userId: String) {
        Timber.d("Setting session User ID: $userId")
        this.sessionUserId = userId
    }

    override fun getSessionUserId(): String? {
        return sessionUserId
    }

    override fun setDuration(seconds: Int) {
        Timber.v("Setting timer duration: $seconds seconds")
        timerService.setDuration(seconds)
    }

    override fun startTimer() {
        Timber.i("Starting timer")
        timerService.start()
    }

    override fun stopTimer() {
        Timber.i("Stopping timer")
        timerService.stop()
    }

    override fun resetTimer() {
        Timber.i("Resetting timer")
        timerService.reset()
    }

    override fun getElapsedTime(): Int {
        val elapsed = timerService.getElapsedTime()
        Timber.v("Elapsed time queried: $elapsed seconds")
        return elapsed
    }

    override fun onTimerComplete(listener: (Int) -> Unit) {
        Timber.d("Registering onTimerComplete listener")
        timerService.setOnTimerCompleteListener { duration ->
            Timber.i("Timer completed. Total duration: $duration")
            listener(duration)
        }
    }
}