package com.yugentech.sessions.di.module

import com.yugentech.sessions.theme.datastore.ThemeDataStore
import com.yugentech.sessions.theme.viewmodel.ThemeViewModel
import com.yugentech.sessions.theme.repository.ThemeRepository
import com.yugentech.sessions.theme.repository.ThemeRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

// Koin module defining dependencies for UI theming
val themeModule = module {

    // Service that saves and retrieves theme preferences
    single {
        ThemeDataStore(
            dataStore = get(named("theme"))
        )
    }

    // Repository acting as a source of truth for the app's current theme
    single<ThemeRepository> {
        ThemeRepositoryImpl(
            dataStore = get(),
            externalScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    // ViewModel for managing theme selection logic in the UI
    viewModel {
        Timber.v("Initializing ThemeViewModel")
        ThemeViewModel(
            repository = get(),
            alertsRepository = get()
        )
    }
}