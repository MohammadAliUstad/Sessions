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

// Koin module defining dependencies for the notification system
val notificationModule = module {

    // Initializes the notification service and creates channels immediately on app startup
    single(createdAtStart = true) {
        Timber.d("Initializing NotificationService and Channels")
        NotificationService(androidContext()).apply {
            createNotificationChannels()
        }
    }

    // Provides access to persistent notification settings
    single {
        NotificationDataStore(
            dataStore = get(named("notification"))
        )
    }

    // Manages the persistent foreground notification for active timers
    single {
        ActiveNotificationManager(
            context = androidContext()
        )
    }

    // Manages scheduling and cancelling of future alarm reminders
    single {
        ReminderNotificationManager(
            context = androidContext()
        )
    }

    // Repository that coordinates both active timer notifications and scheduled reminders
    single<NotificationRepository> {
        NotificationRepositoryImpl(
            activeNotificationManager = get(),
            reminderNotificationManager = get()
        )
    }

    // ViewModel for the notification settings screen
    viewModel {
        NotificationsViewModel(
            notificationRepository = get(),
            notificationDataStore = get()
        )
    }
}