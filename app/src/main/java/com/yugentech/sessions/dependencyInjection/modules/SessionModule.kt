package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.sessions.SyncPreferences
import com.yugentech.sessions.sessions.SessionsService
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepositoryImpl
import com.yugentech.sessions.utils.BillingManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import timber.log.Timber

// Koin module defining dependencies for session management and data syncing
val sessionModule = module {

    // Service for performing direct Firestore operations related to sessions
    single {
        SessionsService(
            firestore = get()
        )
    }

    // Manages Google Play Billing interactions
    single {
        BillingManager(androidContext())
    }

    // Helper class to track last sync timestamps for data consistency
    single {
        Timber.d("Initializing SyncPreferences")
        SyncPreferences(
            context = androidContext()
        )
    }

    // Repository that synchronizes local database records with remote Firestore data
    single<SessionsRepository> {
        SessionsRepositoryImpl(
            sessionsDao = get(),
            sessionService = get(),
            syncPreferences = get(),
            authRepository = get()
        )
    }
}