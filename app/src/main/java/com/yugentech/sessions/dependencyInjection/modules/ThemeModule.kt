package com.yugentech.sessions.dependencyInjection.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yugentech.sessions.theme.ThemeService
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.themeRepository.ThemeRepository
import com.yugentech.sessions.theme.themeRepository.ThemeRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences"
)

val themeModule = module {

    single<DataStore<Preferences>> { androidContext().themeDataStore }

    single { ThemeService(get()) }

    single<ThemeRepository> { ThemeRepositoryImpl(get()) }

    viewModel { ThemeViewModel(get()) }
}