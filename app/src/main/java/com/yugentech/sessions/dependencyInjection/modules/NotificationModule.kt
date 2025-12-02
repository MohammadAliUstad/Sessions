package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.dependencyInjection.NotificationPrefsDataStore
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.notifications.active.ActiveManager
import com.yugentech.sessions.notifications.active.NotificationService
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepositoryImpl
import com.yugentech.sessions.notifications.scheduled.ReminderManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val notificationModule = module {

    single(createdAtStart = true) {
        NotificationService(androidContext()).apply {
            createNotificationChannels()
        }
    }

    single {
        NotificationPrefsDataStore(
            dataStore = get(named("notification"))
        )
    }

    single {
        ActiveManager(
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
            activeManager = get(),
            reminderManager = get()
        )
    }

    viewModel {
        NotificationsViewModel(
            notificationRepository = get(),
            notificationPrefsDataStore = get()
        )
    }
}