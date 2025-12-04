package com.yugentech.sessions.dependencyInjection.modules

import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.authentication.AuthService
import com.yugentech.sessions.authentication.authRepository.AuthRepository
import com.yugentech.sessions.authentication.authRepository.AuthRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authModule = module {

    // Singleton instance of Firebase Auth
    single {
        FirebaseAuth.getInstance()
    }

    // Singleton instance of Firestore
    single {
        FirebaseFirestore.getInstance()
    }

    // Google Sign-In Client for One Tap authentication
    single {
        Identity.getSignInClient(androidContext())
    }

    // Service handling lower-level auth operations
    single {
        AuthService(
            auth = get(),
            oneTapClient = get()
        )
    }

    // Repository implementation exposing auth domain logic
    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get()
        )
    }
}