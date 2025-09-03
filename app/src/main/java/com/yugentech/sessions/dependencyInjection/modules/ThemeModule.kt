package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.theme.ThemeService
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.themeRepository.ThemeRepository
import com.yugentech.sessions.theme.themeRepository.ThemeRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val themeModule = module {

    single {
        ThemeService(
            dataStore = get(named("theme"))
        )
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(
            service = get()
        )
    }

    viewModel {
        ThemeViewModel(
            repository = get()
        )
    }
}