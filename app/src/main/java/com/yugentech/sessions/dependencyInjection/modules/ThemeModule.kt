package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.theme.ThemeService
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.themeRepository.ThemeRepository
import com.yugentech.sessions.theme.themeRepository.ThemeRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

// Koin module defining dependencies for UI theming
val themeModule = module {

    // Service that saves and retrieves theme preferences
    single {
        ThemeService(
            dataStore = get(named("theme"))
        )
    }

    // Repository acting as a source of truth for the app's current theme
    single<ThemeRepository> {
        ThemeRepositoryImpl(
            service = get()
        )
    }

    // ViewModel for managing theme selection logic in the UI
    viewModel {
        Timber.v("Initializing ThemeViewModel")
        ThemeViewModel(
            repository = get()
        )
    }
}