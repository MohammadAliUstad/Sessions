package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.notifications.NotificationService
import com.yugentech.sessions.notifications.NotificationViewModel
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepository
import com.yugentech.sessions.notifications.notificationRepository.NotificationRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val notificationModule = module {

    single {
        NotificationService(androidContext())
    }

    single<NotificationRepository> {
        NotificationRepositoryImpl(
            notificationService = get()
        )
    }

    viewModel {
        NotificationViewModel(
            notificationRepository = get()
        )
    }
}