package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.sessions.SyncPreferences
import com.yugentech.sessions.sessions.SessionsService
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import timber.log.Timber

val sessionModule = module {

    // Service handling direct Firestore operations for sessions
    single {
        SessionsService(
            firestore = get()
        )
    }

    // Manages local timestamps to sync data between device and cloud
    single {
        Timber.d("Initializing SyncPreferences")
        SyncPreferences(
            context = androidContext()
        )
    }

    // Repository coordinating local database, remote Firestore, and sync logic
    single<SessionsRepository> {
        SessionsRepositoryImpl(
            sessionsDao = get(),
            sessionService = get(),
            syncPreferences = get()
        )
    }
}