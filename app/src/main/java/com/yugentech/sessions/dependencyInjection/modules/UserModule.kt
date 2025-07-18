package com.yugentech.sessions.dependencyInjection.modules

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.user.UserService
import com.yugentech.sessions.user.userRepository.UserRepository
import com.yugentech.sessions.user.userRepository.UserRepositoryImpl
import org.koin.dsl.module

val userModule = module {

    single { FirebaseFirestore.getInstance() }

    single { UserService(get()) }

    single<UserRepository> { UserRepositoryImpl(get()) }
}