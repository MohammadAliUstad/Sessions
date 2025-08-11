package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.timer.TimerService
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import com.yugentech.sessions.timer.timerRepository.TimerRepositoryImpl
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    single {
        TimerService(
            coroutineScope = get()
        )
    }

    single<TimerRepository> {
        TimerRepositoryImpl(
            timerService = get()
        )
    }

    viewModel {
        HomeViewModel(
            sessionsRepository = get(),
            timerRepository = get()
        )
    }

    viewModel {
        ProfileViewModel(
            userRepository = get(),
            sessionsRepository = get()
        )
    }
}