package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.notifications.NotificationPrefsDataStore
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.notifications.active.ActiveManager
import com.yugentech.sessions.notifications.NotificationService
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepositoryImpl
import com.yugentech.sessions.notifications.scheduled.ReminderManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

val notificationModule = module {

    // Initializes NotificationService and creates channels immediately on startup
    single(createdAtStart = true) {
        Timber.d("Initializing NotificationService and Channels")
        NotificationService(androidContext()).apply {
            createNotificationChannels()
        }
    }

    // Provides access to notification preferences
    single {
        NotificationPrefsDataStore(
            dataStore = get(named("notification"))
        )
    }

    // Manages the Active Session foreground service
    single {
        ActiveManager(
            context = androidContext()
        )
    }

    // Manages Scheduled Alarm logic
    single {
        ReminderManager(
            context = androidContext()
        )
    }

    // Repository coordinating active and scheduled notifications
    single<NotificationRepository> {
        NotificationRepositoryImpl(
            activeManager = get(),
            reminderManager = get()
        )
    }

    // ViewModel for notification settings UI
    viewModel {
        NotificationsViewModel(
            notificationRepository = get(),
            notificationPrefsDataStore = get()
        )
    }
}