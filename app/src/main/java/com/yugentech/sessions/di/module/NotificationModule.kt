package com.yugentech.sessions.di.module

import com.yugentech.sessions.notification.service.NotificationService
import com.yugentech.sessions.notification.datastore.NotificationDataStore
import com.yugentech.sessions.notification.active.ActiveNotificationManager
import com.yugentech.sessions.notification.repository.NotificationRepository
import com.yugentech.sessions.notification.repository.NotificationRepositoryImpl
import com.yugentech.sessions.notification.reminders.NotificationScheduler
import com.yugentech.sessions.notification.reminders.ReminderManager
import org.koin.android.ext.koin.androidContext
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

    // Books the daily reminder and the nudges as alarms
    single {
        NotificationScheduler(
            context = androidContext()
        )
    }

    // The reminders, set up the same way as in Quill. SessionsApp calls onAppStart() at every
    // start, which books them from the saved settings.
    single {
        ReminderManager(
            context = androidContext(),
            scheduler = get(),
            notificationDataStore = get(),
            sessionsRepository = get()
        )
    }

    // Repository for the active timer notification
    single<NotificationRepository> {
        NotificationRepositoryImpl(
            activeNotificationManager = get()
        )
    }

}
