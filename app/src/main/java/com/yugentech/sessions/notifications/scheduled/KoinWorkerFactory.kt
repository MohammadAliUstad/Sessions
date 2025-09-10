package com.yugentech.sessions.notifications.scheduled

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.yugentech.sessions.notifications.active.ActiveService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KoinWorkerFactory : WorkerFactory(), KoinComponent {

    private val activeService: ActiveService by inject()

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        Log.d("KoinWorkerFactory", "createWorker called for: $workerClassName")

        return when (workerClassName) {
            ReminderWorker::class.java.name -> {
                Log.d("KoinWorkerFactory", "Creating ReminderWorker with Koin DI")
                ReminderWorker(
                    context = appContext,
                    params = workerParameters,
                    activeService = activeService
                )
            }
            else -> {
                Log.d("KoinWorkerFactory", "Unknown worker: $workerClassName, returning null")
                null
            }
        }
    }
}