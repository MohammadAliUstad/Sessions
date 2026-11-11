package com.yugentech.sessions.di.module

import com.google.android.gms.auth.api.identity.Identity
import com.yugentech.sessions.auth.guest.GuestBackupManager
import com.yugentech.sessions.auth.guest.GuestDataManager
import com.yugentech.sessions.auth.guest.GuestSession
import com.yugentech.sessions.auth.service.AuthService
import com.yugentech.sessions.auth.repository.AuthRepository
import com.yugentech.sessions.auth.repository.AuthRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authModule = module {

    single {
        Identity.getSignInClient(androidContext())
    }

    single {
        AuthService(
            auth = get(),
            oneTapClient = get()
        )
    }

    // Guest mode's local ID: guests have no Firebase account
    single { GuestSession(androidContext()) }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get(),
            guestSession = get()
        )
    }

    // Removes a guest's data when they leave guest mode
    single {
        GuestDataManager(
            sessionsDao = get(),
            userDao = get(),
            templateRepository = get()
        )
    }

    // Saves a guest's data to a file they pick, and merges such a file back in
    single {
        GuestBackupManager(
            contentResolver = androidContext().contentResolver,
            sessionsDao = get(),
            templateDao = get(),
            userDao = get()
        )
    }
}