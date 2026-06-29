package com.yugentech.sessions.di.module

import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.user.datastore.UserDataStore
import com.yugentech.sessions.user.service.UserService
import com.yugentech.sessions.user.repository.UserRepository
import com.yugentech.sessions.user.repository.UserRepositoryImpl
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
        UserDataStore(get(named("user")))
    }

    // Repository that syncs user profile data between local storage and Firestore
    single<UserRepository> {
        UserRepositoryImpl(
            userDao = get(),
            userService = get(),
            syncDataStore = get(),
            authRepository = get()
        )
    }
}