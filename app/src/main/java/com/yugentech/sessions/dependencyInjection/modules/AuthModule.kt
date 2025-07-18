package com.yugentech.sessions.dependencyInjection.modules

import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yugentech.sessions.authentication.AuthService
import com.yugentech.sessions.authentication.AuthViewModel
import com.yugentech.sessions.authentication.authRepository.AuthRepository
import com.yugentech.sessions.authentication.authRepository.AuthRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {

    single { FirebaseAuth.getInstance() }

    single { FirebaseFirestore.getInstance() }

    single { Identity.getSignInClient(androidContext()) }

    single { AuthService(get(), get(), get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    viewModel { AuthViewModel(get()) }
}