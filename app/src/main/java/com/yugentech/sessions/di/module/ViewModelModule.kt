package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.LoginViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

// Koin module aggregating ViewModels for major app screens
val viewModelModule = module {

    // ViewModel for the home dashboard, handling session data display
    viewModel {
        Timber.v("Initializing HomeViewModel")
        HomeViewModel(
            sessionsRepository = get(),
            userRepository = get()
        )
    }

    // ViewModel for viewing and editing the user profile
    viewModel {
        Timber.v("Initializing ProfileViewModel")
        ProfileViewModel(
            userRepository = get(),
            sessionsRepository = get(),
            alertsRepository = get()
        )
    }

    // ViewModel managing the login and registration flows
    viewModel {
        Timber.v("Initializing LoginViewModel")
        LoginViewModel(
            authRepository = get(),
            userRepository = get(),
            syncPreferences = get(),
            userPreferences = get()
        )
    }

    // ViewModel for general application settings
    viewModel {
        Timber.v("Initializing SettingsViewModel")
        SettingsViewModel(
            alertsRepository = get()
        )
    }
}