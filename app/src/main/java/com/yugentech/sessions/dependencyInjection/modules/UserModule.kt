package com.yugentech.sessions.dependencyInjection.modules

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.user.UserService
import com.yugentech.sessions.user.userRepository.UserRepository
import com.yugentech.sessions.user.userRepository.UserRepositoryImpl
import org.koin.dsl.module

val userModule = module {

    // Singleton instance of Firestore
    single { FirebaseFirestore.getInstance() }

    // Service handling direct Firestore user operations
    single {
        UserService(
            firestore = get()
        )
    }

    // Repository mediating user data between remote and local sources
    single<UserRepository> {
        UserRepositoryImpl(
            userDao = get(),
            userService = get(),
            syncPreferences = get()
        )
    }
}