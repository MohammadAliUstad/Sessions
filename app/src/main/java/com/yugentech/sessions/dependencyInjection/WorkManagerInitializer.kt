package com.yugentech.sessions.dependencyInjection

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import com.yugentech.sessions.notifications.scheduled.KoinWorkerFactory

class WorkManagerInitializer(
    private val context: Context,
    private val workerFactory: KoinWorkerFactory
) {
    fun initialize() {
        val config = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(context, config)
    }
}