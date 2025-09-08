package com.yugentech.sessions.dependencyInjection.modules

import android.content.Context
import androidx.work.WorkerParameters
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.notifications.active.ActiveServiceManager
import com.yugentech.sessions.notifications.active.ActiveService
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepositoryImpl
import com.yugentech.sessions.notifications.scheduled.KoinWorkerFactory
import com.yugentech.sessions.notifications.scheduled.ReminderManager
import com.yugentech.sessions.notifications.scheduled.ReminderWorker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationModule = module {

    single {
        ActiveService(
            context = androidContext()
        )
    }

    single {
        ActiveServiceManager(
            context = androidContext()
        )
    }

    single {
        ReminderManager(
            context = androidContext()
        )
    }


    single<NotificationRepository> {
        NotificationRepositoryImpl(
            activeService = get(),
            activeServiceManager = get(),
            reminderManager = get()
        )
    }

    factory {
        NotificationsViewModel(
            notificationRepository = get()
        )
    }

    single {
        KoinWorkerFactory()
    }

    factory { (appContext: Context, params: WorkerParameters) ->
        ReminderWorker(
            context = appContext,
            params = params,
            activeService = get()
        )
    }
}