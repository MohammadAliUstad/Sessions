package com.yugentech.sessions.di.module

import com.yugentech.sessions.timer.datastore.TimerDatastore
import com.yugentech.sessions.timer.repository.TimerRepository
import com.yugentech.sessions.timer.repository.TimerRepositoryImpl
import com.yugentech.sessions.timer.service.TimerService
import com.yugentech.sessions.timer.viewmodel.TimerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

// Koin module defining dependencies for the core timer functionality
val timerModule = module {

    // Provides a dedicated scope for timer operations that survives UI lifecycle changes
    single(named("timerScope")) {
        Timber.d("Creating Timer external CoroutineScope")
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    // Manages persistence of timer configuration state
    single {
        TimerDatastore(get(named("timer")))
    }

    // Core logic engine for the countdown timer
    single {
        TimerService(
            coroutineScope = get(named("timerScope")),
        )
    }

    // Repository exposing timer state and control functions to the rest of the app
    single<TimerRepository> {
        TimerRepositoryImpl(
            timerService = get(),
            timerDatastore = get(),
            sessionsRepository = get(),
            externalScope = get(named("timerScope"))
        )
    }

    // ViewModel mediating between the timer repository and the UI
    viewModel {
        Timber.v("Initializing TimerViewModel")
        TimerViewModel(
            timerRepository = get(),
            alertsRepository = get(),
            notificationRepository = get()
        )
    }
}