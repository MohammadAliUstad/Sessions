package com.yugentech.sessions.timer.timerRepository

import com.yugentech.sessions.timer.TimerService

class TimerRepositoryImpl(
    private val timerService: TimerService
) : TimerRepository {

    override val isRunning = timerService.isRunning
    override val currentTime = timerService.currentTime

    override fun setDuration(seconds: Int) {
        timerService.setDuration(seconds)
    }

    override fun startTimer() {
        timerService.start()
    }

    override fun stopTimer() {
        timerService.stop()
    }

    override fun resetTimer() {
        timerService.reset()
    }

    override fun getElapsedTime(): Int {
        return timerService.getElapsedTime()
    }

    override fun onTimerComplete(listener: () -> Unit) {
        timerService.setOnTimerCompleteListener(listener)
    }
}