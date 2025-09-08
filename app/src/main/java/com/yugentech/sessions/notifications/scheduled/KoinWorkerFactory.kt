package com.yugentech.sessions.notifications.scheduled

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import kotlin.reflect.KClass

class KoinWorkerFactory : WorkerFactory() {

    private val koin = GlobalContext.get()

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return try {
            val clazz: KClass<out ListenableWorker> =
                Class.forName(workerClassName).asSubclass(ListenableWorker::class.java).kotlin
            koin.getOrNull(clazz) { parametersOf(appContext, workerParameters) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
