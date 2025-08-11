package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.sessions.SessionPreferences
import com.yugentech.sessions.sessions.SessionsService
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sessionModule = module {

    single {
        SessionsService(
            firestore = get()
        )
    }

    single {
        SessionPreferences(
            context = androidContext()
        )
    }

    single<SessionsRepository> {
        SessionsRepositoryImpl(
            sessionsDao = get(),
            sessionService = get(),
            sessionPreferences = get()
        )
    }
}