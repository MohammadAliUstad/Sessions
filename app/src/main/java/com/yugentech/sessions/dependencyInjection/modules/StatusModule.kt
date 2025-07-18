package com.yugentech.sessions.dependencyInjection.modules

import com.google.firebase.database.FirebaseDatabase
import com.yugentech.sessions.status.StatusService
import com.yugentech.sessions.status.StatusViewModel
import com.yugentech.sessions.status.statusRepository.StatusRepository
import com.yugentech.sessions.status.statusRepository.StatusRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val statusModule = module {

    single { FirebaseDatabase.getInstance() }

    single { StatusService(get()) }

    single<StatusRepository> { StatusRepositoryImpl(get()) }

    viewModel { StatusViewModel(get()) }
}