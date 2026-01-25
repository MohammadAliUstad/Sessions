package com.yugentech.sessions.dependencyInjection.modules

import com.yugentech.sessions.alerts.HapticService
import com.yugentech.sessions.alerts.SoundService
import com.yugentech.sessions.alerts.alertsDatastore.AlertsPreferences
import com.yugentech.sessions.alerts.alertsRepository.AlertsRepository
import com.yugentech.sessions.alerts.BackgroundSoundService
import com.yugentech.sessions.alerts.alertsRepository.AlertsRepositoryImpl
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
        BackgroundSoundService(androidContext())
    }

    // Provides the preferences manager for alert settings using a named DataStore
    single {
        AlertsPreferences(
            dataStore = get(named("alerts"))
        )
    }

    // Provides the main repository that coordinates sounds, haptics, and preferences
    single<AlertsRepository> {
        AlertsRepositoryImpl(
            alertsPreferences = get(),
            hapticService = get(),
            soundService = get(),
            backgroundSoundService = get(),
            externalScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            timerRepository = get()
        )
    }
}