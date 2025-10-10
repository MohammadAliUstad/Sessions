package com.yugentech.sessions.notifications.scheduled

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent

class KoinWorkerFactory : WorkerFactory(), KoinComponent {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when (workerClassName) {
            ReminderWorker::class.java.name -> {
                ReminderWorker(
                    context = appContext,
                    params = workerParameters
                )
            }
            else -> {
                null
            }
        }
    }
}