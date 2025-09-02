package com.yugentech.sessions.dependencyInjection.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yugentech.sessions.soundEffects.SoundService
import com.yugentech.sessions.soundEffects.alertDatastore.AlertRepository
import com.yugentech.sessions.soundEffects.soundRepository.SoundRepository
import com.yugentech.sessions.soundEffects.soundRepository.SoundRepositoryImpl
import com.yugentech.sessions.timer.TimerService
import com.yugentech.sessions.timer.timerRepository.TimerRepository
import com.yugentech.sessions.timer.timerRepository.TimerRepositoryImpl
import com.yugentech.sessions.viewModels.HomeViewModel
import com.yugentech.sessions.viewModels.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// Extension property for DataStore
private val Context.alertDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "alert_prefs"
)

val viewModelModule = module {

    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    single { TimerService(get()) }

    single<TimerRepository> { TimerRepositoryImpl(get()) }

    single<DataStore<Preferences>> { androidContext().alertDataStore }

    single { AlertRepository(get()) }

    single { SoundService(context = get()) }

    single<SoundRepository> {
        SoundRepositoryImpl(
            soundService = get(),
            alertRepository = get()
        )
    }

    viewModel {
        HomeViewModel(
            sessionsRepository = get(),
            timerRepository = get(),
            soundRepository = get()
        )
    }

    viewModel {
        ProfileViewModel(
            userRepository = get(),
            sessionsRepository = get()
        )
    }
}