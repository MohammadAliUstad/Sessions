package com.yugentech.sessions.dependencyInjection.modules

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.session.SessionService
import com.yugentech.sessions.session.SessionViewModel
import com.yugentech.sessions.session.sessionRepository.SessionRepository
import com.yugentech.sessions.session.sessionRepository.SessionRepositoryImpl
import com.yugentech.sessions.session.sessionUtils.TimerManager
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sessionModule = module {

    single { FirebaseFirestore.getInstance() }

    single { SessionService(get()) }

    single { TimerManager() }

    single<SessionRepository> { SessionRepositoryImpl(get()) }

    viewModel { SessionViewModel(get(), get()) }
}