package com.yugentech.sessions.timer.timerRepository

import android.util.Log
import com.yugentech.sessions.timer.TimerService

class TimerRepositoryImpl(
    private val timerService: TimerService
) : TimerRepository {

    companion object {
        private const val TAG = "TimerRepositoryImpl"
    }

    override val isRunning = timerService.isRunning
    override val currentTime = timerService.currentTime

    override fun setDuration(seconds: Int) {
        Log.d(TAG, "setDuration() called with duration = $seconds seconds")
        timerService.setDuration(seconds)
    }

    override fun startTimer() {
        Log.d(TAG, "startTimer() called")
        timerService.start()
    }

    override fun stopTimer() {
        Log.d(TAG, "stopTimer() called")
        timerService.stop()
    }

    override fun resetTimer() {
        Log.d(TAG, "resetTimer() called")
        timerService.reset()
    }

    override fun getElapsedTime(): Int {
        val elapsed = timerService.getElapsedTime()
        Log.d(TAG, "getElapsedTime() = $elapsed seconds")
        return elapsed
    }

    override fun onTimerComplete(listener: () -> Unit) {
        Log.d(TAG, "onTimerComplete() listener set")
        timerService.setOnTimerCompleteListener {
            Log.d(TAG, "Timer complete callback triggered")
            listener()
        }
    }
}