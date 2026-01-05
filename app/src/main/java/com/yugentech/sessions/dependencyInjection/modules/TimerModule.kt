package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.timer.TimerService
import com.yugentech.sessions.timer.TimerViewModel
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import com.yugentech.sessions.timer.timerRepository.TimerRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

val timerModule = module {

    // Global scope for the timer to survive configuration changes
    single<CoroutineScope> {
        Timber.d("Creating Timer CoroutineScope")
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    // Core service managing the countdown logic
    single {
        TimerService(
            coroutineScope = get()
        )
    }

    // Repository exposing timer state to the UI
    single<TimerRepository> {
        TimerRepositoryImpl(
            timerService = get()
        )
    }

    viewModel {
        Timber.v("Initializing TimerViewModel")
        TimerViewModel(
            timerRepository = get(),
            sessionsRepository = get(),
            alertsRepository = get(),
            notificationRepository = get()
        )
    }
}