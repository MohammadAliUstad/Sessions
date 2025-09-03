package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.timer.TimerService
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import com.yugentech.sessions.timer.timerRepository.TimerRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val timerModule = module {

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
}