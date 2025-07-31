package com.yugentech.sessions.status

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.yugentech.sessions.status.statusRepository.StatusRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StatusCleanupService : Service(), KoinComponent {

    private var userId: String? = null
    private val statusRepository: StatusRepository by inject()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = intent?.getStringExtra("USER_ID")
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        userId?.let { uid ->
            coroutineScope.launch {
                statusRepository.setStudyStatus(uid, false)
            }
        }

        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        userId?.let { uid ->
            coroutineScope.launch {
                statusRepository.setStudyStatus(uid, false)
            }
        }

        coroutineScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}