package com.yugentech.sessions.di.module

import com.yugentech.sessions.alerts.datastore.AlertsDataStore
import com.yugentech.sessions.alerts.repository.AlertsRepository
import com.yugentech.sessions.alerts.repository.AlertsRepositoryImpl
import com.yugentech.sessions.alerts.service.BackgroundService
import com.yugentech.sessions.alerts.service.HapticService
import com.yugentech.sessions.alerts.service.SoundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

// Koin module defining dependencies for the alerts and sound system
val alertsModule = module {

    // Provides the service for playing system sound effects
    single {
        SoundService(androidContext())
    }

    // Provides the service for controlling device vibration
    single {
        HapticService(androidContext())
    }

    // Provides the service for managing background ambient audio
    single {
        BackgroundService(androidContext())
    }

    // Provides the preferences manager for alert settings using a named DataStore
    single {
        AlertsDataStore(
            dataStore = get(named("alerts"))
        )
    }

    // Provides the main repository that coordinates sounds, haptics, and preferences
    single<AlertsRepository> {
        AlertsRepositoryImpl(
            alertsDataStore = get(),
            hapticService = get(),
            soundService = get(),
            backgroundSoundService = get(),
            externalScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            timerRepository = get()
        )
    }
}