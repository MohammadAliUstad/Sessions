package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.status.StatusService
import com.yugentech.sessions.status.statusRepository.StatusRepository
import com.yugentech.sessions.status.statusRepository.StatusRepositoryImpl
import com.yugentech.sessions.user.UserService
import com.yugentech.sessions.user.userRepository.UserRepository
import com.yugentech.sessions.user.userRepository.UserRepositoryImpl
import com.yugentech.sessions.viewModels.LeaderboardViewModel
import org.koin.core.module.dsl.viewModel

import org.koin.dsl.module

val leaderboardModule = module {

    single { UserService(get()) }

    single<UserRepository> { UserRepositoryImpl(get()) }

    single { StatusService(get()) }

    single<StatusRepository> { StatusRepositoryImpl(get()) }

    viewModel { LeaderboardViewModel(get(), get()) }
}