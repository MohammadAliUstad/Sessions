package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.LoginViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

val viewModelModule = module {

    // ViewModel for the main dashboard and session management
    viewModel {
        Timber.v("Initializing HomeViewModel")
        HomeViewModel(
            sessionsRepository = get(),
            userRepository = get()
        )
    }

    // ViewModel for user profile display and editing
    viewModel {
        Timber.v("Initializing ProfileViewModel")
        ProfileViewModel(
            userRepository = get(),
            sessionsRepository = get(),
            alertsRepository = get()
        )
    }

    // ViewModel handling authentication flows
    viewModel {
        Timber.v("Initializing LoginViewModel")
        LoginViewModel(
            authRepository = get(),
            userRepository = get(),
            syncPreferences = get(),
            userPreferences = get()
        )
    }

    // ViewModel for app configuration and preferences
    viewModel {
        Timber.v("Initializing SettingsViewModel")
        SettingsViewModel(
            alertsRepository = get()
        )
    }
}