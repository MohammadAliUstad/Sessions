package com.yugentech.sessions.notification.active

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.yugentech.sessions.utils.AppConstants
import org.koin.core.component.KoinComponent
import timber.log.Timber

// Helper to launch and control the ActiveForeground service via intents.
// Data is no longer passed as extras — ActiveForeground reads everything
// live from TimerRepository on its own.
class ActiveNotificationManager(
    private val context: Context
) : KoinComponent {

    // Launches the foreground service. The service reads initial timer state
    // directly from TimerRepository, which is already populated by the time
    // TimerViewModel calls this (after timerRepository.start() returns).
    fun startActiveNotification() {
        Timber.d("Starting ActiveForeground service")
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = AppConstants.ACTION_START_SESSION
        }
        ContextCompat.startForegroundService(context, intent)
    }

    // Sends a stop intent to the service to end the foreground session.
    fun stopActiveNotification() {
        Timber.d("Stopping ActiveForeground service")
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = AppConstants.ACTION_STOP_SESSION
        }
        context.startService(intent)
    }

    // Sends a finish intent to the service to end and play completion sound.
    fun finishActiveNotification() {
        Timber.d("Finishing ActiveForeground service")
        val intent = Intent(context, ActiveForeground::class.java).apply {
            action = AppConstants.ACTION_FINISH_SESSION
        }
        context.startService(intent)
    }
}