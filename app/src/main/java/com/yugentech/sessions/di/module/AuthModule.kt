package com.yugentech.sessions.di.module

import com.google.android.gms.auth.api.identity.Identity
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

    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get()
        )
    }
}