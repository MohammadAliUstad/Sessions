package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.sessions.SessionsService
import com.yugentech.sessions.sessions.SessionsViewModel
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepository
import com.yugentech.sessions.sessions.sessionsRepository.SessionsRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sessionModule = module {

    single {
        SessionsService(
            firestore = get()
        )
    }

    single<SessionsRepository> {
        SessionsRepositoryImpl(
            sessionsDao = get(),
            sessionService = get(),
            userRepository = get()
        )
    }

    viewModel {
        SessionsViewModel(
            sessionsRepository = get()
        )
    }
}