package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.dependencyInjection.NotificationPrefsDataStore
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.notifications.active.ActiveService
import com.yugentech.sessions.notifications.active.ActiveServiceManager
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepositoryImpl
import com.yugentech.sessions.notifications.scheduled.ReminderManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val notificationModule = module {

    single(createdAtStart = true) {
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

    single {
        NotificationPrefsDataStore(get(named("notification")))
    }

    single<NotificationRepository> {
        NotificationRepositoryImpl(
            activeServiceManager = get(),
            reminderManager = get()
        )
    }

    factory {
        NotificationsViewModel(
            notificationRepository = get(),
            notificationPrefsDataStore = get()
        )
    }
}