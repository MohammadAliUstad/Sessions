package com.yugentech.sessions.notifications.active

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.yugentech.sessions.notifications.Notification
import com.yugentech.sessions.notifications.NotificationType
import org.koin.android.ext.android.inject

class ActiveForeground : Service() {

    companion object {
        private const val TAG = "ActiveForeground"
        private const val ACTION_START_SESSION = "START_SESSION"
        private const val ACTION_STOP_SESSION = "STOP_SESSION"
        private const val ACTION_UPDATE_SESSION = "UPDATE_SESSION"
    }

    private val activeService: ActiveService by inject()
    private var isSessionActive = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "🚀 ActiveForeground service command: ${intent?.action}")

        when (intent?.action) {
            ACTION_START_SESSION -> startSession(intent)
            ACTION_STOP_SESSION -> stopSession()
            ACTION_UPDATE_SESSION -> updateSession(intent)
            else -> startSession(intent) // Default behavior
        }

        // Return START_STICKY to restart if killed while session active
        return if (isSessionActive) START_STICKY else START_NOT_STICKY
    }

    private fun startSession(intent: Intent?) {
        isSessionActive = true

        val title = intent?.getStringExtra("title") ?: "Study Session"
        val message = intent?.getStringExtra("message") ?: "Session in progress"
        val totalMinutes = intent?.getIntExtra("totalMinutes", 0)
        val remainingMinutes = intent?.getIntExtra("remainingMinutes", 0)

        val notification = Notification(
            id = ActiveService.ACTIVE_NOTIFICATION_ID,
            type = NotificationType.ACTIVE,
            title = title,
            message = message,
            isOngoing = true,
            totalMinutes = totalMinutes,
            timeRemainingMinutes = remainingMinutes
        )

        val androidNotification = activeService.buildNotification(notification)

        val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        } else {
            0
        }

        ServiceCompat.startForeground(
            this,
            ActiveService.ACTIVE_NOTIFICATION_ID,
            androidNotification,
            serviceType
        )

        Log.d(TAG, "✅ Session started - Service in foreground")
    }

    private fun updateSession(intent: Intent?) {
        if (!isSessionActive) return

        // Update notification with new time/progress
        startSession(intent) // Reuse start logic for updates
        Log.d(TAG, "🔄 Session updated")
    }

    private fun stopSession() {
        isSessionActive = false
        Log.d(TAG, "🛑 Session stopped - Service will stop")

        // Hide notification and stop service
        activeService.hideNotification(ActiveService.ACTIVE_NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "📱 App removed from recent apps")

        // User manually removed app - stop everything
        stopSession()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.d(TAG, "🔄 Service destroyed")
        isSessionActive = false
        activeService.hideNotification(ActiveService.ACTIVE_NOTIFICATION_ID)
        super.onDestroy()
    }

    // Let system know if service can be safely killed
    override fun onLowMemory() {
        super.onLowMemory()
        if (!isSessionActive) {
            Log.d(TAG, "💾 Low memory - no active session, allowing kill")
            stopSelf()
        }
    }
}