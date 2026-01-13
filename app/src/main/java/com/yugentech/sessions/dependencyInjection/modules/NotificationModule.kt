package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.notifications.NotificationDataStore
import com.yugentech.sessions.notifications.NotificationsViewModel
import com.yugentech.sessions.notifications.active.ActiveNotificationManager
import com.yugentech.sessions.notifications.NotificationService
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepositoryImpl
import com.yugentech.sessions.notifications.scheduled.ReminderNotificationManager
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
        NotificationDataStore(
            dataStore = get(named("notification"))
        )
    }

    // Manages the Active Session foreground service
    single {
        ActiveNotificationManager(
            context = androidContext()
        )
    }

    // Manages Scheduled Alarm logic
    single {
        ReminderNotificationManager(
            context = androidContext()
        )
    }

    // Repository coordinating active and scheduled notifications
    single<NotificationRepository> {
        NotificationRepositoryImpl(
            activeNotificationManager = get(),
            reminderNotificationManager = get()
        )
    }

    // ViewModel for notification settings UI
    viewModel {
        NotificationsViewModel(
            notificationRepository = get(),
            notificationDataStore = get()
        )
    }
}