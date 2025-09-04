package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.LoginViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import com.yugentech.sessions.viewModels.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        HomeViewModel(
            sessionsRepository = get(),
            timerRepository = get(),
            alertsRepository = get()
        )
    }

    viewModel {
        ProfileViewModel(
            userRepository = get(),
            sessionsRepository = get()
        )
    }

    viewModel {
        LoginViewModel(
            authRepository = get(),
            userRepository = get()
        )
    }

    viewModel {
        SettingsViewModel(
            alertsManager = get(),
            alertsRepository = get()
        )
    }
}