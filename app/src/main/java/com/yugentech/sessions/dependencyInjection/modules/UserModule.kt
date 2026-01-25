package com.yugentech.sessions.dependencyInjection.modules

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.user.UserPreferences
import com.yugentech.sessions.user.UserService
import com.yugentech.sessions.user.userRepository.UserRepository
import com.yugentech.sessions.user.userRepository.UserRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

// Koin module defining dependencies for user profile management
val userModule = module {

    // Provides the Firestore instance
    single { FirebaseFirestore.getInstance() }

    // Service for direct Firestore user document operations
    single {
        UserService(
            firestore = get()
        )
    }

    // Manages local preferences specific to the user
    single {
        UserPreferences(get(named("user")))
    }

    // Repository that syncs user profile data between local storage and Firestore
    single<UserRepository> {
        UserRepositoryImpl(
            userDao = get(),
            userService = get(),
            syncPreferences = get()
        )
    }
}