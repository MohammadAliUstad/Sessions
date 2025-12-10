package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.theme.ThemeService
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.themeRepository.ThemeRepository
import com.yugentech.sessions.theme.themeRepository.ThemeRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

val themeModule = module {

    // Core service managing theme state persistence
    single {
        ThemeService(
            dataStore = get(named("theme"))
        )
    }

    // Repository abstracting theme data operations
    single<ThemeRepository> {
        ThemeRepositoryImpl(
            service = get()
        )
    }

    // ViewModel handling theme UI logic
    viewModel {
        Timber.v("Initializing ThemeViewModel")
        ThemeViewModel(
            repository = get()
        )
    }
}