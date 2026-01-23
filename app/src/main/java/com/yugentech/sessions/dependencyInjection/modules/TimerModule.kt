package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.timer.TimerDatastore
import com.yugentech.sessions.timer.TimerService
import com.yugentech.sessions.timer.TimerViewModel
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import com.yugentech.sessions.timer.timerRepository.TimerRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

val timerModule = module {

    single(named("timerScope")) {
        Timber.d("Creating Timer external CoroutineScope")
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    single {
        TimerDatastore(get(named("timer")))
    }

    // Core timer engine (inject scope)
    single {
        TimerService(
            scope = get(named("timerScope"))
        )
    }

    // Repository with stateIn (like AlertsRepository pattern)
    single<TimerRepository> {
        TimerRepositoryImpl(
            timerService = get(),
            externalScope = get(named("timerScope"))
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